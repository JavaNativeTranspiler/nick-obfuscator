package dev.name.asm.ir.extensions.processor.processors.methods;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.util.asm.LabelCompressor;

public final class UnusedLabelRemover extends Processor.MethodProcessor {
    @Override
    public void pre(Method method) {
        apply(method);
    }

    @Override
    public void post(Method method) {
        apply(method);
    }

    private void apply(Method method) {
        LabelCompressor.compress(method);
    }
}