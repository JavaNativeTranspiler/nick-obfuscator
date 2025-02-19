package dev.name.asm.analysis.bb;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.types.Node;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.collections.set.LinkedFastHashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@SuppressWarnings("all")
public final class Context {
    private final Method method;
    private final Instructions instructions;
    private final FastHashSet<BasicBlock> blocks;
    private final FastHashSet<Loop> loops;

    private final Map<Node, BasicBlock> nodeToBlock;

    private final BitSet leaders,
                         processed,
                         exceptionRanges;

    private BasicBlock entryBlock;

    private final FastHashSet<BasicBlock> errorBlocks, exitBlocks;

    /**
     * These are handlers that self protect due to having an exception range that contains the handler itself. <br>
     * These should only occur in a finally block (if the exception type is null | `*`). <br>
     * The reason we don't just add a successor and handle as applicable is this can mess up loop analysis among other features.
     */
    private final FastHashSet<BasicBlock.ExceptionHandler.ExceptionBlock> selfProtectingHandlers;

    private boolean hasCycles,
                    hasUnreachableCode,
                    hasExceptionHandlers;

    public Context(Method method) {
        this.method = method;
        this.instructions = method.instructions;
        this.blocks = new FastHashSet<>();
        this.loops = new FastHashSet<>();
        this.nodeToBlock = new IdentityHashMap<>();
        this.leaders = new BitSet(instructions.size());
        this.processed = new BitSet(instructions.size());
        this.exceptionRanges = new BitSet(instructions.size());
        this.errorBlocks = new FastHashSet<>();
        this.exitBlocks = new FastHashSet<>();
        this.selfProtectingHandlers = new FastHashSet<>();
    }

    public void recordBlock(BasicBlock block, LinkedFastHashSet<Node> nodes) {
        blocks.add(block);

        for (Node node : nodes) {
            nodeToBlock.put(node, block);
        }

        block.setTotalInstructions(nodes.size());
    }

    public void markLeader(int index) {
        leaders.set(index);
    }

    public void markLeaders(int... indexes) {
        for (int n : indexes) markLeader(n);
    }

    public boolean isLeader(int index) {
        return leaders.get(index);
    }

    public void markCycle() {
        this.hasCycles = true;
    }

    public void markUnreachable(BasicBlock block) {
        this.hasUnreachableCode = true;
    }

    public void markErrorBlock(BasicBlock block) {
        if (errorBlocks.add(block)) {
            block.setExceptionBlock(new BasicBlock.ExceptionHandler(block));
            block.setError(true);
        }
    }

    public BasicBlock getBlockForNode(Node node) {
        return nodeToBlock.get(node);
    }

    public void addExit(BasicBlock bb) {
        exitBlocks.add(bb);
    }
}