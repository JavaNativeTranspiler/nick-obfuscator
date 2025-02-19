package xyz.qreaj.bytecodeVirtualizer.transformers;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Jump;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.Type;
import xyz.qreaj.bytecodeVirtualizer.Transformer;
import xyz.qreaj.bytecodeVirtualizer.utils.ASMUtils;
import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.writer.BytecodeWriter;
import xyz.qreaj.virtualizer.writer.LabelIndex;

import static org.objectweb.asm.Opcodes.*;
import static xyz.qreaj.virtualizer.opcodes.VirtualizerOpcodes.*;
public class ConditionJumpTransformer extends Transformer{

    @Override
    public boolean canTransform(Node node) {
        return node.opcode >= IFEQ && node.opcode <= IF_ICMPLE;
    }

    @Override
    public void transform(Node node, Method method, Class klass, ConstantPoolWriter constantPool, BytecodeWriter writer) throws Exception {
        final Jump jump = (Jump) node;

        LabelIndex falseLabel = new LabelIndex();
        LabelIndex trueLabel = new LabelIndex();
        LabelIndex skipTrueLabel = new LabelIndex();

        if (node.opcode >= IFEQ && node.opcode <= IFLE) {
            writer.writeVarInt(0);
            writer.writeRuntimeLoad(0);
        } else  {
            writer.writeRuntimeLoad(0);
            writer.writeRuntimeLoad(1);
        }

        switch (jump.opcode) {
            case IFEQ, IF_ICMPEQ -> writer.writeDoubleConditionJump(IF_EQUALS, trueLabel);
            case IFNE, IF_ICMPNE -> writer.writeDoubleConditionJump(IF_NOT_EQUALS, trueLabel);
            case IFLT, IF_ICMPLT -> writer.writeDoubleConditionJump(IF_LESS, trueLabel);
            case IFGE, IF_ICMPGE -> writer.writeDoubleConditionJump(IF_GREATER_AND_EQUALS, trueLabel);
            case IFGT, IF_ICMPGT -> writer.writeDoubleConditionJump(IF_GREATER, trueLabel);
            case IFLE, IF_ICMPLE -> writer.writeDoubleConditionJump(IF_LESS_AND_EQUALS, trueLabel);
        }
        writer.writeLabel(falseLabel);
        writer.writeVarBoolean(false);
        writer.writeGoto(skipTrueLabel);

        writer.writeLabel(trueLabel);
        writer.writeVarBoolean(true);
        writer.writeLabel(skipTrueLabel);

        final int[] location = constantPool.addModuleByteArray(writer.toBytes());

        Type[] types = null;
        if (node.opcode >= IFEQ && node.opcode <= IFLE)  types = new Type[]{Type.INT_TYPE};
        else types = new Type[]{Type.INT_TYPE,Type.INT_TYPE};


        final Instructions instructions = invoker(klass, method, location[0], location[1], types);
        final InstructionBuilder builder = InstructionBuilder.generate();

        instructions.add(ASMUtils.primitiveCast((short)1));
        builder.ifne(jump.label);

        instructions.add(builder.build());

        node.replace(instructions);
    }
}