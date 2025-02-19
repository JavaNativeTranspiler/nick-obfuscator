package dev.name.asm.ir.types;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Type;
import dev.name.asm.ir.nodes.Variable;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.security.SecureRandom;
import java.util.*;

@SuppressWarnings("all")
public final class LocalPool implements Opcodes, Iterable<LocalPool.Local> {
    private static final Random RANDOM = new SecureRandom();

    public static final int
        INT = 0,
        FLOAT = 1,
        OBJECT = 2,
        LONG = 3,
        DOUBLE = 4;

    @NotNull
    @Override
    public Iterator<LocalPool.Local> iterator() {
        return locals.iterator();
    }

    @ToString
    public static final class Local {
        public final Flags flags = new Flags();
        public final int index, size, type;
        private final int load, store;

        public Local(final int index, final int size, final int type) {
            this.index = index;
            this.size = size;
            this.type = type;
            this.load = LocalPool.load(type);
            this.store = LocalPool.store(type);
        }

        public Variable load() {
            return new Variable(load, index);
        }

        public Type cast() {
            return new Type(CHECKCAST, switch (type) {
                case INT -> "I";
                case FLOAT -> "F";
                case DOUBLE -> "D";
                case LONG -> "J";
                default -> throw new IllegalArgumentException();
            });
        }

        public Variable store() {
            return new Variable(store, index);
        }
    }

    public static final class Entry {
        private final LocalPool parent;
        private final Set<Local> used = new LinkedHashSet<>();

        private Entry(final LocalPool parent) {
            this.parent = parent;
        }

        public Local next(final int type) {
            final Local local = parent.locals.stream().filter(loc -> loc.type == type && !used.contains(loc)).findFirst().orElse(parent.allocate(type));
            used.add(local);
            return local;
        }

        public Local[] get() {
            return used.toArray(new Local[0]);
        }
    }

    private final Method method;
    private final List<Local> locals = new ArrayList<>();

    public LocalPool(final Method method) {
        this.method = method;
    }

    public void recompute() {

    }

    public Local allocate(final int type) {
        final int size = switch (type) {
            case LONG, DOUBLE -> 2;
            case INT, FLOAT, OBJECT -> 1;
            default -> throw new RuntimeException();
        };

        final Local local = new Local(method.maxLocals, size, type);
        method.maxLocals += size;
        if (locals.contains(local)) throw new RuntimeException("impossible");
        locals.add(local);
        return local;
    }

    public static int type(final BasicValue value) {
        if (value.equals(BasicValue.INT_VALUE)) return INT;
        else if (value.equals(BasicValue.FLOAT_VALUE)) return FLOAT;
        else if (value.equals(BasicValue.REFERENCE_VALUE)) return OBJECT;
        else if (value.equals(BasicValue.LONG_VALUE)) return LONG;
        else if (value.equals(BasicValue.DOUBLE_VALUE)) return DOUBLE;

        return OBJECT;
    }

    public static int translate(final int opcode) {
        return switch (opcode) {
            case ILOAD, ISTORE -> INT;
            case FLOAD, FSTORE -> FLOAT;
            case ALOAD, ASTORE -> OBJECT;
            case LLOAD, LSTORE -> LONG;
            case DLOAD, DSTORE -> DOUBLE;
            default -> throw new IllegalArgumentException("invalid translation opcode: " + opcode);
        };
    }

    public static int load(final int type) {
        return switch (type) {
            case INT -> ILOAD;
            case FLOAT -> FLOAD;
            case OBJECT -> ALOAD;
            case LONG -> LLOAD;
            case DOUBLE -> DLOAD;
            default -> throw new IllegalArgumentException("unexpected type: " + type);
        };
    }

    public static int store(final int type) {
        return switch (type) {
            case INT -> ISTORE;
            case FLOAT -> FSTORE;
            case OBJECT -> ASTORE;
            case LONG -> LSTORE;
            case DOUBLE -> DSTORE;
            default -> throw new IllegalArgumentException("unexpected type: " + type);
        };
    }

    public static Object initializer(final int type) {
        return switch (type) {
            case INT -> 0;
            case FLOAT -> 0.0F;
            case OBJECT -> null;
            case LONG -> 0L;
            case DOUBLE -> 0.0D;
            default -> throw new IllegalArgumentException("bad type: " + type);
        };
    }

    public static Object randomInitializer(final int type) {
        return switch (type) {
            case INT -> RANDOM.nextInt();
            case FLOAT -> RANDOM.nextFloat(-1.0F, 1.0F) * RANDOM.nextInt();
            case OBJECT -> null;
            case LONG -> RANDOM.nextLong();
            case DOUBLE -> RANDOM.nextDouble(-1.0D, 1.0D) * RANDOM.nextLong();
            default -> throw new IllegalArgumentException("bad type: " + type);
        };
    }

    public Entry nextEntry() {
        return new Entry(this);
    }
}