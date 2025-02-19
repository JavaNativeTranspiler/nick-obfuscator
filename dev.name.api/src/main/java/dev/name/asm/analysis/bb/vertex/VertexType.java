package dev.name.asm.analysis.bb.vertex;

public enum VertexType {
    FALLTHROUGH,
    SWITCH,
    DEFAULT,
    CONDITIONAL,
    UNCONDITIONAL,
    EXCEPTION
}