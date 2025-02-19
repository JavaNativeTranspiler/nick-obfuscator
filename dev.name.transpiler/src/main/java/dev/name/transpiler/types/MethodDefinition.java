package dev.name.transpiler.types;

import dev.name.asm.ir.components.Method;
import org.objectweb.asm.Type;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class MethodDefinition {
    private static final Random RANDOM = new SecureRandom();

    public final Parameter[] parameters;
    public final String FUNCTION_NAME, NAME, DESC;
    private final JNIType RET;
    private final String DEFINITION;

    private MethodDefinition(final Method method) {
        final boolean isInstance = !method.access.isStatic();
        final Type[] args = Type.getArgumentTypes(method.desc);
        final int base = isInstance ? 1 : 0;
        final int params = args.length + base;
        this.parameters = new Parameter[params];

        if (isInstance) parameters[0] = new Parameter(JNIType.OBJECT, "_this");

        int index = base;
        for (final Type type : args) parameters[index] = new Parameter(index++, type);

        this.NAME = method.name;
        this.FUNCTION_NAME = method.name.replaceAll("[<>]", "\\$").concat("_").concat(UUID.randomUUID().toString().replace("-", ""));
        this.DESC = method.desc;
        this.RET = JNIType.parse(Type.getReturnType(method.desc));

        final int max = params - 1;
        final StringBuilder paramString = new StringBuilder();
        for (int i = 0; i < params; i++) {
            paramString.append(parameters[i].toString());
            if (i != max) paramString.append(", ");
        }

        this.DEFINITION = String.format("__forceinline %s %s(%s)", String.format("%s%s", isInstance ? "" : "static ", this.RET.desc), this.FUNCTION_NAME, paramString);
    }

    public static MethodDefinition create(final Method method) {
        return new MethodDefinition(method);
    }

    @Override
    public String toString() {
        return DEFINITION;
    }
}