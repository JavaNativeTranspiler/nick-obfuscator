package dev.name.asm.ir.extensions.processor.processors.methods;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

public class JSRProcessor extends Processor.MethodProcessor {
    @Override
    public void pre(Method type) {
        //TODO
    }

    @Override public void post(Method type) {}
}