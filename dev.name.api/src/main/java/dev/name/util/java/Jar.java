package dev.name.util.java;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Invoke;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
public class Jar {
    private final JarFile jar;
    private final int flags;
    public final ClassLoader loader = new FramedClassLoader(this);
    public final ClassPool classes;
    public final HashMap<String, byte[]> files = new HashMap<>();

    public static int MUTABLE = 1 << 0;
    public static int DEBUG = 1 << 1;

    @SneakyThrows
    public Jar(final JarFile jar, final int flags) {
        if (jar == null) throw new NullPointerException("invalid jar");
        this.jar = jar;
        this.classes = new ClassPool();
        this.flags = flags;

        final Enumeration<JarEntry> entries = this.jar.entries();

        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            if (!entry.getName().endsWith(".class")) continue;
            classes.add(read(entry));
        }

        try (final ZipInputStream zis = new ZipInputStream(Files.newInputStream(new File(jar.getName()).toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) continue;
                files.put(entry.getName(), IOUtils.toByteArray(zis));
            }
        }

        if ((flags & MUTABLE) == 0) return;

        for (final Class klass : classes)
            for (final Method method : klass.methods)
                method.mutable = isMutable(method);
    }

    private boolean isMutable(final Method method) {
        if (method.name.equals("<init>") || method.name.equals("<clinit>")) return true;
        if (method.access.isPrivate()) return true;
        if (method.klass.interfaces.isEmpty() && method.klass.superName == null) return true;

        try {
            final java.lang.Class<?> klass = loader.loadClass(method.klass.name.replace('/', '.'));
            java.lang.reflect.Method reflected = null;

            for (final java.lang.reflect.Method ref : klass.getDeclaredMethods()) {
                final String name = ref.getName();
                final String desc = Type.getMethodDescriptor(ref);
                if (!name.equals(method.name) || !desc.equals(method.desc)) continue;
                reflected = ref;
                break;
            }

            if (reflected == null && ((this.flags & DEBUG) == 0)) {
                System.out.println("weird: " + method.name + " " + method.desc);
                return false;
            }

            final Set<java.lang.reflect.Method> hierarchy = MethodUtils.getOverrideHierarchy(reflected, ClassUtils.Interfaces.INCLUDE);
            final boolean mutable = hierarchy.size() <= 1;

            if (!mutable) {
                try {
                    final java.lang.reflect.Method superMethod = hierarchy.toArray(new java.lang.reflect.Method[0])[1];
                    method.superMethod = get(superMethod.getDeclaringClass().getTypeName().replace('.', '/')).getMethod(method.name, method.desc);
                } catch (final Throwable ignored) {} // not in the classpath so it is useless to us for now.
            }

            return mutable;
        } catch (final Throwable _t) {
            if ((this.flags & DEBUG) == 0) _t.printStackTrace(System.err);
            return false;
        }
    }

    public Class get(final String name) {
        return classes.get(name);
    }

    public static Jar read(final JarFile jar, final int flags) {
        return new Jar(jar, flags);
    }

    public static Jar read(final File file, final int flags) {
        try {
            return new Jar(new JarFile(file), flags);
        } catch (final Throwable _t) { if ((flags & DEBUG) == 0) _t.printStackTrace(System.err); throw new RuntimeException("Failed to read jar " + file.getAbsolutePath()); }
    }

    public static Jar read(final String path, final int flags) {
        return read(new File(path), flags);
    }

    private Class read(final JarEntry entry) throws IOException {
        try (final InputStream is = jar.getInputStream(entry)) {
            final Class node = new Class();
            new ClassReader(is).accept(node, 256/*4 | 256*/); // SKIP_FRAMES | EXPAND_ASM_INSNS
            node.buf = jar.getInputStream(entry).readAllBytes();
            return node;
        }
    }

    public void write(final String path, final byte[] data) {
        files.put(path, data);
    }

    public void export(final String path) {
        export(new File(path));
    }

    @SneakyThrows
    public void export(final File output) {

        try (final JarOutputStream jos = new JarOutputStream(Files.newOutputStream(output.toPath()))) {
            classes.forEach(node -> {
                try {
                    jos.putNextEntry(new JarEntry(node.name.concat(".class")));

                    final ClassWriter writer = new ClassWriter(3) {
                        @Override
                        protected ClassLoader getClassLoader() {
                            return loader;
                        }
                    };

                    node.accept(writer);

                    jos.write(writer.toByteArray());
                    jos.closeEntry();
                } catch (final Throwable _t) {
                    try {
                        final ClassWriter writer = new ClassWriter(0) {
                            @Override
                            protected ClassLoader getClassLoader() {
                                return loader;
                            }
                        };

                        node.accept(writer);

                        jos.write(writer.toByteArray());
                        jos.closeEntry();
                        System.out.println("Computed node " + node.name + " without frames.");
                        if ((this.flags & DEBUG) == 0) _t.printStackTrace(System.err);
                    } catch (final Throwable ex) {
                        System.err.println("Error writing node: " + node.name);
                        if ((this.flags & DEBUG) == 0) ex.printStackTrace(System.err);
                        try {
                            System.out.println("Fallbacking on " + node.name);
                            jos.write(node.buf);
                            jos.closeEntry();
                        } catch (final Throwable __T) {
                            System.out.println("Failed to fallback on node: " + node.name);
                            __T.printStackTrace(System.err);
                        }
                    }
                }
            });
            files.forEach((name, bytes) -> {
                try {
                    jos.putNextEntry(new JarEntry(name));
                    jos.write(bytes);
                    jos.closeEntry();
                } catch (final Throwable _t) {
                    throw new RuntimeException("Error writing file: " + name, _t);
                }
            });
        }
    }

    private static final class FramedClassLoader extends ClassLoader {
        private final Jar jar;

        public FramedClassLoader(final Jar jar) {
            this.jar = jar;
        }

        @Override
        protected java.lang.Class<?> findClass(final String name) throws ClassNotFoundException {
            try {
                byte[] buf = jar.get(name.replace('.', '/')).buf;
                if (buf == null) {
                    System.out.println("Generating buffer for: " + name);
                    final Class klass = jar.get(name.replace('.', '/'));
                    final ClassWriter writer = new ClassWriter(0);
                    klass.accept(writer);
                    buf = writer.toByteArray();
                }
                return defineClass(name, buf, 0, buf.length);
            } catch (final Throwable _t) {
                try {
                    System.out.println("Failed to find library for: " + name);
                    return super.findClass(name);
                } catch (Throwable __t) {
                    __t.printStackTrace(System.err);
                    return null;
                }
            }
        }
    }

    public String getCommonSuperClass(final String type1, final String type2) {
        java.lang.Class<?> class1;
        try {
            class1 = java.lang.Class.forName(type1.replace('/', '.'), false, loader);
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(type1, e);
        }
        java.lang.Class<?> class2;
        try {
            class2 = java.lang.Class.forName(type2.replace('/', '.'), false, loader);
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(type2, e);
        }
        if (class1.isAssignableFrom(class2)) {
            return type1;
        }
        if (class2.isAssignableFrom(class1)) {
            return type2;
        }
        if (class1.isInterface() || class2.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                class1 = class1.getSuperclass();
            } while (!class1.isAssignableFrom(class2));
            return class1.getName().replace('.', '/');
        }
    }
}