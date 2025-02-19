package xyz.qreaj.virtualizer.opcodes.type;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class SingleConditionJumpOpcode extends Opcode {
    public short offset;

    public void readData(final DataInputStream dis) throws IOException {
        this.offset = dis.readShort();
    }

    public abstract boolean getResult(final Object n);
}