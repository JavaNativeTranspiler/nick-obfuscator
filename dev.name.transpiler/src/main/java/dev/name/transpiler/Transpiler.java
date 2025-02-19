package dev.name.transpiler;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.extensions.processor.processors.instructions.*;
import dev.name.asm.ir.extensions.processor.processors.methods.*;
import dev.name.transpiler.builder.TranspiledClass;
import dev.name.util.asm.Bytecode;
import dev.name.transpiler.processor.ClinitExtractor;
import dev.name.transpiler.processor.InitExtractor;
import dev.name.transpiler.types.Cache;
import dev.name.util.java.Jar;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.io.FileOutputStream;

public class Transpiler {
    public static void main(String[] args) throws Throwable{
        Processor.register(new JSRProcessor());
        Processor.register(new ConstantInstructionProcessor());
        Processor.register(new InstructionCleanerProcessor());
        Processor.register(new StackFolderProcessor());
        Processor.register(new MethodInformationCleaner());
        Processor.register(new DeadcodeProcessor());
        Processor.register(new BadLocalProcessor());
        Processor.register(new LocalInitializerProcessor());
        Processor.register(new JunkExceptionProcessor());
        Processor.register(new FallthroughInliner());
        Processor.register(new UnusedLabelRemover());
        Processor.register(new ClinitExtractor());
        Processor.register(new InitExtractor());

        final Jar jar = Jar.read("jars\\zkm_crack.jar", 0);

        for (Class klass : jar.classes) {
            TranspiledClass k = TranspiledClass.create(klass);
            System.out.println(k.registry);
        }

        jar.export("transpiler.jar");
    }
}