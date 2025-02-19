package dev.name.transformer.transformers.obf.hash;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class HashContext {
    public final Method method;
    public final long[] states;
    public final LocalPool.Local[] locals;
    public final InstructionBuilder builder;
    public final Node node;
}