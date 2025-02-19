package dev.name.transpiler.handlers;

import dev.name.asm.ir.nodes.Jump;
import dev.name.asm.ir.nodes.Label;
import dev.name.transpiler.builder.TranspiledMethod;
import dev.name.transpiler.types.StackFrame;

public final class JumpHandler extends Handler<Jump> {
    @Override
    public void handle(StackFrame frame, TranspiledMethod method, Jump node) {
        Label label = node.label;
        String jump = method.labelMap.goto_(label);
    }
}