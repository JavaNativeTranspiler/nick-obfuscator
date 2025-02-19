package xyz.qreaj.virtualizer.opcodes;

public enum VirtualizerOpcodes {

    // variables opcodes
    BOOLEAN,
    BYTE,
    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    NULL,
    OBJECT,
    SHORT,
    STRING,

    // stack opcodes
    CAST,
    GOTO,
    LABEL,
    LOAD,
    RETURN,
    RUNTIME_LOAD,
    RUNTIME_STORE,
    STORE,

    // math opcodes
    ADD,
    AND,
    DIVIDE,
    LEFT_SHIFT,
    MODULUS,
    MULTIPLY,
    NEGATION,
    OR,
    RIGHT_SHIFT,
    SUBTRACT,
    UNSIGNED_RIGHT_SHIFT,
    XOR,

    // invoke
    INVOKE,

    // compare opcodes
    IF_EQUALS,
    IF_GREATER,
    IF_GREATER_AND_EQUALS,
    IF_LESS,
    IF_LESS_AND_EQUALS,
    IF_NOT_EQUALS,
    IF_NOT_NULL,
    IF_NULL;

    public static VirtualizerOpcodes[] opcodes = VirtualizerOpcodes.values();
}
