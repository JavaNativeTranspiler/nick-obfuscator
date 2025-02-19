package tests;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.extensions.processor.processors.instructions.BadLocalProcessor;
import dev.name.asm.ir.extensions.processor.processors.instructions.ConstantInstructionProcessor;
import dev.name.asm.ir.extensions.processor.processors.instructions.InstructionCleanerProcessor;
import dev.name.asm.ir.extensions.processor.processors.instructions.StackFolderProcessor;
import dev.name.asm.ir.extensions.processor.processors.methods.DeadcodeProcessor;
import dev.name.asm.ir.extensions.processor.processors.methods.LocalInitializerProcessor;
import dev.name.asm.ir.extensions.processor.processors.methods.MethodInformationCleaner;
import xyz.qreaj.bytecodeVirtualizer.Virtualizer;

public class Main {
    public static void main(String[] args) throws Throwable {
        Processor.register(new ConstantInstructionProcessor());
        Processor.register(new InstructionCleanerProcessor());
        Processor.register(new StackFolderProcessor());
        Processor.register(new MethodInformationCleaner());
        Processor.register(new DeadcodeProcessor());
        Processor.register(new BadLocalProcessor());
        Processor.register(new LocalInitializerProcessor());

        final Virtualizer virtualizer = new Virtualizer();
        final long time = System.currentTimeMillis();

        virtualizer.virtualize("jars\\Prestige1.jar", "ZKM-virt.jar");
        System.out.println("Virtualized in: " + (System.currentTimeMillis() - time));
    }
}