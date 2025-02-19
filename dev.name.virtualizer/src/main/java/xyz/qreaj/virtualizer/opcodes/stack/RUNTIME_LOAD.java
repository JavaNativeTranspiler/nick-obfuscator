package xyz.qreaj.virtualizer.opcodes.stack;

import xyz.qreaj.virtualizer.opcodes.type.Opcode;

import java.io.DataInputStream;
import java.io.IOException;

public class RUNTIME_LOAD extends Opcode {
    public short id;

    @Override
    public void readData(final DataInputStream dis) throws IOException {
        id = dis.readShort();
    }
}