package xyz.qreaj.virtualizer.opcodes.type;

import java.io.DataInputStream;

public abstract class ArithmeticOpcode extends Opcode {
    public boolean inputIs2ArgsElseOne = true;
    @Override public void readData(final DataInputStream dis) {}
    public abstract Object calculate(Object o1, Object o2);
}