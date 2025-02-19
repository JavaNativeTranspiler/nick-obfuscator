package xyz.qreaj.bytecodeVirtualizer.transformers;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Type;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.Opcodes;
import xyz.qreaj.bytecodeVirtualizer.Transformer;
import xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;

import static xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils.getString;

public class StringTransformer extends Transformer {
    @Override
    public boolean canTransform(final Node node) {
        return ASMUtils.isString(node);
    }

    @Override
    public void transform(final Node node, final Method method, final Class klass, final ConstantPoolWriter constantPool, final BytecodeWriter writer) throws Exception {
        writer.writeVarString(getString(node));
        final int[] location = constantPool.addModuleByteArray(writer.toBytes());
        final Instructions instructions = invoker(klass, method,location[0], location[1]);
        instructions.add(new Type(Opcodes.CHECKCAST, "java/lang/String"));
        node.replace(instructions);
    }
}