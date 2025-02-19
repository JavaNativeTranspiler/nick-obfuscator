package dev.name.transformer.transformers.obf.hash.hashes.exception;

import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.transformer.transformers.obf.hash.Hash;
import dev.name.transformer.transformers.obf.hash.HashContext;

import java.util.function.Consumer;

public class ExceptionHash implements Hash {
    @Override
    public void hash(final HashContext context, final Consumer<InstructionBuilder> consumer) {

    }

    /*private static final String DESC = "java/lang/RuntimeException";

    @Override
    public void hash(final HashContext.Instruction context) {
        final InstructionBuilder builder = InstructionBuilder.generate();
        final int index = nextInt(0, context.states.length - 1);
        final long key = context.states[index];
        final LocalPool.Local local = context.locals[index];

        final int size = nextInt(3, 7);
        final Label[] labels = new Label[size];
        Arrays.setAll(labels, i -> builder.newlabel());
        final int[] keys = new int[size];
        Arrays.setAll(keys, k -> nextInt());
        Arrays.sort(keys);

        final int correct = nextInt(0, keys.length - 1);
        keys[correct] = (int) key;
        Arrays.sort(keys);
        //final Label executed = labels[];

        final Label esc = builder.newlabel();
        final Label handler = builder.newlabel();
        final Label def = builder.newlabel();

        final Label start = builder.label();

        final Lookup lookup = new Lookup(def, keys, labels);
        builder.add(local.load()).l2i();
        builder.lookupswitch(lookup);

        final int altered = nextInt(0, context.locals.length - 1, index);
        long change = 0;

        for (final Label label : labels) {
            builder.bind(label);
            final int random = nextInt(0, context.locals.length - 1);
            switch (nextInt(1, 3)) {
                case 1 -> {
                    final long r = nextLong();
                    builder.add(context.locals[random].load());
                    //builder.ldc(r)
                }
                case 2 -> {

                }
                case 3 -> {

                }
            }
        }

        {
            builder.bind(def);
        }

        {
            builder.bind(handler);
        }

        context.states[altered] ^= change;
        context.method.blocks.add(new Block(start, esc, handler, DESC));
    }

    @Override
    public void hash(final HashContext.Builder context) {

    }*/
}