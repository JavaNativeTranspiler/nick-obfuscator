package dev.name.transformer.transformers.obf.stack.impl;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class StackRandomizerPOP implements Opcodes {

    private static final Random RANDOM = new SecureRandom();
    private static LocalPool.Local shiftAmount = null;
    private static LocalPool.Local junkInt = null;
    private static Method lastMethod = null;
    public static void apply(Node node, final Map<Node, Frame<BasicValue>> frame_map, final LocalPool locals) {
        if (!Bytecode.isStack(node)) return;
        if (node.opcode != POP) return;
        Method method = node.method;
        if (lastMethod != method) {
            //reset for each method
            shiftAmount = null;
            junkInt = null;
        }
        Class klass =  method.klass;

        final Frame<BasicValue> frame = frame_map.get(node);
        final Type type = frame.getStack(frame.getStackSize() -1).getType();
        final InstructionBuilder builder = InstructionBuilder.generate();

        switch (type.getSort()) {
            case Type.INT -> {
                Label label = builder.label();
                builder.ldc(RANDOM.nextInt(255)).ixor().ldc(RANDOM.nextInt(255)).ifeq(label).ldc(RANDOM.nextInt(255)).ixor().dup().iconst_1().iadd().ifeq(label);
            }
            case Type.OBJECT -> {
                System.out.println(klass.name + " " + method.name);
                Label defaultLabel = builder.newlabel();
                final int lookupSize = RANDOM.nextInt(3,10);
                int[] keys = new int[lookupSize];
                Label[] labels = new Label[lookupSize];

                for (int i = 0;i<lookupSize;i++) {
                    keys[i] = RANDOM.nextInt(1,100000);
                    labels[i] = builder.newlabel();
                }



                if (shiftAmount == null) shiftAmount = locals.allocate(LocalPool.INT);
                if (junkInt == null) junkInt = locals.allocate(LocalPool.INT);

                final int baseNumber = RANDOM.nextInt(1,100000);

                keys[0] = baseNumber;
                Arrays.sort(keys);

                builder.ldc(baseNumber).invokestatic("java/lang/Integer","valueOf","(I)Ljava/lang/Integer;",false);

                builder.ldc(RANDOM.nextInt(1,10000))

                        .add(junkInt.store());

                builder.invokevirtual("java/lang/Object","hashCode","()I",false)
                        .lookupswitch(defaultLabel,keys,labels);

                for (int i = 0;i<lookupSize;i++) {
                    builder.bind(labels[i])
                            .add(junkInt.load())
                            .ldc(RANDOM.nextInt(100))  
                            .ixor()
                            .add(junkInt.store());
                }



                final int key = RANDOM.nextInt(1,255);
                final int mask = RANDOM.nextInt(1,255);


                builder.bind(defaultLabel).invokevirtual("java/lang/Object","hashCode","()I",false)
                        .add(shiftAmount.store())
                        .add(junkInt.load())
                        .add(shiftAmount.load())
                        .ishl()
                        .ldc(key)
                        .ldc(mask)
                        .iand()
                        .ior()
                        .add(junkInt.store())


                        .add(junkInt.load())
                        .ldc(mask)
                        .iand()
                        .add(junkInt.load())
                        .add(shiftAmount.load())
                        .ishr()
                        .ior()

                        .add(junkInt.store());

                            /*Label label = builder.newlabel();
                            builder.ifnull(label).jump(label);
                            builder.bind(label);*/
            }
            default -> {
                builder.pop();
            }


        }
        node.replace(builder.build());
    }
}
