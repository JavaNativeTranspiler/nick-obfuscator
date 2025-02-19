package dev.name;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.extensions.processor.processors.instructions.*;
import dev.name.asm.ir.extensions.processor.processors.methods.*;
import dev.name.asm.ir.types.Access;
import dev.name.gui.GUI;
import dev.name.gui.base.ImGuiApplication;
import dev.name.transformer.Transformers;
import dev.name.util.java.Jar;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.zip.Inflater;

public class Main {
    private static final boolean TEST_MODE = true;

    public static void main(String[] args) {
        //GUI gui  = new GUI();
        //ImGuiApplication.launch(gui);
        Processor.register(new JSRProcessor());
        Processor.register(new ConstantInstructionProcessor());
        Processor.register(new InstructionCleanerProcessor());
        Processor.register(new StackFolderProcessor());
        Processor.register(new MethodInformationCleaner());
        Processor.register(new DeadcodeProcessor());
        Processor.register(new BadLocalProcessor());
        //Processor.register(new LocalInitializerProcessor());
        Processor.register(new JunkExceptionProcessor());
        Processor.register(new FallthroughInliner());
        Processor.register(new UnusedLabelRemover());

        Jar jar = Jar.read("tests\\test_unobfed.jar", 0);
        Transformers.call(jar);
        jar.export("tests\\test_obfed.jar");
        System.out.println("Successfully obfuscated " + jar.getJar().getName() + ".");

        if (TEST_MODE) {
            System.out.println("[TEST] Initializing Tests");

            try {
                final Process process = new ProcessBuilder()
                        .command("java", "-jar", "test_obfed.jar")
                        .directory(new File("tests"))
                        .redirectInput(ProcessBuilder.Redirect.PIPE)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start();

                Executors.newSingleThreadExecutor().submit(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) System.out.println("[TEST] " + line);
                    } catch (final Throwable _t) {
                        _t.printStackTrace(System.err);
                    }
                });

                Executors.newSingleThreadExecutor().submit(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) System.err.println("[TEST] " + line);
                    } catch (final Throwable _t) {
                        _t.printStackTrace(System.err);
                    }
                });

                if (process.waitFor() != 0) System.out.println("Process failed to execute.");
            } catch (final Throwable _t) {
                _t.printStackTrace(System.err);
            }

            System.out.println("[TEST] Completed Tests");
            System.exit(0);
        }
    }
}