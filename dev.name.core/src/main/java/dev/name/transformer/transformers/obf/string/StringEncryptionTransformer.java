package dev.name.transformer.transformers.obf.string;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Field;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.nodes.Table;
import dev.name.asm.ir.types.Access;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import dev.name.util.math.Random;

public final class StringEncryptionTransformer extends Transformer implements Random {
    private record Key(int[] keys, int[] keys1, int[] keys2, int[] keys3, int[] keys4, int[] keys5, int[] keys6, byte[] sec, byte[] sec1, byte[] sec2) {}

    private static void intMap(int[] arr, InstructionBuilder builder) {
        Label[] labels = new Label[arr.length - 1];

        for (int i = 0, n = labels.length; i < n; i++) {
            labels[i] = builder.newlabel();
        }

        Label _default = builder.newlabel();
        Label _exit = builder.newlabel();

        builder.tableswitch(new Table(0, arr.length - 2, _default, labels));

        for (int i = 0, n = arr.length - 1; i < n; i++) {
            builder.bind(labels[i]);
            builder.ldc(arr[i]);
            builder.jump(_exit);
        }

        builder.bind(_default);
        builder.ldc(arr[arr.length - 1]);
        builder.bind(_exit);
    }

    private static void byteMap(byte[] arr, InstructionBuilder builder) {
        Label[] labels = new Label[arr.length - 1];

        for (int i = 0, n = labels.length; i < n; i++) {
            labels[i] = builder.newlabel();
        }

        Label _default = builder.newlabel();
        Label _exit = builder.newlabel();

        builder.tableswitch(new Table(0, arr.length - 2, _default, labels));

        for (int i = 0, n = arr.length - 1; i < n; i++) {
            builder.bind(labels[i]);
            builder.ldc(arr[i]);
            builder.jump(_exit);
        }

        builder.bind(_default);
        builder.ldc(arr[arr.length - 1]);
        builder.bind(_exit);
    }

    private static Key generate() {
        int[] keys = RANDOM.ints(0x10).toArray();
        int[] keys1 = RANDOM.ints(0x10).toArray();
        int[] keys2 = RANDOM.ints(0x10).toArray();
        int[] keys3 = RANDOM.ints(0x10).toArray();
        int[] keys4 = RANDOM.ints(0x10).toArray();
        int[] keys5 = RANDOM.ints(0x10).toArray();
        int[] keys6 = RANDOM.ints(0x10).toArray();
        byte[] sec = new byte[RANDOM.nextInt(100, 256)], sec1 = new byte[RANDOM.nextInt(100, 256)], sec2 = new byte[RANDOM.nextInt(100, 256)];
        RANDOM.nextBytes(sec);
        RANDOM.nextBytes(sec1);
        RANDOM.nextBytes(sec2);
        return new Key(keys, keys1, keys2, keys3, keys4, keys5, keys6, sec, sec1, sec2);
    }

    private static Instructions form(String str, int index, Key key, Method dec) {
        if (index > Character.MAX_VALUE) throw new IllegalStateException();
        long k1 = RANDOM.nextLong();
        long k2 = RANDOM.nextLong();
        char r = (char) RANDOM.nextInt();
        int mask = RANDOM.nextInt();

        char nr = r;
        long nk1 = k1;
        long nk2 = k2;
        nk1 ^= ~nr | nk1;
        nk2 ^= ~nr | nk2;
        nk1 |= (nr & mask);
        nk2 |= (nr & mask);
        nr ^= (char) (~(nk1 & nk2) ^ (nk1 ^ nk2));
        nr ^= (char) ~(nr & 0xFF);
        nr ^= (char) key.keys[((nr >> 0x0F) != 0x00 ? ~nr : nr) % 0x10];
        char m = (char) (nr ^ index);
        mask = (mask & 0xFFFF0000) | m;

        String encrypted = encrypt(str, k1, k2, mask, r, key.keys, key.keys1, key.keys2, key.keys3, key.keys4, key.keys5, key.keys6, key.sec, key.sec1, key.sec2);
        InstructionBuilder builder = InstructionBuilder.generate();
        builder.ldc(encrypted).ldc(k1).ldc(k2).ldc(mask).ldc(r);
        builder.invoke(dec);
        return builder.build();
    }

