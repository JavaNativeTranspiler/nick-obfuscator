package dev.name.asm.analysis.bb;

import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Opcodes;
import dev.name.util.collections.set.FastHashSet;

import static dev.name.asm.analysis.bb.ControlFlowGraph.altersFlow;

@SuppressWarnings("unused")
public final class DotGraph {
    public static String generate(ControlFlowGraph graph) {
        StringBuilder dot = new StringBuilder("digraph Flow {\n");

        dot.append("  graph [\n")
                .append("    rankdir=TB,\n")
                .append("    splines=ortho,\n")
                .append("    nodesep=0.5,\n")
                .append("    ranksep=0.7\n")
                .append("  ];\n\n");

        dot.append("  node [\n")
                .append("    shape=box,\n")
                .append("    fontname=Helvetica,\n")
                .append("    fontsize=11,\n")
                .append("    margin=0.2,\n")
                .append("    height=0.4\n")
                .append("  ];\n\n")
                .append("  edge [\n")
                .append("    fontname=Helvetica,\n")
                .append("    fontsize=10\n")
                .append("  ];\n\n");

        Context context = graph.getContext();
        FastHashSet<BasicBlock> blocks = context.getBlocks();

        for (BasicBlock block : blocks) {
            String blockColor = determineBlockColor(block);
            String blockStyle = determineBlockStyle(block);

            dot.append(String.format("  flow_block%d [\n", block.getLabel()))
                    .append(String.format("    label=\"%s\",\n", formatBlockLabel(block)))
                    .append(String.format("    color=\"%s\",\n", blockColor))
                    .append(String.format("    style=\"%s\"\n", blockStyle))
                    .append("  ];\n");
        }

        for (BasicBlock block : blocks) {
            Node end = block.getEnd();
            BasicBlock t = null;

            if (end instanceof Jump jump && jump.conditional()) {
                t = context.getBlockForNode(jump.label);
            }

            for (BasicBlock succ : block.getSuccessors()) {
                boolean isBackEdge = false;
                dot.append(String.format("  flow_block%d -> flow_block%d [\n", block.getLabel(), succ.getLabel()))
                        .append(String.format("    color=\"%s\",\n", determineBlockColor(succ)))
                        .append("    style=\"solid\"");
                if (t != null) dot.append(",\n").append(String.format("    label=\"%s\"\n", t == succ ? "true" : "false"));
                else dot.append("\n");
                dot.append("  ];\n");
            }
        }

        dot.append("}");
        return dot.toString();
    }

    private static String determineBlockColor(BasicBlock block) {
        if (block.isExit() && block.isError()) return "#7852A9";
        if (block.isEntry()) return "#008000";
        if (block.isExit()) return "#800000";
        if (block.isError()) return "#FFA500";
        return "#000000";
    }

    private static String determineBlockStyle(BasicBlock block) {
        //if (block.getLoopHeader() != null) return "filled";
        return "solid";
    }

    private static String formatBlockLabel(BasicBlock block) {
        if (false) {
            StringBuilder sb = new StringBuilder();

            for (Node node : block.getInstructions()) {
                if (node instanceof Label || node instanceof Frame || node instanceof Line) continue;
                sb.append(node instanceof Constant ? "LDC" : Opcodes.id(node.opcode)).append("\n");
            }

            if (true) return sb.toString();
        }
        Node end = block.getEnd();
        return block.getLabel() + " | " + (altersFlow(end) ? Opcodes.id(end.opcode) : "GOTO");
    }

    public static String generateDominanceTree(ControlFlowGraph graph) {
        StringBuilder dot = new StringBuilder("digraph DominanceTree {\n");

        dot.append("  node [\n")
                .append("    shape=box,\n")
                .append("    fontname=Helvetica,\n")
                .append("    fontsize=11,\n")
                .append("    margin=0.2,\n")
                .append("    height=0.4\n")
                .append("  ];\n\n");

        FastHashSet<BasicBlock> blocks = graph.getContext().getBlocks();

        for (BasicBlock block : blocks) {
            dot.append(String.format("  dominance_block%d [\n", block.getLabel()))
                    .append(String.format("    label=\"%s\",\n", block.getLabel()))
                    .append(String.format("    color=\"%s\"\n", determineBlockColor(block)))
                    .append("  ];\n");
        }

        for (BasicBlock block : blocks) {
            if (block.getImmediateDominator() == null) continue;
            dot.append(String.format("  dominance_block%d -> dominance_block%d [\n", block.getImmediateDominator().getLabel(), block.getLabel()))
                    .append("    color=\"#006400\",\n")
                    .append("    style=solid,\n")
                    .append("    label=\"dom\"\n")
                    .append("  ];\n");
        }

        dot.append("}\n");
        return dot.toString();
    }

    public static String generatePostDominanceTree(ControlFlowGraph graph) {
        StringBuilder dot = new StringBuilder("digraph PostDominanceTree {\n");

        dot.append("  node [\n")
                .append("    shape=box,\n")
                .append("    fontname=Helvetica,\n")
                .append("    fontsize=11,\n")
                .append("    margin=0.2,\n")
                .append("    height=0.4\n")
                .append("  ];\n\n");

        FastHashSet<BasicBlock> blocks = graph.getContext().getBlocks();

        for (BasicBlock block : blocks) {
            dot.append(String.format("  postdom_block%d [\n", block.getLabel()))
                    .append(String.format("    label=\"%s\",\n", block.getLabel()))
                    .append(String.format("    color=\"%s\"\n",
                            block.isEntry() ? "#008000" :
                                    block.isExit() ? "#800000" :
                                            block.isError() ? "#FFA500" : "#000000"))
                    .append("  ];\n");
        }

        for (BasicBlock block : blocks) {
            BasicBlock ipdom = block.getImmediatePostDominator();
            if (ipdom == null) continue;

            dot.append(String.format("  postdom_block%d -> postdom_block%d [\n", ipdom.getLabel(), block.getLabel()))
                    .append("    color=\"#8B4513\",\n")
                    .append("    style=solid,\n")
                    .append("    label=\"postdom\"\n")
                    .append("  ];\n");
        }

        dot.append("}\n");
        return dot.toString();
    }
}