package dev.name.transformer.transformers.obf.hash.hashes.generic;

import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.transformer.transformers.obf.hash.Hash;
import dev.name.transformer.transformers.obf.hash.HashContext;

import java.util.function.Consumer;

public class BasicHash implements Hash {
    @Override
    public void hash(final HashContext context, final Consumer<InstructionBuilder> consumer) {
        final InstructionBuilder builder = context.builder;

        final long k = nextLong();
        final int l = nextInt(0, context.states.length - 1);

        builder.add(context.locals[l].load());
        builder.ldc(k);
        builder.lxor();
        builder.add(context.locals[l].store());

        context.states[l] ^= k;

        consumer.accept(builder);
    }
}