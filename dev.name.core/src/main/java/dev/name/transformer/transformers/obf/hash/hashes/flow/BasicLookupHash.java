package dev.name.transformer.transformers.obf.hash.hashes.flow;

import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.nodes.Lookup;
import dev.name.asm.ir.types.LocalPool;
import dev.name.transformer.transformers.obf.hash.Hash;
import dev.name.transformer.transformers.obf.hash.HashContext;
import dev.name.transformer.transformers.obf.hash.HashTransformer;

import java.util.Arrays;
import java.util.function.Consumer;

public class BasicLookupHash implements Hash {
    @Override
    public void hash(final HashContext context, final Consumer<InstructionBuilder> consumer) {
        final InstructionBuilder builder = context.builder;
        final Label exit = builder.newlabel();

        final int random = nextInt(0, context.locals.length - 1);
        final LocalPool.Local key = context.locals[random];
        final int state = (int) context.states[random];

        final int cases = nextInt(3, 7);
        final int[] keys = new int[cases];

        keys[0] = state;
        for (int i = 1; i < keys.length; i++) keys[i] = nextInt();

        final Label[] labels = new Label[cases];
        for (int i = 0; i < labels.length; i++) labels[i] = builder.newlabel();

        Arrays.sort(keys);
        if (dev.name.util.collections.array.Arrays.indexOf(keys, 0) != -1) System.out.println("[BasicLookupSwitch] Shouldn't happen: " + context.method.klass.name + " " + context.method.name + " " + context.method.desc);

        final int correct = dev.name.util.collections.array.Arrays.indexOf(keys, state);
        final Label executed = labels[correct];
        final Label default_ = new Label();

        builder.add(key.load());
        builder.l2i();
        builder.lookupswitch(new Lookup(default_, keys, labels));

        for (final Label label : labels) {
            final int altered = nextInt(0, context.locals.length - 1, random);
            final long r = nextLong();

            if (label.equals(executed)) {
                context.states[altered] ^= r;
                context.states[random] ^= context.states[altered];
            }

            final LocalPool.Local local = context.locals[altered];

            builder.bind(label);
            builder.add(key.load());
            builder.add(local.load());
            builder.ldc(r);
            builder.lxor();
            builder.dup2();
            builder.add(local.store());
            builder.lxor();
            builder.add(key.store());
            builder.jump(exit);
        }

        {
            builder.bind(default_);
            builder._new("java/lang/RuntimeException");
            builder.dup();
            builder.invokespecial("java/lang/RuntimeException", "<init>", "()V");
            builder.athrow();
        }

        builder.bind(exit);
        builder.forEach(node -> node.flags.set(HashTransformer.NO_DETOUR, true));

        consumer.accept(builder);
    }
}