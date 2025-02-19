package xyz.qreaj.virtualizer.opcodes.type;

import xyz.qreaj.virtualizer.engine.ConstantPoolReader;
import xyz.qreaj.virtualizer.engine.Engine;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Opcode {
    public abstract void readData(final DataInputStream dis) throws IOException;
    public ConstantPoolReader pool = Engine.getConstantPoolReader();
}