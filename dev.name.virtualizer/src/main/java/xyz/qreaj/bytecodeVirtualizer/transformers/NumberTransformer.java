package xyz.qreaj.bytecodeVirtualizer.transformers;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.types.Node;
import xyz.qreaj.bytecodeVirtualizer.Transformer;
import xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;

import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.getNumber;
import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.primitiveCast;

public class NumberTransformer extends Transformer {
    @Override
    public boolean canTransform(final Node node) {
        return ASMUtils.isNumber(node);
    }

    @Override
    public void transform(final Node node, final Method method, final Class klass, final ConstantPoolWriter constantPool, final BytecodeWriter writer) throws Exception {
        final Number number = getNumber(node);

        switch (number.getClass().getSimpleName()) {
            case "Double" -> writer.writeVarDouble(number.doubleValue());
            case "Float" -> writer.writeVarFloat(number.floatValue());
            case "Long" -> writer.writeVarLong(number.longValue());
            case "Integer" -> writer.writeVarInt(number.intValue());
            case "Short" -> writer.writeVarShort(number.shortValue());
            case "Byte" -> writer.writeVarByte(number.byteValue());
        }

        final int[] location = constantPool.addModuleByteArray(writer.toBytes());
        final Instructions instructions = invoker(klass, method, location[0], location[1]);
        instructions.add(primitiveCast(number));
        node.replace(instructions);
    }
}