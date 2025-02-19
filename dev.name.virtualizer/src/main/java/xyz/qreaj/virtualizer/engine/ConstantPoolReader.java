package xyz.qreaj.virtualizer.engine;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstantPoolReader {
    private final DataInputStream dis;
    private String[] strings;
    private long[] longs;
    private int[] integers;
    private short[] shorts;
    private byte[] bytes;
    private double[] doubles;
    private float[] floats;

    @Getter
    private byte[] modules;

    public ConstantPoolReader(DataInputStream dis) {
        this.dis = dis;
    }

    public void load() throws IOException {
        strings = new String[dis.readInt()];
        longs = new long[dis.readInt()];
        integers = new int[dis.readInt()];
        shorts = new short[dis.readInt()];
        bytes = new byte[dis.readInt()];
        doubles = new double[dis.readInt()];
        floats = new float[dis.readInt()];
        modules = new byte[dis.readInt()];

        for (int i = 0, n = strings.length; i < n; i++) strings[i] = dis.readUTF();
        for (int i = 0, n = longs.length; i < n; i++) longs[i] = dis.readLong();
        for (int i = 0, n = integers.length; i < n; i++) integers[i] = dis.readInt();
        for (int i = 0, n = shorts.length; i < n; i++) shorts[i] = dis.readShort();
        for (int i = 0, n = bytes.length; i < n; i++) bytes[i] = dis.readByte();
        for (int i = 0, n = doubles.length; i < n; i++) doubles[i] = dis.readDouble();
        for (int i = 0, n = floats.length; i < n; i++) floats[i] = dis.readFloat();
        for (int i = 0, n = modules.length; i < n; i++) modules[i] = dis.readByte();
    }

    public String getString(final int index) {
        return strings[index];
    }

    public long getLong(final int index) {
        return longs[index];
    }

    public int getInteger(final int index) {
        return integers[index];
    }

    public short getShort(final int index) {
        return shorts[index];
    }

    public byte getByte(final int index) {
        return bytes[index];
    }

    public double getDouble(final int index) {
        return doubles[index];
    }

    public float getFloat(final int index) {
        return floats[index];
    }

    public byte[] getVirtualized_module(final int start, final int end) {
        if (start < 0 || end >= modules.length || start > end) {
            throw new IllegalArgumentException("Invalid start or end indices.");
        }

        byte[] r = new byte[end - start + 1];
        if (end + 1 - start >= 0) System.arraycopy(modules, start, r, 0, end + 1 - start);
        return r;
    }
}