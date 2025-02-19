package dev.name.transpiler.handlers;

import dev.name.asm.ir.types.Node;
import dev.name.transpiler.builder.TranspiledMethod;
import dev.name.transpiler.types.StackFrame;

public abstract class Handler<T extends Node> {
    public abstract void handle(StackFrame frame, TranspiledMethod method, T node);
}