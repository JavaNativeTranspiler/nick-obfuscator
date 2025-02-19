package xyz.qreaj.virtualizer.opcodes.invoke;

public enum InvokeType {
    STATIC,
    SPECIAL,
    VIRTUAL;

    public byte toByte() {
        return (byte) this.ordinal();
    }

    private static final InvokeType[] values = InvokeType.values();

    public static InvokeType of(final byte val) {
        return values[val];
    }
}