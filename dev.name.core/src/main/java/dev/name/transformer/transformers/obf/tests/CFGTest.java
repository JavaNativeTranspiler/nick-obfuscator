package dev.name.transformer.transformers.obf.tests;

import dev.name.asm.analysis.bb.BasicBlock;
import dev.name.asm.analysis.bb.ControlFlowGraph;
import dev.name.asm.analysis.bb.DotGraph;
import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Accessor;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.nodes.Instruction;
import dev.name.asm.ir.nodes.Invoke;
import dev.name.transformer.Transformer;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.java.ClassPool;

public class CFGTest extends Transformer {
    @Override
    public String name() {
        return "CFG Test";
    }

    @Override
    public void transform(ClassPool pool) {
        for (Class k : pool) {
            System.out.println(k.name);
            for (Method m : k.methods) {

                ControlFlowGraph cfg = ControlFlowGraph.build(m, jar);
                //ControlFlowGraph grap2h = ControlFlowGraph.build(m, jar);
                //System.out.println(DotGraph.generate(grap2h));
                //System.out.println(DotGraph.generate(graph));
                //System.out.println(DotGraph.generateDominanceTree(graph));
            }
        }
    }
}
