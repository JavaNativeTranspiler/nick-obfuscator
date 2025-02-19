package xyz.qreaj.virtualizer.writer;

import xyz.qreaj.virtualizer.engine.ConstantPoolWriter;
import xyz.qreaj.virtualizer.opcodes.VirtualizerOpcodes;
import xyz.qreaj.virtualizer.opcodes.invoke.InvokeType;

import java.io.*;

import static xyz.qreaj.virtualizer.opcodes.VirtualizerOpcodes.*;

public class BytecodeWriter {
    private final DataOutputStream dos;
    private ByteArrayOutputStream baos;

    private final ConstantPoolWriter writer;

    public BytecodeWriter(final String path, final ConstantPoolWriter writer) throws FileNotFoundException {
        final File file = new File(path);
        this.writer = writer;

        try {
            if (!file.exists() && !file.createNewFile()) throw new RuntimeException("Failed to construct file.");
            this.dos = new DataOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            throw new FileNotFoundException("Failed creating data output stream for writing bytecode");
        }
    }

    public BytecodeWriter(final ConstantPoolWriter writer) {
        this.writer = writer;
        this.baos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(baos);
    }

    public void writeOpcode(final VirtualizerOpcodes opcode) throws IOException {
        dos.writeShort(opcode.ordinal());
    }

    public void writeDoubleConditionJump(final VirtualizerOpcodes opcode, final LabelIndex index) throws IOException {
        if (index.index == null) index.index = LabelIndex.counter++;
        writeOpcode(opcode);
        dos.writeShort(index.index);
    }

    public void writeSingleConditionJump(final VirtualizerOpcodes opcode, final LabelIndex label) throws IOException {
        if (label.index == null) label.index = LabelIndex.counter++;
        writeOpcode(opcode);
        dos.writeShort(label.index);
    }

    public void writeStore(final int index) throws IOException {
        writeOpcode(STORE);
        dos.writeShort(index);
    }

    public void writeLoad(final int index) throws IOException {
        writeOpcode(LOAD);
        dos.writeShort(index);
    }

    public void writeRuntimeStore(final int index) throws Exception {
        writeOpcode(RUNTIME_STORE);
        dos.writeShort(index);
    }

    public void writeRuntimeLoad(final int index) throws Exception {
        writeOpcode(RUNTIME_LOAD);
        dos.writeShort(index);
    }

    public void writeInvoke(final InvokeType invoke, final String owner, final String name, final String desc) throws IOException {
        writeOpcode(INVOKE);
        dos.writeByte(invoke.toByte());

        dos.writeInt(writer.addString(owner));
        dos.writeInt(writer.addString(name));
        dos.writeInt(writer.addString(desc));

    }

    public void writeCast(final VirtualizerOpcodes cast) throws IOException {
        writeOpcode(CAST);
        writeOpcode(cast);
    }

    public void writeLabel(final LabelIndex label) throws IOException {
        if (label.index == null) label.index = LabelIndex.counter++;
        writeOpcode(LABEL);
        dos.writeShort(label.index);
    }

    public void writeGoto(final LabelIndex label) throws IOException {
        if (label.index == null) label.index = LabelIndex.counter++;
        writeOpcode(GOTO);
        dos.writeShort(label.index);
    }

    public void writeVarShort(final int i) throws IOException {
        writeOpcode(SHORT);
        dos.writeShort(i);
    }

    public void writeVarInt(final int i) throws IOException {
        writeOpcode(INT);
        dos.writeInt(i);
    }

    public void writeVarLong(final long l) throws IOException {
        writeOpcode(LONG);
        dos.writeLong(l);
    }

    public void writeVarFloat(final float f) throws IOException {
        writeOpcode(FLOAT);
        dos.writeFloat(f);
    }

    public void writeVarDouble(final double d) throws IOException {
        writeOpcode(DOUBLE);
        dos.writeDouble(d);
    }

    public void writeVarChar(final int c) throws IOException {
        writeOpcode(CHAR);
        dos.writeChar(c);
    }

    public void writeVarByte(final int b) throws IOException {
        writeOpcode(BYTE);
        dos.writeByte(b);
    }

    public void writeVarBoolean(final boolean v) throws IOException {
        writeOpcode(BOOLEAN);
        dos.writeBoolean(v);
    }

    public void writeVarString(final String s) throws IOException {
        writeOpcode(STRING);
        dos.writeInt(writer.addString(s));
    }

    public byte[] toBytes() throws IOException {
        if (baos != null) {
            byte[] b = baos.toByteArray();
            dos.close();
            baos.close();
            return b;
        } else {
            throw new IllegalArgumentException("Writer is in file mode");
        }
    }
}