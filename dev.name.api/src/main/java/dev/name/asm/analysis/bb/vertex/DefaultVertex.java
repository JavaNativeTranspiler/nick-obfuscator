package dev.name.asm.analysis.bb.vertex;

import dev.name.asm.analysis.bb.BasicBlock;
import org.objectweb.asm.tree.analysis.Analyzer;

public final class DefaultVertex extends Vertex {
    public DefaultVertex(BasicBlock target) {
        super(VertexType.DEFAULT, target);
    }
}