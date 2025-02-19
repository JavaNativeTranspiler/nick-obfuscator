package xyz.qreaj.bytecodeVirtualizer;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.types.Node;
import dev.name.util.java.ClassPool;
import dev.name.util.java.Jar;
import xyz.qreaj.bytecodeVirtualizer.transformers.ConditionJumpTransformer;
import xyz.qreaj.bytecodeVirtualizer.transformers.MathTransformer;
import xyz.qreaj.bytecodeVirtualizer.transformers.NumberTransformer;
import xyz.qreaj.bytecodeVirtualizer.transformers.StringTransformer;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.engine.Engine;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;
import xyz.qreaj.virtualizer.writer.LabelIndex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

public class Virtualizer {
     private static final Set<Transformer> transformers = Set.of(new MathTransformer(), new ConditionJumpTransformer(), new NumberTransformer(), new StringTransformer());
    public static ClassPool classes;

    public static Map<Class, Method> methodsToAdd = new HashMap<>();

    public void virtualize(final String input, final String output) throws Exception {
        final Jar jar;
        classes = (jar = new Jar(new JarFile(input), 0)).getClasses();

        final ByteArrayOutputStream virtualized = new ByteArrayOutputStream();
        final ConstantPoolWriter constantPool = new ConstantPoolWriter(new DataOutputStream(virtualized));

        for (final Class klass : classes) {
            if (klass.access.isInterface()) continue; // no clinit
            for (final Method method : klass.methods) {
                if (method.name.equals("<clinit>")) continue;
                for (final Node node : method.instructions) {
                    if (method.instructions.size() > 20000) {
                        System.out.println("Virtualization stopped in " + klass.name + "." + method.name + " due to huge method size");
                        break;
                    }

                    final BytecodeWriter writer = new BytecodeWriter(constantPool);

                    for (final Transformer transformer : transformers)
                        if (transformer.canTransform(node))
                            transformer.transform(node, method, klass, constantPool, writer);

                    LabelIndex.counter = 0;
                }
            }
        }
        for (Map.Entry<Class, Method> entry : methodsToAdd.entrySet()) {
            entry.getKey().methods.add(entry.getValue());
        }

        constantPool.writeConstantPool();


        Files.walk(Paths.get("dev.name.virtualizer/target/classes/xyz")).forEach(path -> {
            if (Files.isRegularFile(path)) {
                try {
                    byte[] content = Files.readAllBytes(path);

                    jar.write(Paths.get("dev.name.virtualizer/target/classes/").relativize(path).toString().replace("\\", "/"), content);
                } catch (IOException e) {
                    System.err.println("Failed to read file: " + path + " - " + e.getMessage());
                }
            }
        });

        jar.write(Engine.DEFAULT_CONSTANT_POOL_FILENAME, virtualized.toByteArray());
        jar.export(output);
    }
}