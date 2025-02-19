package xyz.qreaj.virtualizer.opcodes.invoke;

import lombok.Getter;
import xyz.qreaj.virtualizer.opcodes.type.Opcode;
import xyz.qreaj.virtualizer.utils.Type;
import xyz.qreaj.virtualizer.utils.UnsafeReflection;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class INVOKE extends Opcode {
    private InvokeType type;
    private MethodHandle handle;

    @Getter
    private int len;

    @Override
    public void readData(final DataInputStream dis) throws IOException {
        type = InvokeType.of(dis.readByte());
        final String owner = pool.getString(dis.readInt());
        final String name = pool.getString(dis.readInt());
        final String desc = pool.getString(dis.readInt());

        final Type mt = Type.getMethodType(desc);
        final Type ret = mt.getReturnType();
        final Type[] types = mt.getArgumentTypes();

        final Class<?> ret_cl = getClassFromType(ret);
        final Class<?>[] args = new Class<?>[types.length];
        for (int i = 0, n = types.length; i < n; i++) args[i] = getClassFromType(types[i]);

        this.len = args.length;
        final MethodType java_mt = MethodType.methodType(ret_cl, args);
        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            handle = switch (type) {
                case STATIC -> lookup.findStatic(Class.forName(owner), name, java_mt);
                case SPECIAL -> "<init>".equals(name) ? lookup.findConstructor(Class.forName(owner), java_mt) : lookup.findSpecial(Class.forName(owner), name, java_mt, Class.forName(owner));
                case VIRTUAL -> {
                    len++;
                    yield lookup.findVirtual(Class.forName(owner), name, java_mt);
                }
            };
        } catch (final Throwable _t) {
            try {
                handle = lookup.unreflect(UnsafeReflection.get_method(Class.forName(owner), name, args));
            } catch (final Throwable __t) {
                __t.printStackTrace(System.err);
                return;
            }

            _t.printStackTrace(System.err);
        }
    }


    public Object invoke(Object ... param) throws Throwable {
        if (param.length != len) throw new IllegalArgumentException(String.format("invalid args len, expected: %d, got: %d", len, param.length));
        if (type == InvokeType.STATIC || type == InvokeType.SPECIAL) return (len == 1 && handle.type().parameterList().get(0).isArray()) ? handle.invoke((Object[]) param[0]) : handle.invokeWithArguments(param);
        else if (type == InvokeType.VIRTUAL) {
            if (param.length < 1) throw new IllegalArgumentException("Instance required for virtual invocation.");
            final Object inst = param[0];
            final Object[] isolated = new Object[param.length - 1];
            System.arraycopy(param, 1, isolated, 0, isolated.length);
            return handle.bindTo(inst).invokeWithArguments(isolated);
        }

        return null;
    }

    private static Class<?> getClassFromType(final Type type) {
        try {
            return switch (type.getSort()) {
                case Type.VOID -> void.class;
                case Type.BOOLEAN -> boolean.class;
                case Type.CHAR -> char.class;
                case Type.BYTE -> byte.class;
                case Type.SHORT -> short.class;
                case Type.INT -> int.class;
                case Type.FLOAT -> float.class;
                case Type.LONG -> long.class;
                case Type.DOUBLE -> double.class;
                case Type.ARRAY -> Class.forName(type.getDescriptor().replace('/', '.'));
                case Type.OBJECT -> Class.forName(type.getClassName());
                default -> throw new IllegalArgumentException("Unsupported type: " + type);
            };
        } catch (final ClassNotFoundException _t) {
            _t.printStackTrace(System.err);
            return null;
        }
    }
}