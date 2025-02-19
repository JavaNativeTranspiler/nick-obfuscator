package xyz.qreaj.virtualizer.opcodes.stack;

import xyz.qreaj.virtualizer.opcodes.type.Opcode;

import java.io.DataInputStream;
import java.io.IOException;

public class LABEL extends Opcode {
    public short index;
    public short line;

    @Override
    public void readData(final DataInputStream dis) throws IOException {
        this.line = dis.readShort();
    }
}