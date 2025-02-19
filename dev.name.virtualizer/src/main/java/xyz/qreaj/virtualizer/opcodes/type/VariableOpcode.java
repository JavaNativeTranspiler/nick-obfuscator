package xyz.qreaj.virtualizer.opcodes.type;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class VariableOpcode extends Opcode {
    public Object value = null;
    public abstract void readData(final DataInputStream dis) throws IOException;
}