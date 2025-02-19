package dev.name.transformer.transformers.obf.dynamic;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.types.Access;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodType;

@SuppressWarnings("all")
public final class ConstantDynamicTransformer extends Transformer {
    private static final String RESOLVER_NAME = "cd_resolve";

    @Override
    public String name() {
        return "Constant Dynamic Transformer";
    }

    @Override
    public void transform(ClassPool pool) {
        for (Class k : pool) {
            k.version = V17; // this is useless, just to fix obf-test iridium lib.

            for (Method m : k.methods.toArray(new Method[0])) { // array for concurrent mod thx java
                if (m.access.isAbstract() || m.access.isNative() || m.instructions.size() <= 0) continue;

                m.instructions.forEach(node -> node instanceof Constant constant && constant.cst != null, node -> {
                    Constant constant = (Constant) node;

                    Object cst = constant.cst;
                    Object adjusted = cst instanceof Character c ? (int) c : cst instanceof Boolean b ? (b ? 1 : 0) : cst;

                    String base = descriptor(adjusted);
                    String desc = String.format("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;%s)%s", base, base);

                    Method bsm = k.getMethod(RESOLVER_NAME, desc);

                    if (bsm == null) k.addMethod(bsm = bootstrap(RESOLVER_NAME, desc));

                    Handle unpatched = bsm.handle();
                    Handle handle = new Handle(H_INVOKESTATIC, unpatched.getOwner(), unpatched.getName(), unpatched.getDesc(), k.access.isInterface());
                    constant.cst = new ConstantDynamic("anything", base, handle, constant.cst);
                });
            }
        }
    }

    private static Method bootstrap(String name, String desc) {
        Type ret = Type.getReturnType(desc);
        InstructionBuilder builder = InstructionBuilder.generate();
        Method method = new Method(Access.builder()._private()._static().build(), name, desc, null, new String[0]);

        switch (ret.getSort()) {
            case Type.INT -> {
                builder.iload(3);
                builder.ireturn();
            }
            case Type.LONG -> {
                builder.lload(3);
                builder.lreturn();
            }
            case Type.FLOAT -> {
                builder.fload(3);
                builder.freturn();
            }
            case Type.DOUBLE -> {
                builder.dload(3);
                builder.dreturn();
            }
            case Type.OBJECT -> {
                builder.aload(3);
                builder.areturn();
            }
        }

        method.setInstructions(builder.build());

        return method;
    }

    private static String descriptor(final Object obj) {
        if (obj instanceof Type) return "Ljava/lang/Class;";
        else if (obj instanceof MethodType) return "Ljava/lang/invoke/MethodType;";
        else if (obj instanceof Handle) return "Ljava/lang/invoke/MethodHandle;";
        else if (obj instanceof Double) return "D";
        else if (obj instanceof Float) return "F";
        else if (obj instanceof Long) return "J";
        else if (obj instanceof Integer) return "I";
        else if (obj instanceof String) return "Ljava/lang/String;";
        else return null;
    }
}