    private static Method decrypt(Key key, Field enc, Field dec) {
        Method method = new Method(Access.builder()._private()._static().build(), "__dec__", "(JJIC)Ljava/lang/String;", null, new String[0]);
        InstructionBuilder builder = InstructionBuilder.generate();

        int k1 = 0, k2 = 2, mask = 4, r = 5, b = 6, index = 7, result = 8, k3 = 9, k4 = 9, k5 = 10, k6 = 11, k7 = 12, arr = 13, v = 14, len = 15;
        int[] bases = {k1, k2};
        Label _ret = builder.newlabel();

        for (int i : bases) {
            builder
                    .lload(i)
                    .dup2()
                    .iload(5)
                    .iconst_m1()
                    .ixor()
                    .i2l()
                    .lor()
                    .lxor()
                    .lstore(i);
        }

        builder
                .lload(k1)
                .iload(r)
                .iload(mask)
                .iand()
                .i2l()
                .lor()
                .lstore(k1)
                .lload(k2)
                .iload(r)
                .iload(mask)
                .iand()
                .i2l()
                .lor()
                .lstore(k2)
                .iload(r)
                .lload(k1)
                .dup2()
                .lload(k2)
                .dup2_x2()
                .land()
                .iconst_m1()
                .i2l()
                .lxor()
                .lxor()
                .lxor()
                .l2i()
                .i2c()
                .ixor()
                .i2c()
                .dup()
                .istore(r)
                .dup()
                .ldc(0xFF)
                .iand()
                .iconst_m1()
                .ixor()
                .i2c()
                .ixor()
                .i2c()
                .dup()
                .istore(r)
                .dup()
                .dup()
                .bipush(15)
                .ishr()
                .ineg()
                .ixor()
                .ldc(0x10)
                .irem();

        intMap(key.keys, builder);

        builder
                .i2c()
                .ixor()
                .i2c()
                .dup()
                .istore(r)
                .i2b()
                .istore(b)
                .iload(mask)
                .ldc(0xFFFF)
                .iand()
                .iload(r)
                .ixor()
                .dup()
                .istore(index)
                .get(dec)
                .swap()
                .aaload()
                .dup()
                .astore(result)
                .ifnonnull(_ret)
                .iload(b)
                .ldc(0x01)
                .ldc(0x07)
                .ishl()
                .iconst_m1()
                .ixor()
                .i2b()
                .iand()
                .i2b()
                .ldc(0x0F)
                .iand()
                .istore(b)
                .lload(k2)
                .iload(b)
                .ldc(0x08)
                .iload(b)
                .ldc(0x03)
                .iand()
                .ishl()
                .ishl()
                .i2l()
                .land()
                .lstore(k2)
                .lload(k1)
                .lload(k2)
                .iload(b)
                .lushr()
                .land()
                .lstore(k1)
                .iload(mask)
                .lload(k1)
                .iload(b)
                .ldc(0x0F)
                .iand()
                .lshl()
                .ldc(-1L)
                .lxor()
                .l2i()
                .ior()
                .istore(mask)
                .iload(b);

        intMap(key.keys, builder);

        builder
                .istore(k3)
                .lload(k1)
                .l2i()
                .iload(k3)
                .ixor()
                .istore(k4)
                .lload(k2)
                .ldc(0x20)
                .lushr()
                .l2i()
                .iload(k4)
                .ixor()
                .istore(k5)
                .iload(k3)
                .ldc(0x0F)
                .iand();

        intMap(key.keys1, builder);

        builder
                .iload(mask)
                .iload(r)
                .i2l()
                .lload(k1)
                .lxor()
                .lload(k2)
                .lxor()
                .ldc(4L)
                .lrem()
                .l2i();

        Label[] cases = new Label[] {builder.newlabel(), builder.newlabel(), builder.newlabel(), builder.newlabel()};
        Label _def = builder.newlabel();
        Label esc = builder.newlabel();
        Table table = new Table(0, 3, _def, cases);

        builder
                .tableswitch(table)
                .bind(cases[0])
                .iload(k3)
                .dup()
                .iload(r)
                .iconst_2()
                .irem()
                .ishl()
                .ldc(0x20)
                .iload(r)
                .iconst_2()
                .irem()
                .isub()
                .iushr()
                .ior()
                .jump(esc);

        builder
                .bind(cases[1])
                .iload(k4)
                .dup()
                .iload(r)
                .iconst_3()
                .irem()
                .iushr()
                .ldc(0x20)
                .iload(r)
                .iconst_3()
                .irem()
                .isub()
                .ishl()
                .ior()
                .jump(esc);

        builder
                .bind(cases[2])
                .iload(k5)
                .dup()
                .iload(r)
                .iconst_4()
                .irem()
                .ishl()
                .ldc(0x20)
                .iload(r)
                .iconst_4()
                .irem()
                .isub()
                .iushr()
                .ior()
                .jump(esc);

        builder
                .bind(cases[3])
                .iload(k6)
                .dup()
                .iload(r)
                .iconst_5()
                .irem()
                .iushr()
                .ldc(0x20)
                .iload(r)
                .iconst_5()
                .irem()
                .isub()
                .ishl()
                .ior()
                .jump(esc);

        builder
                .bind(_def)
                .lload(k1)
                .lload(k2)
                .land()
                .l2i()
                .iload(r)
                .i2l()
                .lload(k1)
                .ldc(0x06)
                .lshl()
                .lrem()
                .l2i()
                .ishl();

        builder
                .bind(esc)
                .ixor()
                .istore(mask)
                .iload(k3)
                .iload(k4)
                .iand()
                .iconst_m1()
                .ixor()
                .iload(k5)
                .iload(k6)
                .iand()
                .iconst_m1()
                .ixor()
                .iand()
                .iload(mask)
                .ixor()
                .dup()
                .bipush(15)
                .ishr()
                .ineg()
                .ixor()
                .dup()
                .ldc(0x10)
                .irem();

        intMap(key.keys2, builder);

        builder
                .ixor()
                .istore(k7)
                .get(enc)
                .iload(index)
                .aaload()
                .invokevirtual("java/lang/String", "toCharArray", "()[C", false)
                .dup()
                .astore(arr)
                .arraylength()
                .istore(len)
                .iload(r)
                .ldc(key.sec.length)
                .irem();

        byteMap(key.sec, builder);

        builder
                .istore(v);

        builder.bind(_ret).aload(result).areturn();

        method.setInstructions(builder.build());
        return method;
    }

