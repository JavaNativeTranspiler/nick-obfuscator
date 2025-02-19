package xyz.qreaj.bytecodeVirtualizer.transformers;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Instruction;
import dev.name.asm.ir.nodes.Invoke;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.Type;
import xyz.qreaj.bytecodeVirtualizer.Transformer;
import xyz.qreaj.bytecodeVirtualizer.Virtualizer;
import xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.opcodes.invoke.InvokeType;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;

import static org.objectweb.asm.Opcodes.*;
import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.number;
import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.primitiveCast;

public class InvokeTransformer extends Transformer {
    @Override
    public boolean canTransform(final Node node) {
        return node instanceof Invoke && node.opcode == INVOKEVIRTUAL || node.opcode == INVOKESTATIC;
    }

    @Override
    public void transform(final Node node, final Method method, final Class klass, final ConstantPoolWriter constantPool, final BytecodeWriter writer) throws Exception {
        final Invoke invoke = (Invoke) node;

        final InvokeType type = (node.opcode == INVOKESTATIC) ? InvokeType.STATIC : InvokeType.VIRTUAL;

        if (type == InvokeType.VIRTUAL && invoke.owner.startsWith("[")) {
            System.out.println("Unsupported array method caller owner " + invoke.owner);
            return;
        }

        final Type methodType = Type.getMethodType(invoke.desc);
        final Type ret = methodType.getReturnType();
        Type[] args = methodType.getArgumentTypes();

        if (type == InvokeType.VIRTUAL) {
            final Type[] virtualArgs = new Type[args.length + 1];
            System.arraycopy(args, 0, virtualArgs, 1, args.length);
            virtualArgs[0] = Type.getType(Object.class);
            args = virtualArgs;
        }

        for (int i = 0; i < args.length; i++) writer.writeRuntimeLoad(i);

        String owner = invoke.owner;
        Class parent = ASMUtils.getClass(Virtualizer.classes, invoke.owner);
        if (parent != null) {
            Method invokedMethod = ASMUtils.getMethod(parent, invoke.name, invoke.desc);
            if (invokedMethod == null) {
                while (invokedMethod == null && !parent.superName.isEmpty()) {
                    parent = ASMUtils.getClass(Virtualizer.classes, parent.superName);
                    if (parent == null) break;
                    invokedMethod = ASMUtils.getMethod(parent, invoke.name, invoke.desc);
                }
                if (invokedMethod != null) owner = parent.name;
            }
        }

        writer.writeInvoke(type, owner.replace("/", "."), invoke.name.replace("/", "."), invoke.desc.replace("/", "."));

        final int[] location = constantPool.addModuleByteArray(writer.toBytes());
        final Instructions instructions = invoker(klass, method, location[0], location[1], args);

        final Number number = number(ret.getClassName());
        if (number != null) instructions.add(primitiveCast(number));
        else {
            if (!ret.getInternalName().equals("V")) instructions.add(new dev.name.asm.ir.nodes.Type(CHECKCAST, ret.getInternalName()));
            else instructions.add(new Instruction(POP));
        }

        node.replace(instructions);

    }
}