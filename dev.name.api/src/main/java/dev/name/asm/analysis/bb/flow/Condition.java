package dev.name.asm.analysis.bb.flow;

import org.objectweb.asm.Opcodes;

@SuppressWarnings("unused")
public enum Condition implements Opcodes {
    NOT_NULL(IFNONNULL),
    NULL(IFNULL),
    OBJECT_EQUAL(IF_ACMPEQ),
    OBJECT_NOT_EQUAL(IF_ACMPNE),
    INT_EQUAL(IF_ICMPEQ),
    INT_NOT_EQUAL(IF_ICMPNE),
    GREATER(IF_ICMPGT),
    GREATER_OR_EQUAL(IF_ICMPGE),
    LESS(IF_ICMPLT),
    LESS_OR_EQUAL(IF_ICMPLE),
    EQUAL_ZERO(IFEQ),
    NOT_EQUAL_ZERO(IFNE),
    GREATER_ZERO(IFGT),
    GREATER_OR_EQUAL_ZERO(IFGE),
    LESS_ZERO(IFLT),
    LESS_OR_EQUAL_ZERO(IFLE);

    public final int opcode;
    private Condition negated;

    Condition(int opcode) {
        this.opcode = opcode;
    }

    static {
        NOT_NULL.negated = NULL;
        NULL.negated = NOT_NULL;
        OBJECT_EQUAL.negated = OBJECT_NOT_EQUAL;
        OBJECT_NOT_EQUAL.negated = OBJECT_EQUAL;
        INT_EQUAL.negated = INT_NOT_EQUAL;
        INT_NOT_EQUAL.negated = INT_EQUAL;
        GREATER.negated = LESS_OR_EQUAL;
        GREATER_OR_EQUAL.negated = LESS;
        LESS.negated = GREATER_OR_EQUAL;
        LESS_OR_EQUAL.negated = GREATER;
        EQUAL_ZERO.negated = NOT_EQUAL_ZERO;
        NOT_EQUAL_ZERO.negated = EQUAL_ZERO;
        GREATER_ZERO.negated = LESS_OR_EQUAL_ZERO;
        GREATER_OR_EQUAL_ZERO.negated = LESS_ZERO;
        LESS_ZERO.negated = GREATER_OR_EQUAL_ZERO;
        LESS_OR_EQUAL_ZERO.negated = GREATER_ZERO;
    }

    public Condition negate() {
        return negated;
    }

    public static Condition fromOpcode(int opcode) {
        return switch (opcode) {
            case IFEQ -> EQUAL_ZERO;
            case IFNE -> NOT_EQUAL_ZERO;
            case IFGT -> GREATER_ZERO;
            case IFGE -> GREATER_OR_EQUAL_ZERO;
            case IFLT -> LESS_ZERO;
            case IFLE -> LESS_OR_EQUAL_ZERO;
            case IF_ICMPGT -> GREATER;
            case IF_ICMPGE -> GREATER_OR_EQUAL;
            case IF_ICMPLT -> LESS;
            case IF_ICMPLE -> LESS_OR_EQUAL;
            case IF_ACMPEQ -> OBJECT_EQUAL;
            case IF_ACMPNE -> OBJECT_NOT_EQUAL;
            case IFNULL -> NULL;
            case IFNONNULL -> NOT_NULL;
            default -> throw new IllegalStateException();
        };
    }

    public String asString(String l, String r) {
        return switch (this) {
            case NULL -> l + " == null";
            case NOT_NULL -> l + " != null";
            case EQUAL_ZERO -> l + " == 0";
            case NOT_EQUAL_ZERO -> l + " != 0";
            case GREATER_ZERO -> l + " > 0";
            case GREATER_OR_EQUAL_ZERO -> l + " >= 0";
            case LESS_ZERO -> l + " < 0";
            case LESS_OR_EQUAL_ZERO -> l + " <= 0";
            case INT_EQUAL, OBJECT_EQUAL -> l + " == " + r;
            case INT_NOT_EQUAL, OBJECT_NOT_EQUAL -> l + " != " + r;
            case GREATER -> l + " > " + r;
            case GREATER_OR_EQUAL -> l + " >= " + r;
            case LESS -> l + " < " + r;
            case LESS_OR_EQUAL -> l + " <= " + r;
        };
    }
}