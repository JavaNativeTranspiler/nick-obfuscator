package xyz.qreaj.virtualizer.opcodes.stack;

import xyz.qreaj.virtualizer.opcodes.OpcodeFactory;
import xyz.qreaj.virtualizer.opcodes.type.Opcode;
import xyz.qreaj.virtualizer.opcodes.type.VariableOpcode;

import java.io.DataInputStream;
import java.io.IOException;

public class CAST extends Opcode {
    public VariableOpcode variable;

    @Override
    public void readData(final DataInputStream dis) throws IOException {
        variable = (VariableOpcode) OpcodeFactory.createOpcode(dis.readShort());
    }
}