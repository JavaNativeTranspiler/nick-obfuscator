package xyz.qreaj.bytecodeVirtualizer;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Field;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Instruction;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;

import java.util.ArrayList;
import java.util.List;

import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.cast;
import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.number;

public abstract class Transformer {
    public abstract boolean canTransform(final Node node);

    public abstract void transform(final Node node, final Method method, final Class klass, final ConstantPoolWriter constantPool, final BytecodeWriter writer) throws Exception;

    private void createEngineObject(final Class klass, final int start, final int end) {
        if (lastKlass == null) lastKlass = klass;
        if (lastKlass != klass) locationList.clear();
        if (locationList.contains(start)) return;

        final InstructionBuilder builder = InstructionBuilder.generate();
        locationList.add(start);
        Method method = klass.getMethod("<clinit>", "()V");

        Field field = new Field(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC, "MOD_" + start + "_" + end, "Lxyz/qreaj/virtualizer/engine/Engine;", null, null);
        klass.fields.add(field);
        if (method == null) {
            method = Virtualizer.methodsToAdd.get(klass);
            if (method == null) {
                method = new Method(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                method.instructions.add(new Instruction(Opcodes.RETURN));
                Virtualizer.methodsToAdd.put(klass, method);
            }
        }
        builder._new("xyz/qreaj/virtualizer/engine/Engine").dup();
        builder.ldc(start).ldc(end);
        builder.invokespecial("xyz/qreaj/virtualizer/engine/Engine", "<init>", "(II)V");
        builder.putstatic(klass.name, "MOD_" + start + "_" + end, "Lxyz/qreaj/virtualizer/engine/Engine;");
        method.instructions.insert(builder.build());

    }

    /*    public final Instructions invoker(final int start, final int end) {
            final InstructionBuilder builder = InstructionBuilder.generate();

            builder._new("xyz/qreaj/virtualizer/engine/Engine").dup();
            builder.ldc(start).ldc(end);
            builder.invokespecial("xyz/qreaj/virtualizer/engine/Engine", "<init>", "(II)V");
            builder.iconst_0().anewarray("java/lang/Object");
            builder.invokevirtual("xyz/qreaj/virtualizer/engine/Engine", "execute", "([Ljava/lang/Object;)Ljava/lang/Object;", false);

            return builder.build();
        }*/
    private final List<Integer> locationList = new ArrayList<>();

    private Class lastKlass = null;

    public final Instructions invoker(final Class klass, final Method method, final int start, final int end) {
        final InstructionBuilder builder = InstructionBuilder.generate();
        createEngineObject(klass, start, end);
        builder.getstatic(klass.name, "MOD_" + start + "_" + end, "Lxyz/qreaj/virtualizer/engine/Engine;");
        builder.iconst_0().anewarray("java/lang/Object");
        builder.invokevirtual("xyz/qreaj/virtualizer/engine/Engine", "execute", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        lastKlass = klass;
        return builder.build();
    }

    public final Instructions invoker(final Class klass, final Method method, final int start, final int end, final Type[] arguments) {
        final InstructionBuilder builder = InstructionBuilder.generate();
        final int len = arguments.length;
        final LocalPool pool = new LocalPool(method);
        final LocalPool.Local[] locals = new LocalPool.Local[255]; // increase the size if needed

        for (int i = len - 1; i >= 0; i--) {
            final Number number = number(arguments[i].getClassName());
            if (number != null) builder.add(cast(number));
            final int index = (len - 1 - i);
            if (locals[index] == null) locals[index] = pool.allocate(LocalPool.OBJECT);
            builder.astore(locals[index].index);
        }

        createEngineObject(klass, start, end);
        builder.getstatic(klass.name, "MOD_" + start + "_" + end, "Lxyz/qreaj/virtualizer/engine/Engine;");

        builder.ldc(len).anewarray("java/lang/Object");

        for (int i = 0; i < len; i++) {
            builder.dup();
            builder.ldc(i);
            builder.aload(locals[i].index);
            builder.aastore();
        }

        builder.invokevirtual("xyz/qreaj/virtualizer/engine/Engine", "execute", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        lastKlass = klass;
        return builder.build();
    }
}