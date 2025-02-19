package dev.name.transformer.transformers.obf.vm;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.nodes.Instruction;
import dev.name.asm.ir.nodes.Invoke;
import dev.name.asm.ir.types.Access;
import dev.name.asm.ir.types.Node;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import dev.name.util.java.Jar;
import dev.name.util.math.Random;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetourTransformer extends Transformer implements Random {
    @Override
    public String name() {
        return "Detour Transformer";
    }

    @Override
    public void transform(final ClassPool pool) {
        for (final Class klass : pool.getClasses().toArray(new Class[0]))
            try {
                apply(klass, jar);
                final Method m = klass.getMethod("<clinit>", "()V");
                if (m == null) {
                    final Method clinit = new Method(Access.builder()._static().build(), "<clinit>", "()V", null, List.of());
                    klass.addMethod(clinit);
                    clinit.instructions.add(new Constant(klass.name.replace('/', '.')));
                    clinit.instructions.add(klass.constant());
                    clinit.instructions.add(new Invoke(INVOKESTATIC, "me/nick/BootstrapClassLoader", "define_event", "(Ljava/lang/String;Ljava/lang/Class;)V", false));
                    clinit.instructions.add(new Instruction(RETURN));
                } else {
                    m.instructions.insert(new Invoke(INVOKESTATIC, "me/nick/BootstrapClassLoader", "define_event", "(Ljava/lang/String;Ljava/lang/Class;)V", false));
                    m.instructions.insert(klass.constant());
                    m.instructions.insert(new Constant(klass.name.replace('/', '.')));
                }
            } catch (final Throwable _t) {
                _t.printStackTrace(System.err);
            }
    }

    private static void fill(final byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int r = RANDOM.nextInt(36);
            if (r < 10) arr[i] = (byte) ('0' + r);
            else arr[i] = (byte) ('a' + (r - 10));
        }
    }

    private static final class Detour {
        private final Method detour = new Method();
        private final String fake_name;
        private final String real_name;
        public final String id;

        private final int opcode;
        private final String m_owner, m_name, o_desc;
        private String m_desc;

        public Detour(final Class klass, final String id, final Invoke invoke) {
            detour.klass = klass;
            final byte[] _fake_name = new byte[RANDOM.nextInt(4, 16)];
            final byte[] _real_name = new byte[RANDOM.nextInt(4, 16)];
            fill(_fake_name);
            fill(_real_name);
            this.real_name = "nick_jdk_" + new String(_real_name);
            this.fake_name = this.real_name; //"nick_jdk_" + new String(_fake_name);
            detour.name = this.fake_name;
            detour.desc = "()V";
            detour.access = Access.builder()._static()._private().build();

            this.id = id;

            this.opcode = invoke.opcode;
            this.m_owner = invoke.owner;
            this.m_name = invoke.name;
            this.o_desc = invoke.desc;
            this.m_desc = invoke.desc;

            if (this.opcode != INVOKESTATIC) {
                String sub = this.m_desc.substring(1, this.m_desc.lastIndexOf(')'));
                String ret = this.m_desc.substring(this.m_desc.lastIndexOf(')') + 1);
                this.m_desc = String.format(!this.m_owner.contains("[") ? "(L%s;%s)%s" : "(%s%s)%s", this.m_owner, sub, ret);
            }

            detour.desc = this.m_desc;

            final Type return_type = Type.getReturnType(detour.desc);
            detour.instructions = new Instructions();

            detour.instructions.add(new dev.name.asm.ir.nodes.Type(NEW, "java/lang/RuntimeException"));
            detour.instructions.add(new Instruction(Opcodes.DUP));
            detour.instructions.add(new Invoke(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V"));
            detour.instructions.add(new Instruction(ATHROW));
            if (return_type.equals(Type.BOOLEAN_TYPE) || return_type.equals(Type.CHAR_TYPE) || return_type.equals(Type.BYTE_TYPE) || return_type.equals(Type.SHORT_TYPE) || return_type.equals(Type.INT_TYPE)) {
                detour.instructions.add(new Constant(0));
                detour.instructions.add(new Instruction(IRETURN));
            } else if (return_type.equals(Type.LONG_TYPE)) {
                detour.instructions.add(new Constant(0L));
                detour.instructions.add(new Instruction(LRETURN));
            }else if (return_type.equals(Type.DOUBLE_TYPE)) {
                detour.instructions.add(new Constant(0D));
                detour.instructions.add(new Instruction(DRETURN));
            } else if (return_type.equals(Type.VOID_TYPE)) {
                detour.instructions.add(new Instruction(RETURN));
            } else {
                detour.instructions.add(new Constant(null));
                detour.instructions.add(new Instruction(ARETURN));
            }


        }

        public Invoke call() {
            return new Invoke(INVOKESTATIC, this.detour.klass.name, this.real_name, this.m_desc, this.detour.klass.access.isInterface());
        }

        public String register() {
            //return String.format("DETOUR(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", this.fake_name, this.real_name, this.detour.desc, this.m_desc, this.m_owner, this.m_name, this.o_desc);
            return String.format("DETOUR(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", this.detour.klass.name, this.real_name, this.real_name, this.m_desc, this.m_desc, this.m_owner, this.m_name, this.o_desc);
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Detour det && det.id.equals(this.id);
        }
    }

    private static String id(final Invoke invoke) {
        return String.format("%s.%s %s", invoke.owner, invoke.name, invoke.desc);
    }

    private static void apply(final Class klass, final Jar jar) throws Throwable {
        final List<Detour> detours = new ArrayList<>(); // if we use a map it looks to noticeable.
        //final Map<String, Detour> detourMap = new HashMap<>();
        final StringBuilder builder = new StringBuilder();

        for (final Method method : klass.methods)
            for (final Node node : method.instructions) {
                if (!(node instanceof Invoke invoke)) continue;
                if (invoke.opcode == INVOKESPECIAL) continue; // not dealing with constructors
                if (invoke.opcode == INVOKEINTERFACE) continue; // implement after virtual & static.
                if (invoke.opcode == INVOKEVIRTUAL) continue; // requires vtable lookup for interface and default and miranda methods.
                if (invoke.name.equals("invoke") && invoke.owner.contains("MethodHandle")) continue; // polymorphic signatures are resolved at runtime.
                //if (jar.isHotspotIntrinsic(invoke)) continue;
                final String id = id(invoke);
                final Detour detour = new Detour(klass, id, invoke);
                detours.add(detour);
                invoke.replace(detour.call());
            }

        builder.append(String.format("detour_dispatchers[\"%s\"] = [](JNIEnv* env, jclass cls)\n{\n    ", klass.name.replace('/', '.'))).append(String.format("KLASS(\"%s\");", klass.name)).append('\n');

        detours.forEach(d -> {
            builder.append("    ").append(d.register()).append('\n');
            klass.addMethod(d.detour);
        });

        builder.append("};\n");

        new FileOutputStream("detours.txt", true).write(builder.toString().getBytes());
    }
}