    public static String encrypt(String str, long k1, long k2, int mask, char r, int[] keys, int[] keys1, int[] keys2, int[] keys3, int[] keys4, int[] keys5, int[] keys6, byte[] sec, byte[] sec1, byte[] sec2) {
        k1 ^= ~r | k1;
        k2 ^= ~r | k2;
        k1 |= (r & mask);
        k2 |= (r & mask);
        r ^= (char) (~(k1 & k2) ^ (k1 ^ k2));
        r ^= (char) ~(r & 0xFF);
        r ^= (char) keys[((r >> 0x0F) != 0x00 ? ~r : r) % 0x10];
        byte b = (byte) r;
        int index = (mask & 0xFFFF) ^ r;
        b &= (byte) ~(0x01 << 0x07);
        b &= 0x0F;
        k2 &= ((long) b << (0x08 << (b & 0x03)));
        k1 &= (k2 >>> b);
        mask |= (int) ~(k1 >> (b & 0x0F));
        int k3 = keys[b];
        int k4 = (int) k1 ^ k3;
        int k5 = (int) (k2 >>> 0x20) ^ k4;
        int k6 = keys1[k3 & 0x0F];

        mask ^= switch ((int) ((r ^ k1 ^ k2) % 0x04)) {
            case 0x00 -> (k3 << r % 0x02) | (k3 >>> (0x20 - r % 0x02));
            case 0x01 -> (k4 >>> r % 0x03) | (k4 << (0x20 - r % 0x03));
            case 0x02 -> (k5 << r % 0x04) | (k5 >>> (0x20 - r % 0x04));
            case 0x03 -> (k6 >>> r % 0x05) | (k6 << (0x20 - r % 0x05));
            default -> (int) (k1 & k2) << r % (k1 << 0x06);
        };

        int k7 = ~(k3 & k4) & ~(k5 & k6) ^ mask;
        k7 ^= ((k7 >> 0x1F) != 0x00) ? -0x01 : 0x00;
        k7 ^= keys2[k7 % 0x10];
        char[] c = str.toCharArray();
        r ^= (char) (((r >> 0x0F) != 0x00) ? -0x01 : 0x00);
        byte v = sec[r % sec.length];

        for (int i = 0, n = c.length; i < n; i++) {
            c[i] ^= (char) (keys3[i % 0x10] % 0x10000);
            c[i] ^= (char) (sec1[i % sec1.length] % 0x8000);
            c[i] ^= ((char) (((char) k7) % 0x4000));
            c[i] ^= ((char) ((char) keys4[i % 0x10] % 0x2000));
            c[i] ^= ((char) ((char) (k1 << (0x08 * (keys5[i % 0x10] % 0x07))) % 0x1000));
            c[i] ^= ((char) ((char) (k2 >> (0x04 * (keys6[i % 0x10] % 0x0F))) % 0x800));
            c[i] ^= ((char) ((char) v % 0x400));
            c[i] ^= (r ^= (char) ((char) sec2[r % sec2.length] % 0x200));
        }

        return new String(c).intern();
    }

    public static void main(String[] args) {
        //form("nn", '\uFFFF', null);

    }

    @Override
    public String name() {
        return "String Encryption Transformer";
    }

    @Override
    public void transform(ClassPool pool) {
        for (Class k : pool) {
            Field dec = new Field(Access.builder()._private()._static().build(), "ezzzz", "[Ljava/lang/String;", null, null);
            Field enc = new Field(Access.builder()._private()._static().build(), "ezzzzz", "[Ljava/lang/String;", null, null);
            k.addField(dec);
            k.addField(enc);
            k.addMethod(decrypt(generate(),enc, dec));
           /* for (Method method : k.methods) {
                if (method.access.isAbstract() || method.access.isNative() || method.instructions.size() <= 0) continue;
                //form("nn", 55555, null);
            }*/
        }
    }
}