package xyz.qreaj.virtualizer.opcodes.variables;

import xyz.qreaj.virtualizer.opcodes.type.VariableOpcode;

import java.io.DataInputStream;
import java.io.IOException;

public class LONG extends VariableOpcode {
    @Override
    public void readData(final DataInputStream dis) throws IOException {
        value = dis.readLong();
    }
}