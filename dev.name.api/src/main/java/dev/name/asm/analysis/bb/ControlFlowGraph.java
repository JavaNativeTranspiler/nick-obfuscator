package dev.name.asm.analysis.bb;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.processors.methods.DeadcodeProcessor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.collections.set.LinkedFastHashSet;
import dev.name.util.java.Jar;
import lombok.Getter;
import lombok.Setter;

import java.util.BitSet;
import java.util.Objects;

import static dev.name.asm.analysis.bb.Sanity.SANITY;
import static dev.name.asm.analysis.bb.Sanity.sanity;
import static org.objectweb.asm.Opcodes.*;

@Getter
public final class ControlFlowGraph {
    private final Method method;
    private final Jar jar;
    private final Context context;
    @Setter private boolean analyzed;

    private ControlFlowGraph(Method method, Jar jar) {
        this.method = method;
        this.jar = jar;
        this.context = new Context(method);
    }

    public static ControlFlowGraph build(Method method, Jar jar) {
        final ControlFlowGraph cfg = new ControlFlowGraph(method, jar);

        if (method.access.isNative() || method.access.isAbstract() || method.instructions.size() <= 0) {
            cfg.setAnalyzed(true);
            return cfg;
        }

        cfg.analyze();
        return cfg;
    }

    static boolean terminatesMethod(Node node) {
        return node.opcode >= Node.IRETURN && node.opcode <= Node.RETURN || node.opcode == Node.ATHROW;
    }

    static boolean altersFlow(Node node) {
        return terminatesMethod(node) || node instanceof Table || node instanceof Lookup || node instanceof Jump;
    }

    static boolean canFall(Node node) {
        return !terminatesMethod(node) && !(node instanceof Table || node instanceof Lookup || (node instanceof Jump jump && jump.unconditional()));
    }

    private void analyze() {
        if (analyzed) {
            sanity("Attempted to analyze again.");
            return;
        }

        DeadcodeProcessor.process(method);

        Sanity sanity = new Sanity(method);
        sanity.verify();

        populate();
        link();
        exceptions();
        inline();
        dominance();
        loops();
        form();

        analyzed = true;
    }

    private void populate() {
        Instructions instructions = method.instructions;
        context.markLeader(0);

        // let's have every bb exit be a branch and not a fall through
        {
            for (Node node : instructions) {
                if (!(node instanceof Jump jump)) continue;
                if (jump.unconditional()) continue;
                jump.insertAfter(new Label());
            }

            instructions.forEach(node -> node instanceof Label, node -> {
                Label label = (Label) node;
                Node prev = label.previous;

                if (prev == null) {
                    if (instructions.first != label) sanity("magical node");
                    return;
                }

                if (terminatesMethod(prev)) return;
                if (prev instanceof Table || prev instanceof Jump || prev instanceof Lookup) return;
                label.insertBefore(label.jump(GOTO));
            });
        }

        for (Node node : instructions) {
            switch (node.type()) {
                case Node.JUMP -> {
                    Jump jump = (Jump) node;
                    context.markLeader(jump.label.index());
                    if (jump.unconditional()) continue;
                    if (jump.next == null) sanity("Unconditional jump falls through flow");
                    context.markLeader(jump.index() + 1);
                }
                case Node.LOOKUP -> {
                    Lookup lookup = (Lookup) node;
                    context.markLeader(lookup._default.index());
                    for (Label label : lookup.labels) context.markLeader(label.index());
                }
                case Node.TABLE -> {
                    Table table = (Table) node;
                    context.markLeader(table._default.index());
                    for (Label label : table.labels) context.markLeader(label.index());
                }
            }
        }

        for (Block block : method.blocks) {
            context.markLeaders(block.start.index(), block.end.index(), block.handler.index());
        }

        BitSet leaders = context.getLeaders();
        int size = instructions.size();

        for (int i = 0; i < size; i++) {
            if (!leaders.get(i)) continue;

            BasicBlock bb = new BasicBlock(this);
            bb.setLabel(i);

            if (i == 0) {
                bb.setEntry(true);
                context.setEntryBlock(bb);
            }

            Node lead, curr;
            bb.setStart(curr = lead = instructions.get(i));

            LinkedFastHashSet<Node> nodes = bb.getInstructions();

            while (curr != null) {
                nodes.add(curr);

                if (altersFlow(curr)) {
                    bb.setEnd(curr);
                    context.recordBlock(bb, nodes);
                    break;
                }

                if (leaders.get(curr.next.index()) && curr != lead) {
                    bb.setEnd(curr);
                    context.recordBlock(bb, nodes);
                    break;
                }

                curr = curr.next;
            }

            if (terminatesMethod(bb.getEnd())) {
                bb.setExit(true);
                context.addExit(bb);
            }
        }

        if (SANITY) {
            FastHashSet<Node> marked = new FastHashSet<>();
            BasicBlock root = context.getEntryBlock();

            for (BasicBlock bb : context.getBlocks()) {
                if (bb != root) {
                    Node start = bb.getStart();
                    Node prev = start.previous;

                    if (!(start instanceof Label) && !(prev instanceof Jump jump && jump.conditional())) sanity("weird fallthrough " + prev + " " + start);
                }

                marked.addAll(bb.getInstructions());
            }

            for (Node node : method.instructions) {
                if (!marked.contains(node)) sanity("unmarked node");
            }
        }
    }

