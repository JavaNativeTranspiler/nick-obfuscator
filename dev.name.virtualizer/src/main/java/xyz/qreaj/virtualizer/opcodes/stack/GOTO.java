package xyz.qreaj.virtualizer.opcodes.stack;

import xyz.qreaj.virtualizer.opcodes.type.Opcode;

import java.io.DataInputStream;
import java.io.IOException;

public class GOTO extends Opcode {
    public short offset;

    @Override
    public void readData(final DataInputStream dis) throws IOException {
      this.offset = dis.readShort();
    }
}