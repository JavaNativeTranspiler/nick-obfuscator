package dev.name.transformer.transformers.obf.hash;

import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.util.math.Random;

import java.util.function.Consumer;

public interface Hash extends Random {
    void hash(final HashContext context, final Consumer<InstructionBuilder> consumer);
}