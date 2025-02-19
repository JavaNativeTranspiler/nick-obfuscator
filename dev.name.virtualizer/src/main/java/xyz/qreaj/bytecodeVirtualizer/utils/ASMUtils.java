package xyz.qreaj.bytecodeVirtualizer.utils;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.types.Node;
import dev.name.util.java.ClassPool;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ASMUtils {
    public static boolean isNumber(final Node node) {
        return switch (node.opcode) {
            case ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, BIPUSH, SIPUSH -> true;
            case LDC -> (((Constant) node).cst instanceof Number);
            default -> false;
        };
    }

    public static Number getNumber(final Node node) {
        if (node.opcode >= ICONST_M1 && node.opcode <= ICONST_5) return node.opcode - 3;
        else if (node instanceof Constant constant && constant.cst instanceof Number number) return number;
        return 0;
    }

    public static boolean isMathOperator(final Node node) {
        return switch (node.opcode) {
            case IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM,
                 LREM, FREM, DREM, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IXOR, LXOR, IAND, LAND, IOR, LOR, INEG, LNEG,
                 FNEG, DNEG, IINC -> true;
            default -> false;
        };
    }

    public static boolean isAdd(final Node node) {
        final int opcode = node.opcode;
        return opcode == IADD || opcode == LADD || opcode == FADD || opcode == DADD;
    }

    public static boolean isSubtract(final Node node) {
        final int opcode = node.opcode;
        return opcode == ISUB || opcode == LSUB || opcode == FSUB || opcode == DSUB;
    }

    public static boolean isMultiply(final Node node) {
        final int opcode = node.opcode;
        return opcode == IMUL || opcode == LMUL || opcode == FMUL || opcode == DMUL;
    }

    public static boolean isDivide(final Node node) {
        final int opcode = node.opcode;
        return opcode == IDIV || opcode == LDIV || opcode == FDIV || opcode == DDIV;
    }

    public static boolean isModulus(final Node node) {
        final int opcode = node.opcode;
        return opcode == IREM || opcode == LREM || opcode == FREM || opcode == DREM;
    }

    public static boolean isLeftShift(final Node node) {
        final int opcode = node.opcode;
        return opcode == ISHL || opcode == LSHL;
    }

    public static boolean isRightShift(final Node node) {
        final int opcode = node.opcode;
        return opcode == ISHR || opcode == LSHR;
    }

    public static boolean isUnsignedRightShift(final Node node) {
        final int opcode = node.opcode;
        return opcode == IUSHR || opcode == LUSHR;
    }

    public static boolean isXor(final Node node) {
        final int opcode = node.opcode;
        return opcode == IXOR || opcode == LXOR;
    }

    public static boolean isAnd(final Node node) {
        final int opcode = node.opcode;
        return opcode == IAND || opcode == LAND;
    }

    public static boolean isOr(final Node node) {
        final int opcode = node.opcode;
        return opcode == IOR || opcode == LOR;
    }

    public static boolean isNegate(final Node node) {
        final int opcode = node.opcode;
        return opcode == INEG || opcode == LNEG || opcode == FNEG || opcode == DNEG;
    }

    public static boolean isIncrement(final Node node) {
        final int opcode = node.opcode;
        return opcode == IINC;
    }

    public static Number number(final Node node) {
        return switch (node.opcode) {
            case IADD, ISUB, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IXOR, IAND, IOR, INEG, IINC -> 0;
            case LADD, LSUB, LMUL, LDIV, LREM, LSHL, LSHR, LUSHR, LXOR, LAND, LOR, LNEG -> 0L;
            case FADD, FSUB, FMUL, FDIV, FREM, FNEG -> 0.0F;
            case DADD, DSUB, DMUL, DDIV, DREM, DNEG -> 0.0D;
            default -> null;
        };
    }

    public static Method getMethod(final Class node, final String name, final String desc) {
        return node.methods.stream()
                .filter(m -> m.name.equals(name))
                .filter(m -> m.desc.equals(desc))
                .findAny()
                .orElse(null);
    }


    public static Class getClass(final ClassPool pool, final String name) {
        return pool.getClasses().stream()
                .filter(cn -> cn.name.equals(name))
                .findAny()
                .orElse(null);
    }

    public static boolean isString(final Node node) {
        return node instanceof Constant constant && constant.cst instanceof String;
    }

    public static String getString(final Node node) {
        return node instanceof Constant constant ? (String) constant.cst : null;
    }

    public static Instructions primitiveCast(final Number number) {
        final InstructionBuilder builder = InstructionBuilder.generate();

        switch (number.getClass().getSimpleName()) {
            case "Double" -> {
                builder.checkcast("java/lang/Double");
                builder.invokevirtual("java/lang/Double", "doubleValue", "()D", false);
            }
            case "Float" -> {
                builder.checkcast("java/lang/Float");
                builder.invokevirtual("java/lang/Float", "floatValue", "()F", false);
            }
            case "Long" -> {
                builder.checkcast("java/lang/Long");
                builder.invokevirtual("java/lang/Long", "longValue", "()J", false);
            }
            case "Integer" -> {
                builder.checkcast("java/lang/Integer");
                builder.invokevirtual("java/lang/Integer", "intValue", "()I", false);
            }
            case "Short" -> {
                final short s = (short) number;
                if (s == 1) {
                    builder.checkcast("java/lang/Boolean");
                    builder.invokevirtual("java/lang/Boolean", "booleanValue", "()Z", false);
                    break;
                } else if (s == 2) {
                    builder.checkcast("java/lang/Character");
                    builder.invokevirtual("java/lang/Character", "charValue", "()C", false);
                    break;
                }
                builder.checkcast("java/lang/Short");
                builder.invokevirtual("java/lang/Short", "shortValue", "()S", false);
            }
            case "Byte" -> {
                builder.checkcast("java/lang/Byte");
                builder.invokevirtual("java/lang/Byte", "byteValue", "()B", false);
            }
            default -> throw new IllegalArgumentException("Unsupported number type: " + number.getClass().getName());
        }

        return builder.build();
    }

    public static Instructions cast(final Number number) {
        final InstructionBuilder builder = InstructionBuilder.generate();

        switch (number.getClass().getSimpleName()) {
            case "Double" -> builder.invokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            case "Float" -> builder.invokestatic("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            case "Long" -> builder.invokestatic("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            case "Integer" -> builder.invokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            case "Short" -> {
                final short s = number.shortValue();
                if (s == 1) builder.invokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                else if (s == 2) builder.invokestatic("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                else builder.invokestatic("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            }
            case "Byte" -> builder.invokestatic("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            default -> throw new IllegalArgumentException("Unsupported number type: " + number.getClass().getName());
        }

        return builder.build();
    }

    public static Number number(String className) {
        return switch (className) {
            case "double" -> 0.0;
            case "float" -> 0f;
            case "long" -> 0L;
            case "int" -> 0;
            case "short" -> (short) 0;
            case "byte" -> (byte) 0;
            case "boolean" -> (short) 1;
            case "char" -> (short) 2;
            default -> null;
        };
    }

    public static Type type(Number number) {
        return switch (number.getClass().getName()) {
            case "java.lang.Double" -> Type.DOUBLE_TYPE;
            case "java.lang.Float" -> Type.FLOAT_TYPE;
            case "java.lang.Long" -> Type.LONG_TYPE;
            case "java.lang.Integer" -> Type.INT_TYPE;
            case "java.lang.Short" -> {
                short s = (short) number;
                if (s == 1) yield Type.BOOLEAN_TYPE;
                else if (s == 2) yield Type.CHAR_TYPE;
                yield Type.SHORT_TYPE;
            }
            case "java.lang.Byte" -> Type.BYTE_TYPE;
            default -> null;
        };
    }
}