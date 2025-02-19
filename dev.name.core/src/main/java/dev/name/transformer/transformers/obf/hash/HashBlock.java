package dev.name.transformer.transformers.obf.hash;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.transformer.transformers.obf.hash.hashes.flow.BasicLookupHash;
import dev.name.transformer.transformers.obf.hash.hashes.generic.BasicHash;
import dev.name.util.collections.array.Arrays;
import dev.name.util.collections.generic.Pair;
import dev.name.util.math.Math;
import dev.name.util.math.Random;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class HashBlock implements Random {
    private static final List<Hash> HASHES = new ArrayList<>()
    {
        {
            add(new BasicHash());
            add(new BasicLookupHash());
            //add(new ExceptionHash());
            //add(new LookupHash());
        }
    };

    private static final boolean INTERPOLATION = true;

    public final Method method;
    public long[] keys;
    public final int magnitude;
    public final LocalPool.Local[] locals;
    public final Label label;

    public HashBlock(final Method method, final Label label, final LocalPool.Local[] locals, final int magnitude) {
        if (locals.length < 2) throw new IllegalArgumentException("not enough keys");
        final int size = locals.length;
        this.method = method;
        this.label = label;
        this.keys = new long[size];
        this.magnitude = magnitude;
        this.locals = locals;
        for (int i = 0, n = keys.length; i < n; i++) keys[i] = nextLong();
    }

    // exception handlers
    public void merge(final HashBlock block) {
        this.keys = block.keys.clone();
    }

    public void encrypt() {
        long[] states = keys.clone();

        Node curr = label.next;

        while (curr != null && !(curr instanceof Label)) {
            Node next = curr.next;

            final int l = nextInt(0, states.length - 1);
            final HashContext context = new HashContext(method, states, locals, InstructionBuilder.generate(), curr);

            if (INTERPOLATION && java.lang.Math.random() < 0.025D) HASHES.get(nextInt(0, HASHES.size() - 1)).hash(context, builder -> context.node.insertBefore(builder.build()));

            if (INTERPOLATION && curr instanceof Jump || curr instanceof Table || curr instanceof Lookup) {
                reinterpolate(curr, states);
                states = keys.clone();
                curr = next;
                continue;
            }


            if (!(curr instanceof Constant constant && (constant.cst instanceof Number || constant.cst instanceof Boolean || constant.cst instanceof Character))) {
                curr = next;
                continue;
            }

            Object _constant = constant.cst;
            _constant = _constant instanceof Boolean b ? (b ? 1 : 0) : _constant instanceof Character c ? (int) c : _constant;

            final Number number = (Number) _constant;

            final InstructionBuilder builder = InstructionBuilder.generate();
            final long id = number instanceof Double d ? Double.doubleToLongBits(d) : number instanceof Float f ? Float.floatToIntBits(f) : number.longValue();
            final long k = states[l];
            final int g = nextInt(0, states.length - 1, l);
            final long[] r = Math.xor(id, 1, k, states[g]);

            builder.add(locals[l].load());
            builder.add(locals[g].load());
            builder.ldc(r[2]);
            builder.lxor();
            builder.lxor();
            /*builder.dup2();
            builder.ldc(id);
            builder.lxor();
            builder.getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
            builder.dup_x2();
            builder.pop();
            builder.invokevirtual("java/io/PrintStream", "println", "(J)V", false);*/


            switch (number.getClass().getSimpleName()) {
                case "Boolean", "Integer" -> builder.l2i();
                case "Byte" -> builder.l2i().i2b();
                case "Short" -> builder.l2i().i2s();
                case "Character" -> builder.l2i().i2c();
                case "Long" -> {}
                case "Float" -> builder.l2i().invokestatic("java/lang/Float", "intBitsToFloat", "(I)F", false);
                case "Double" -> builder.invokestatic("java/lang/Double", "longBitsToDouble", "(J)D", false);
                default -> throw new IllegalArgumentException("did not expect: " + number.getClass().getSimpleName());
            }

            constant.replace(builder.build());
            curr = next;
        }

        if (INTERPOLATION) reinterpolate(curr == null ? label.parent.last : curr, states);
    }

    private void reinterpolate(final Node end, final long[] states) {
        final InstructionBuilder builder = InstructionBuilder.generate();
        final List<Integer> unmodified = new ArrayList<>(), modified = new ArrayList<>();

        for (int i = 0, n = states.length; i < n; i++)
            if (keys[i] == states[i]) unmodified.add(i);
            else modified.add(i);

        for (final int k : modified) {
            final long key = keys[k];
            final LocalPool.Local l = locals[k];
            final int r = unmodified.isEmpty() ? -1 : unmodified.get(nextInt(0, unmodified.size() - 1));
            final long[] f = (r == -1) ? Math.xor(key, 1, states[k]) : Math.xor(key, 1, states[k], keys[r]);

            builder.add(l.load());

            if (r == -1) builder.ldc(f[1]);
            else {
                builder.add(locals[r].load());
                builder.ldc(f[2]);
                builder.lxor();
            }

            builder.lxor();
            builder.add(l.store());
        }

        end.insertBefore(builder.build());
    }

    public Instructions jump(final HashBlock block) {
        final InstructionBuilder builder = InstructionBuilder.generate();

        final LocalPool.Local[] k1 = locals.clone(), k2 = locals.clone();
        Arrays.shuffle(k1, k2, false);
        final List<LocalPool.Local> indexed = List.of(locals);
        final long[] states = keys.clone();

        for (int i = 0, n = k1.length; i < n; i++) {
            final LocalPool.Local l1 = k1[i];
            final LocalPool.Local l2 = k2[i];
            if (l1.equals(l2)) System.out.println("[HashBlock] Shouldn't reach here:\n" + java.util.Arrays.toString(k1) + "\n" + java.util.Arrays.toString(k2) + "\n");
            final long r = states[indexed.indexOf(l2)];

            builder.add(l1.load());
            builder.add(l2.load());
            builder.lxor();
            builder.add(l1.store());

            states[indexed.indexOf(l1)] ^= r;

            if (magnitude == 0) continue;

            for (int j = 0; j < magnitude; j++) {
                final HashContext context = new HashContext(method, states, this.locals, InstructionBuilder.generate(), null);
                if (java.lang.Math.random() < 0.1D) HASHES.get(nextInt(0, HASHES.size() - 1)).hash(context, b -> builder.add(b.build()));
            }
        }

        // TODO make more advanced, smaller.
        for (int i = 0, n = keys.length; i < n; i++) {
            final int kl = nextInt(0, locals.length - 1, i);
            final long d = block.keys[i];
            final long c = states[i];
            final long j = states[kl];
            final long f = Math.xor(d, 1, c, j)[2];

            final LocalPool.Local l = locals[i];
            final LocalPool.Local lk = locals[kl];

            builder.add(l.load());
            builder.add(lk.load());
            builder.ldc(f);

            builder.lxor();
            builder.lxor();
            builder.add(l.store());

            states[i] ^= j;
            states[i] ^= f;
        }

        builder.jump(block.label);

        return builder.build();
    }

    public Instructions initializer() {
        final InstructionBuilder builder = InstructionBuilder.generate();

        final LocalPool.Local[] shuffled = locals.clone();
        Arrays.shuffle(shuffled);
        final List<LocalPool.Local> indexes = List.of(locals);

        // avoid decompiler variable assigning patterns
        for (int i = 0, n = shuffled.length; i < n; i++) {
            final LocalPool.Local local = shuffled[i];
            final int index = indexes.indexOf(local);
            if (i == 0) {
                builder.ldc(keys[index]);
                builder.add(local.store());
            } else {
                final LocalPool.Local prev = shuffled[i - 1];
                final int prevIndex = indexes.indexOf(prev);
                builder.add(prev.load());
                builder.ldc(Math.xor(keys[index], 1, keys[prevIndex])[1]);
                builder.lxor();
                builder.add(local.store());
            }
        }

        /*for (int i = 0, n = locals.length; i < n; i++) {
            builder.ldc(keys[i]);
            builder.add(locals[i].store());
        }*/

        return builder.build();
    }

    public Instructions debug() {
        final InstructionBuilder builder = InstructionBuilder.generate();

        builder.getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");

        for (int i = 0, n = locals.length, m = n - 1; i < n; i++) {
            if (i != m) builder.dup();
            builder.add(locals[i].load());
            builder.invokevirtual("java/io/PrintStream", "println", "(J)V", false);
        }

        return builder.build();
    }

    public Pair<Instructions, Long> save(final LocalPool.Local local) {
        final InstructionBuilder builder = InstructionBuilder.generate();
        long r = 0;
        int count = -1;

        for (int i = 0, n = keys.length; i < n; i++) {
            builder.add(locals[i].load());

            if (java.lang.Math.random() < 0.13) {
                final long l = nextLong();
                builder.ldc(l);
                r ^= l;
                count++;
            }

            r ^= keys[i];
            count++;
        }

        int idx = 0;

        while (idx != count) {
            builder.lxor();
            idx++;
        }

        builder.add(local.store());

        return new Pair<>(builder.build(), r);
    }

    public Instructions recover(final LocalPool.Local mirror, long key) {
        final InstructionBuilder builder = InstructionBuilder.generate();

        for (int i = 0, n = keys.length; i < n; i++) {
            if (java.lang.Math.random() < 0.657D) {
                final long r = nextLong();

                builder.add(mirror.load());
                builder.ldc(r);
                builder.lxor();
                builder.add(mirror.store());

                key ^= r;
            }

            builder.add(mirror.load());
            builder.ldc(Math.xor(keys[i], 1, key)[1]);
            builder.lxor();
            builder.add(locals[i].store());
        }

        return builder.build();
    }
}