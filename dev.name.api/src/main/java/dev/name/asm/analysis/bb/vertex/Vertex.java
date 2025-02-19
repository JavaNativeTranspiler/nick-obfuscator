package dev.name.asm.analysis.bb.vertex;

import dev.name.asm.analysis.bb.BasicBlock;

public abstract class Vertex {
    public final VertexType type;
    public final BasicBlock target;

    public Vertex(VertexType type, BasicBlock target) {
        this.type = type;
        this.target = target;
    }

    @Override
    public String toString() {
        return String.format("Vertex#%s", type.toString());
    }
}