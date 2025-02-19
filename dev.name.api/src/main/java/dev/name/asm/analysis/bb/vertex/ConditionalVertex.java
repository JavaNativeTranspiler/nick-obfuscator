package dev.name.asm.analysis.bb.vertex;

import dev.name.asm.analysis.bb.BasicBlock;
import dev.name.asm.analysis.bb.flow.Condition;

public final class ConditionalVertex extends Vertex {
    public final Condition condition;

    public ConditionalVertex(int opcode, BasicBlock target) {
        super(VertexType.CONDITIONAL, target);
        this.condition = Condition.fromOpcode(opcode);
    }
}