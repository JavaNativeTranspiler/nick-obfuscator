package xyz.qreaj.bytecodeVirtualizer.transformers;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Increment;
import dev.name.asm.ir.nodes.Variable;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import xyz.qreaj.bytecodeVirtualizer.Transformer;
import xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.opcodes.VirtualizerOpcodes;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.*;
import static xyz.qreaj.virtualizer.opcodes.VirtualizerOpcodes.*;

public class MathTransformer extends Transformer implements Opcodes {
    public static final Map<VirtualizerOpcodes, Predicate<Node>> OPERATION_MAP = new HashMap<>() {
        {
            put(ADD, ASMUtils::isAdd);
            put(SUBTRACT, ASMUtils::isSubtract);
            put(DIVIDE, ASMUtils::isDivide);
            put(MULTIPLY, ASMUtils::isMultiply);
            put(XOR, ASMUtils::isXor);
            put(LEFT_SHIFT, ASMUtils::isLeftShift);
            put(RIGHT_SHIFT, ASMUtils::isRightShift);
            put(UNSIGNED_RIGHT_SHIFT, ASMUtils::isUnsignedRightShift);
            put(MODULUS, ASMUtils::isModulus);
            put(AND, ASMUtils::isAnd);
            put(OR, ASMUtils::isOr);
            put(NEGATION, ASMUtils::isNegate);
        }
    };

    @Override
    public boolean canTransform(final Node node) {
       // if (node.opcode == LOR) return false;
        return isMathOperator(node);
    }

    @Override
    public void transform(final Node node, final Method method, final Class klass, final ConstantPoolWriter constantPool, final BytecodeWriter writer) throws Exception {
        writer.writeRuntimeLoad(0);
        if (!isNegate(node) && !isIncrement(node)) writer.writeRuntimeLoad(1);
        final Number number = number(node);
        if (number == null) throw new IllegalStateException();

        if (node instanceof Increment increment) {
            writer.writeVarInt(increment.amount);
            writer.writeOpcode(ADD);
        }

        OPERATION_MAP.forEach((type, predicate) -> {
            if (!predicate.test(node)) return;
            try {
                writer.writeOpcode(type);
            } catch (final Throwable _t) { _t.printStackTrace(System.err); }
        });

        final int[] location = constantPool.addModuleByteArray(writer.toBytes());
        final Type[] types = (isNegate(node) || isIncrement(node)) ? new Type[] { type(number) } : new Type[]{ type(number), (node.opcode == LSHL || node.opcode == LSHR || node.opcode == LUSHR) ? Type.INT_TYPE : type(number) };
        final Instructions instructions = invoker(klass, method,location[0], location[1], types);
        if (node instanceof Increment increment) instructions.first.insertBefore(new Variable(ILOAD, increment.local.index));
        instructions.add(primitiveCast(number));
        if (node instanceof Increment increment) instructions.add(new Variable(ISTORE, increment.local.index));
        node.replace(instructions);
    }
}