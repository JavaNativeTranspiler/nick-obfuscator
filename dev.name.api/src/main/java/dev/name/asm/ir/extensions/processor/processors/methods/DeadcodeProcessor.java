package dev.name.asm.ir.extensions.processor.processors.methods;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.FlowAnalyzer;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

public final class DeadcodeProcessor extends Processor.MethodProcessor {
    @Override
    public void pre(final Method method) {
        process(method);
    }

    @Override
    public void post(final Method method) {
        process(method);
    }

    public static void process(final Method method) {
        final FlowAnalyzer analyzer = new FlowAnalyzer(method);
        analyzer.analyze();

        final ObjectLinkedOpenHashSet<Node> frames = analyzer.getFrames();

        for (final Node node : method.instructions) {
            if (frames.contains(node)) continue;
            node.delete();
        }
    }
}