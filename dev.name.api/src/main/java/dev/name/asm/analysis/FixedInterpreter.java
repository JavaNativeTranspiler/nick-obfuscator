package dev.name.asm.analysis;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

public class FixedInterpreter extends Interpreter<BasicValue> implements Opcodes {
    public static final Type NULL_TYPE = Type.getObjectType("null");

    public FixedInterpreter() {
        super(ASM9);
    }

    @Override
    public BasicValue newValue(final Type type) {
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        return switch (type.getSort()) {
            case Type.VOID -> null;
            case Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT -> BasicValue.INT_VALUE;
            case Type.FLOAT -> BasicValue.FLOAT_VALUE;
            case Type.LONG -> BasicValue.LONG_VALUE;
            case Type.DOUBLE -> BasicValue.DOUBLE_VALUE;
            case Type.ARRAY, Type.OBJECT -> new BasicValue(type);
            default -> throw new AssertionError();
        };
    }

    @Override
    public BasicValue newOperation(final AbstractInsnNode node) throws AnalyzerException {
        return switch (node.getOpcode()) {
            case ACONST_NULL -> newValue(NULL_TYPE);
            case ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, BIPUSH, SIPUSH -> BasicValue.INT_VALUE;
            case LCONST_0, LCONST_1 -> BasicValue.LONG_VALUE;
            case FCONST_0, FCONST_1, FCONST_2 -> BasicValue.FLOAT_VALUE;
            case DCONST_0, DCONST_1 -> BasicValue.DOUBLE_VALUE;
            case LDC -> {
                Object value = ((LdcInsnNode) node).cst;
                if (value instanceof Integer) yield BasicValue.INT_VALUE;
                if (value instanceof Float) yield BasicValue.FLOAT_VALUE;
                if (value instanceof Long) yield BasicValue.LONG_VALUE;
                if (value instanceof Double) yield BasicValue.DOUBLE_VALUE;
                if (value instanceof String) yield newValue(Type.getObjectType("java/lang/String"));
                if (value instanceof Type) {
                    int sort = ((Type) value).getSort();
                    if (sort == Type.OBJECT || sort == Type.ARRAY) yield newValue(Type.getObjectType("java/lang/Class"));
                    if (sort == Type.METHOD) yield newValue(Type.getObjectType("java/lang/invoke/MethodType"));
                    throw new AnalyzerException(node, "Illegal LDC value " + value);
                }
                if (value instanceof Handle) yield newValue(Type.getObjectType("java/lang/invoke/MethodHandle"));
                if (value instanceof ConstantDynamic) yield newValue(Type.getType(((ConstantDynamic) value).getDescriptor()));
                throw new AnalyzerException(node, "Illegal LDC value " + value);
            }
            case JSR -> BasicValue.RETURNADDRESS_VALUE;
            case GETSTATIC -> newValue(Type.getType(((FieldInsnNode) node).desc));
            case NEW -> newValue(Type.getObjectType(((TypeInsnNode) node).desc));
            default -> throw new AssertionError();
        };
    }

    @Override
    public BasicValue copyOperation(final AbstractInsnNode node, final BasicValue value) {
        return value;
    }

    @Override
    public BasicValue unaryOperation(final AbstractInsnNode node, final BasicValue value) throws AnalyzerException {
        return switch (node.getOpcode()) {
            case INEG, IINC, L2I, F2I, D2I, I2B, I2C, I2S, INSTANCEOF, ARRAYLENGTH -> BasicValue.INT_VALUE;
            case FNEG, I2F, L2F, D2F -> BasicValue.FLOAT_VALUE;
            case LNEG, I2L, F2L, D2L -> BasicValue.LONG_VALUE;
            case DNEG, I2D, L2D, F2D -> BasicValue.DOUBLE_VALUE;
            case IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, TABLESWITCH, LOOKUPSWITCH, IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, PUTSTATIC, MONITORENTER, MONITOREXIT, IFNULL, IFNONNULL, ATHROW -> null;
            case GETFIELD -> newValue(Type.getType(((FieldInsnNode) node).desc));
            case NEWARRAY -> switch (((IntInsnNode) node).operand) {
                case T_BOOLEAN -> newValue(Type.getType("[Z"));
                case T_CHAR -> newValue(Type.getType("[C"));
                case T_BYTE -> newValue(Type.getType("[B"));
                case T_SHORT -> newValue(Type.getType("[S"));
                case T_INT -> newValue(Type.getType("[I"));
                case T_FLOAT -> newValue(Type.getType("[F"));
                case T_DOUBLE -> newValue(Type.getType("[D"));
                case T_LONG -> newValue(Type.getType("[J"));
                default -> throw new AnalyzerException(node, "Invalid array type");
            };
            case ANEWARRAY -> newValue(Type.getType("[" + Type.getObjectType(((TypeInsnNode) node).desc)));
            case CHECKCAST -> newValue(Type.getObjectType(((TypeInsnNode) node).desc));
            default -> throw new AssertionError();
        };
    }

    @Override
    public BasicValue binaryOperation(final AbstractInsnNode node, final BasicValue v1, final BasicValue v2) {
        return switch (node.getOpcode()) {
            case IALOAD, BALOAD, CALOAD, SALOAD, IADD, ISUB, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR, LCMP, FCMPL, FCMPG, DCMPL, DCMPG -> BasicValue.INT_VALUE;
            case FALOAD, FADD, FSUB, FMUL, FDIV, FREM -> BasicValue.FLOAT_VALUE;
            case LALOAD, LADD, LSUB, LMUL, LDIV, LREM, LSHL, LSHR, LUSHR, LAND, LOR, LXOR -> BasicValue.LONG_VALUE;
            case DALOAD, DADD, DSUB, DMUL, DDIV, DREM -> BasicValue.DOUBLE_VALUE;
            case AALOAD -> BasicValue.REFERENCE_VALUE;
            case IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, PUTFIELD -> null;
            default -> throw new AssertionError();
        };
    }

    @Override
    public BasicValue ternaryOperation(final AbstractInsnNode node, final BasicValue v1, final BasicValue v2, final BasicValue v3) {
        return null;
    }

    @Override
    public BasicValue naryOperation(final AbstractInsnNode node, final List<? extends BasicValue> values) {
        return switch (node.getOpcode()) {
            case MULTIANEWARRAY -> newValue(Type.getType(((MultiANewArrayInsnNode) node).desc));
            case INVOKEDYNAMIC -> newValue(Type.getReturnType(((InvokeDynamicInsnNode) node).desc));
            default -> newValue(Type.getReturnType(((MethodInsnNode) node).desc));
        };
    }

    @Override public void returnOperation(final AbstractInsnNode node, final BasicValue value, final BasicValue expected) {}

    @Override
    public BasicValue merge(final BasicValue value1, final BasicValue value2) {
        if (!value1.equals(value2)) return BasicValue.UNINITIALIZED_VALUE;
        return value1;
    }
}