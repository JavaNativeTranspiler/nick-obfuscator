package dev.name.asm.ir.extensions.processor.processors.methods;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;

public final class MethodInformationCleaner extends Processor.MethodProcessor {
    @Override
    public void pre(final Method method) {
        process(method);
    }

    @Override
    public void post(final Method method) {
        process(method);
    }

    //probably only needs on pre but whatever
    private void process(final Method method) {
        method.signature = null;
        method.typeAnnotations.clear();
        method.locals.clear();
        method.localAnnotations.clear();
        method.parameters.clear();
        method.parameterAnnotations.clear();
        method.blocks.forEach(block -> block.annotations.clear());
        method.instructions.forEach(instruction -> instruction.annotations.clear());
    }
}