    /**
     * Links all the blocks that can be reached via fallthrough, jumps, and switches.
     * Exception handlers are handled in the next step of building the cfg.
     */
    private void link() {
        BasicBlock root = context.getEntryBlock();
        Instructions instructions = method.instructions;

        for (BasicBlock bb : context.getBlocks()) {
            Node branch = bb.getEnd();

            if (branch == null) {
                sanity("Unknown end for basic block");
                continue;
            }

            // improve fall through handling later.
            {
                if (bb != root) {
                    Node prev = Objects.requireNonNull(bb.getStart().previous);
                    if (canFall(prev)) bb.addPredecessor(Objects.requireNonNull(context.getBlockForNode(prev)));
                }

                if (!bb.isExit()) {
                    Node end = Objects.requireNonNull(bb.getEnd());

                    if (end != instructions.last) {
                        Node next = Objects.requireNonNull(end.next);
                        if (canFall(end)) bb.addSuccessor(context.getBlockForNode(next));
                    }
                }
            }

            switch (branch.type()) {
                case Node.JUMP -> {
                    Jump jump = (Jump) branch;
                    BasicBlock target = context.getBlockForNode(jump.label);
                    if (target == null) sanity("Unknown jump destination");
                    else bb.addSuccessor(target);

                    if (jump.unconditional()) continue;
                    BasicBlock fall = context.getBlockForNode(Objects.requireNonNull(jump.next));
                    bb.addSuccessor(fall);
                }
                case Node.LOOKUP -> {
                    Lookup lookup = (Lookup) branch;
                    BasicBlock default_ = context.getBlockForNode(lookup._default);
                    if (default_ == null) sanity("Invalid lookupswitch default block");
                    else bb.addSuccessor(default_);

                    for (Label label : lookup.labels) {
                        BasicBlock target = context.getBlockForNode(label);
                        if (default_ == null) sanity("Invalid lookupswitch case block");
                        else bb.addSuccessor(target);
                    }
                }
                case Node.TABLE -> {
                    Table table = (Table) branch;

                    BasicBlock default_ = context.getBlockForNode(table._default);
                    if (default_ == null) sanity("Invalid tableswitch default block");
                    else bb.addSuccessor(default_);

                    for (Label label : table.labels) {
                        BasicBlock target = context.getBlockForNode(label);
                        if (default_ == null) sanity("Invalid tableswitch case block");
                        else bb.addSuccessor(target);
                    }
                }
            }
        }
    }

    private void inline() {
        FastHashSet<BasicBlock> blocks = context.getBlocks();

        for (BasicBlock block : blocks.toArray(new BasicBlock[0])) {
            if (block.isEntry() || block.isError()) continue;
            FastHashSet<BasicBlock> predecessors = block.getPredecessors();
            if (predecessors.size() != 1) continue;

            BasicBlock predecessor = predecessors.getFirst();
            assert predecessor != null;
            if (!block.getExceptionHandlers().equals(predecessor.getExceptionHandlers())) continue;

            Node end = predecessor.getEnd();
            if (end.opcode != GOTO) continue;

            LinkedFastHashSet<Node> instructions = predecessor.getInstructions();
            instructions.remove(end);
            instructions.addAll(block.getInstructions());

            FastHashSet<BasicBlock> successors = predecessor.getSuccessors();
            successors.remove(block);

            for (BasicBlock successor : block.getSuccessors().toArray(new BasicBlock[0])) {
                successor.addPredecessor(predecessor);
                successor.removePredecessor(block);
            }

            if (block.isExit()) {
                predecessor.setExit(true);
                FastHashSet<BasicBlock> exits = context.getExitBlocks();
                exits.remove(block);
                exits.add(predecessor);
            }

            predecessor.setEnd(block.getEnd());

            context.getLeaders().clear((int) block.getLabel());
            context.recordBlock(predecessor, instructions);
            blocks.remove(block);
        }
    }

    private void exceptions() {
        OptimizedExceptionAnalysis analysis = new OptimizedExceptionAnalysis(this);
        analysis.analyze();
    }

    private void dominance() {
        LengauerTarjanDominanceAnalysis dominance = new LengauerTarjanDominanceAnalysis(this);
        dominance.analyze();
        /*
        PostDominanceAnalysis postDominance = new PostDominanceAnalysis(this);
        postDominance.analyze();
        */
    }

    private void loops() {
        HavlakLoopAnalysis loops = new HavlakLoopAnalysis(this);
        loops.analyze();
    }

    private void form() {

    }
}