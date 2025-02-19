package dev.name.transformer.transformers.obf.flow;

import dev.name.asm.analysis.bb.*;
import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.transformer.Transformer;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.java.ClassPool;
import dev.name.util.math.Random;

import java.lang.invoke.LambdaMetafactory;

public final class LoopMutationTransformer extends Transformer implements Random {
    @Override
    public String name() {
        return "";
    }

    @Override
    public void transform(ClassPool pool) {

        for (Class k : pool)
            for (Method m : k.methods) {
                if (!k.name.contains("c")) continue;
                if (!m.name.equals("a")) continue;
                if (!m.desc.contains("(Ljava/lang/String;)Ljava/lang/String;")) continue;
                ControlFlowGraph cfg = ControlFlowGraph.build(m, jar);
                //System.out.println(DotGraph.generatePostDominanceTree(cfg));
                System.out.println(DotGraph.generate(cfg));
                if (true) continue;
                if (m == null || m.access.isAbstract() || m.access.isNative()) continue;
                if (m.instructions.size() <= 0) continue;

                //ControlFlowGraph cfg = ControlFlowGraph.build(m, jar);
                FastHashSet<Loop> loops = cfg.getContext().getLoops();
                if (loops.isEmpty()) continue;

                for (Loop loop : loops) {
                    assert loop != null;
                    LocalPool lp = new LocalPool(m);
                    LocalPool.Local v = lp.allocate(LocalPool.INT);
                    BasicBlock root = cfg.getContext().getEntryBlock();
                    BasicBlock header = loop.getHeader();

                    int val = nextInt();
                    int initial = val;

                    for (BasicBlock dom : header.getDominators()) {
                        if (dom == root || dom == header) continue;
                        if (!dom.getLoops().isEmpty()) continue;
                        Node end = dom.getEnd();

                        int key;
                        val ^= (key = nextInt());
                        end.insertBefore(v.load());
                        end.insertBefore(new Constant(key));
                        end.insertBefore(new Instruction(IXOR));
                        end.insertBefore(v.store());
                    }

                    Node end = root.getEnd();

                    end.insertBefore(new Constant(initial));
                    end.insertBefore(v.store());

                    Node hend = header.getEnd();

                    hend.insertAfter(new Invoke(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V"));
                    hend.insertAfter(new Instruction(IXOR));
                    hend.insertAfter(new Constant(val));
                    hend.insertAfter(v.load());
                    hend.insertAfter(new Accessor(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));

                    header.getDominated().forEach(pred -> {
                        Node ez = pred.getEnd();
                        if (!(ez instanceof Jump jump && jump.unconditional())) return;
                        jump.opcode = IFNE;
                        jump.insertBefore(v.load());
                        jump.insertAfter(new Jump(GOTO, jump.label));
                    });


                }
            }
    }
}