package dev.name.transpiler.processor;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;

public class InitExtractor extends Processor.ClassProcessor {
    @Override
    public void pre(final Class klass) {
        if (!klass.superName.equals("java/lang/Object")) return;

        for (final Method method : klass.methods) {
            if (!method.name.equals("<init>")) continue;
            // klass -> no super class -> method -> method doesnt call other constructor -> extract... later
        }
    }

    @Override
    public void post(final Class type) {

    }
}
