package dev.name.transformer.transformers.obf.string;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.nodes.Table;
import dev.name.asm.ir.types.Node;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import dev.name.util.math.Random;

public final class StringTableTransformer extends Transformer implements Random {
    @Override
    public String name() {
        return "String Table Transformer";
    }

    @Override
    public void transform(final ClassPool pool) {
        for (final Class klass : pool)
            for (final Method method : klass.methods) {
                if (!method.name.equals("<clinit>")) continue;

                for (final Node instruction : method.instructions.toArray()) {
                    if (!(instruction instanceof Constant constant) || !(constant.cst instanceof String str)) continue;
                    constant.replace(form(str));
                }
            }
    }

    private static Instructions form(final String str) {
        final InstructionBuilder builder = InstructionBuilder.generate();
        final Label end = builder.newlabel(), dflt = builder.newlabel();
        //
        final int size = RANDOM.nextInt(7, 15);
        final int[] cases = new int[size];
        for (int i = 0; i < size; i++) cases[i] = RANDOM.nextInt(Character.MIN_VALUE, Character.MAX_VALUE);
        final int coefficient = RANDOM.nextInt(Character.MIN_VALUE, Character.MAX_VALUE);
        final Label[] labels = new Label[size - 1];
        for (int i = 0; i < labels.length; i++) labels[i] = new Label();
        //
        builder.ldc(encrypt(str, cases, size, coefficient))
                .iconst_0()
                .dup_x1()
                .pop()
                .invokevirtual("java/lang/String", "toCharArray", "()[C", false)
                .dup()
                .arraylength();

        final Label loop = builder.label();
        builder.swap().dup_x2().pop().swap().dup2().if_icmple(end);
        builder.swap().dup_x2().pop().dup2().dup2_x2().caload().dup2_x2().pop2().dup2_x2().pop();
        builder.ldc(coefficient).swap().ldc(size).irem().tableswitch(new Table(0, size - 2, dflt, labels));
        final Label handler = builder.label();
        builder.ixor().ixor();
        builder.i2c();
        builder.castore();
        builder.swap();
        builder.dup_x2();
        builder.pop();
        builder.iconst_1();
        builder.iadd();
        builder.dup_x2();
        builder.pop();
        builder.jump(loop);
        builder.bind(dflt).ldc(cases[size - 1]).jump(handler);

        for (int i = 0; i < size - 1; i++) builder.bind(labels[i]).ldc(cases[i]).jump(handler);

        builder.bind(end);
        builder.pop2();
        builder._new(String.class);
        builder.dup_x1();
        builder.swap();

        builder.invokespecial("java/lang/String", "<init>", "([C)V")
               .invokevirtual("java/lang/String", "intern", "()Ljava/lang/String;", false);

        return builder.build();
    }

    private static String encrypt(final String str, final int[] cases, final int size, final int coeff) {
        final char[] data = str.toCharArray();

        for (int i = 0; i < data.length; i++)
            data[i] = (char) (data[i] ^ (coeff ^ cases[i % size]));

        return new String(data);
    }
}