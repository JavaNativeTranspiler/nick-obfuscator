package xyz.qreaj.virtualizer.engine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ConstantPoolWriter {
    private final DataOutputStream dos;
    private final ArrayList<String> strings;
    private final ArrayList<Long> longs;
    private final ArrayList<Integer> integers;
    private final ArrayList<Short> shorts;
    private final ArrayList<Byte> bytes;
    private final ArrayList<Double> doubles;
    private final ArrayList<Float> floats;
    private final ArrayList<Byte> modules;

    public ConstantPoolWriter(DataOutputStream dos) {
        this.dos = dos;
        this.strings = new ArrayList<>();
        this.longs = new ArrayList<>();
        this.integers = new ArrayList<>();
        this.shorts = new ArrayList<>();
        this.bytes = new ArrayList<>();
        this.doubles = new ArrayList<>();
        this.floats = new ArrayList<>();
        this.modules = new ArrayList<>();
    }

    public void writeConstantPool() throws IOException {
        dos.writeInt(strings.size());
        dos.writeInt(longs.size());
        dos.writeInt(integers.size());
        dos.writeInt(shorts.size());
        dos.writeInt(bytes.size());
        dos.writeInt(doubles.size());
        dos.writeInt(floats.size());
        dos.writeInt(modules.size());

        for (final String s : strings) dos.writeUTF(s);
        for (final Long l : longs) dos.writeLong(l);
        for (final Integer i : integers) dos.writeInt(i);
        for (final Short s : shorts) dos.writeShort(s);
        for (final Byte b : bytes) dos.writeByte(b);
        for (final Double d : doubles) dos.writeDouble(d);
        for (final Float f : floats) dos.writeFloat(f);
        for (final Byte b : modules) dos.writeByte(b);
    }


    private <T> int addToPool(ArrayList<T> pool, T value) {
        int index = pool.indexOf(value);
        if (index != -1) {
            return index; // returns index of value written earlier if it's the same
        } else {
            pool.add(value);
            return pool.size() - 1; // val index
        }
    }

    public int addString(String value) {
        return addToPool(strings, value);
    }

    public int addLong(long value) {
        return addToPool(longs, value);
    }

    public int addInteger(int value) {
        return addToPool(integers, value);
    }

    public int addShort(short value) {
        return addToPool(shorts, value);
    }

    public int addByte(byte value) {
        return addToPool(bytes, value);
    }

    public int addDouble(double value) {
        return addToPool(doubles, value);
    }

    public int addFloat(float value) {
        return addToPool(floats, value);
    }

    private int[] findBytes(final ArrayList<Byte> list, byte[] pattern) {
        int len = pattern.length;
        int size = list.size();

        for (int i = 0; i <= size - len; i++) {
            boolean found = true;

            for (int j = 0; j < len; j++) {
                if (!list.get(i + j).equals(pattern[j])) {
                    found = false;
                    break;
                }
            }

            if (found) return new int[]{i, i + len - 1};

        }

        return null;
    }

    public int[] addModuleByteArray(byte[] bytes) {
        final int[] res = findBytes(modules, bytes);
        if (res != null) return res;

        modules.add(bytes[0]);
        int start = modules.size() - 1;
        for (int i = 1, n = bytes.length - 1; i < n; i++) modules.add(bytes[i]);
        modules.add(bytes[bytes.length - 1]);
        int endIndex = modules.size() - 1;

        return new int[]{start, endIndex};
    }
}