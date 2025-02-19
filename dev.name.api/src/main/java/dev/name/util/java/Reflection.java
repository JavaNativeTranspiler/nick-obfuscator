package dev.name.util.java;

import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"CatchMayIgnoreException", "unused", "unchecked"})
public class Reflection {
    private static Unsafe unsafe;
    private static Method[] table;

    static {
        try {
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            table = new Method[]
                    {
                            Class.class.getDeclaredMethod("getDeclaredMethods0", boolean.class),
                            Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class),
                            Class.class.getDeclaredMethod("getDeclaredConstructors0", boolean.class)
                    };

            for (Method method : table) patch(method);
        } catch (Throwable t) {}
    }

    public enum Mask {
        METHODS,
        FIELDS,
        CONSTRUCTORS
    }

    public static <T extends AccessibleObject> boolean is_patched(final T obj) {
        return unsafe.getBoolean(obj, 12);
    }

    public static <T extends AccessibleObject> void patch(final T obj) {
        if (!is_patched(obj))
            unsafe.putBoolean(obj, 12, true);
    }

    public static <T extends AccessibleObject> void patch_all(final T[] objects) {
        for (final T obj : objects)
            patch(obj);
    }

    public static List<Class<?>> get_loaded_classes(ClassLoader cl) {
        try {
            return (List<Class<?>>) get_field(ClassLoader.class, "classes").get(cl);
        } catch (Throwable t) { throw new RuntimeException("shouldnt reach here"); }
    }

    public static Method get_method(final Class<?> klass, final String name, final Class<?>... parameters) {
        final Method[] methods = get_members(klass, Mask.METHODS, false);

        for (final Method method : methods)
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameters)) {
                patch(method);
                return method;
            }

        throw new RuntimeException("no method found");
    }

    public static Field get_field(final Class<?> klass, final String name) {
        final Field[] fields = get_members(klass, Mask.FIELDS, false);

        for (final Field field : fields)
            if (field.getName().equals(name)) {
                patch(field);
                return field;
            }

        throw new RuntimeException("no field found");
    }

    public static <T> Constructor<T> get_constructor(final Class<?> klass, final Class<?>... parameters) {
        final Constructor<?>[] constructors = get_members(klass, Mask.CONSTRUCTORS, false);

        for (final Constructor<?> constructor : constructors)
            if (Arrays.equals(constructor.getParameterTypes(), parameters)) {
                patch(constructor);
                return (Constructor<T>) constructor;
            }

        throw new RuntimeException("no constructor found");
    }

    public static <T extends AccessibleObject> T[] get_members(final Class<?> klass, final Mask mask, final boolean patch_all) {
        final Method method = table[mask.ordinal()];

        try {
            final T[] objects = (T[]) method.invoke(klass, false);
            if (patch_all) patch_all(objects);
            return objects;
        } catch (Throwable t) { throw new RuntimeException("shouldnt reach here"); }
    }
}