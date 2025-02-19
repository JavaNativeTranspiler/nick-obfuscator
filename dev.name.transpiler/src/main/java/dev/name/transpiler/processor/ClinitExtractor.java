package dev.name.transpiler.processor;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.types.Access;

import java.util.UUID;

public class ClinitExtractor extends Processor.ClassProcessor {
    @Override
    public void pre(final Class klass) {
        Method clinit = klass.methods.stream().filter(method -> method.name.equals("<clinit>")).findFirst().orElse(null);
        if (clinit == null) return;
        Method extracted = new Method(Access.builder()._private()._static().build(), String.format("$clinit$extracted$%s", UUID.randomUUID().toString().replace("-", "")), clinit.desc, clinit.signature, clinit.exceptions);
        klass.addMethod(extracted);
        extracted.blocks = clinit.blocks;
        clinit.blocks.clear();
        extracted.setInstructions(clinit.instructions);
        clinit.setInstructions(InstructionBuilder.generate().add(extracted.invoker())._return().build());
    }

    @Override public void post(final Class klass) {}
}