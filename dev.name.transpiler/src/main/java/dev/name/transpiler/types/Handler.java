package dev.name.transpiler.types;

import dev.name.asm.ir.types.Node;

public abstract class Handler<T extends Node> {
    public abstract int rsp(final T node, final int rsp);
}