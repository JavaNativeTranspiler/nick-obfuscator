package xyz.qreaj.virtualizer.opcodes;

import xyz.qreaj.virtualizer.opcodes.compare.*;
import xyz.qreaj.virtualizer.opcodes.invoke.INVOKE;
import xyz.qreaj.virtualizer.opcodes.math.*;
import xyz.qreaj.virtualizer.opcodes.stack.*;
import xyz.qreaj.virtualizer.opcodes.type.Opcode;
import xyz.qreaj.virtualizer.opcodes.variables.*;

public class OpcodeFactory {
    public static Opcode createOpcode(final int id) {
        return switch (VirtualizerOpcodes.opcodes[id]) {
            case BOOLEAN -> new BOOLEAN();
            case BYTE -> new BYTE();
            case CHAR -> new CHAR();
            case DOUBLE -> new DOUBLE();
            case FLOAT -> new FLOAT();
            case INT -> new INT();
            case LONG -> new LONG();
            case NULL -> new NULL();
            case OBJECT -> new OBJECT();
            case SHORT -> new SHORT();
            case STRING -> new STRING();
            case CAST -> new CAST();
            case GOTO -> new GOTO();
            case LABEL -> new LABEL();
            case LOAD -> new LOAD();
            case RUNTIME_LOAD -> new RUNTIME_LOAD();
            case RUNTIME_STORE -> new RUNTIME_STORE();
            case STORE -> new STORE();
            case ADD -> new ADD();
            case AND -> new AND();
            case DIVIDE -> new DIVIDE();
            case LEFT_SHIFT -> new LEFT_SHIFT();
            case MODULUS -> new MODULUS();
            case MULTIPLY -> new MULTIPLY();
            case NEGATION -> new NEGATION();
            case OR -> new OR();
            case RIGHT_SHIFT -> new RIGHT_SHIFT();
            case SUBTRACT -> new SUBTRACT();
            case UNSIGNED_RIGHT_SHIFT -> new UNSIGNED_RIGHT_SHIFT();
            case XOR -> new XOR();
            case INVOKE -> new INVOKE();
            case IF_EQUALS -> new IF_EQUALS();
            case IF_GREATER -> new IF_GREATER();
            case IF_GREATER_AND_EQUALS -> new IF_GREATER_OR_EQUALS();
            case IF_LESS -> new IF_LESS();
            case IF_LESS_AND_EQUALS -> new IF_LESS_OR_EQUALS();
            case IF_NOT_EQUALS -> new IF_NOT_EQUALS();
            case IF_NOT_NULL -> new IF_NOT_NULL();
            case IF_NULL -> new IF_NULL();
            default -> throw new IllegalStateException("Unexpected value: " + VirtualizerOpcodes.opcodes[id] + " ID: " + id);
        };
    }
}