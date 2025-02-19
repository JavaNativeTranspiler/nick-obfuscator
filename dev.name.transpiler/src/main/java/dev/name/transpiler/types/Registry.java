package dev.name.transpiler.types;

import dev.name.transpiler.builder.TranspiledClass;
import dev.name.transpiler.builder.TranspiledMethod;

import java.util.ArrayList;
import java.util.List;

public class Registry {
    private final TranspiledClass klass;
    private final List<MethodDefinition> methods = new ArrayList<>();

    private Registry(final TranspiledClass klass) {
        this.klass = klass;
    }

    public static Registry create(final TranspiledClass klass) {
        return new Registry(klass);
    }

    private void register() {
        for (final TranspiledMethod method : klass)
            methods.add(method.definition);
    }

    @Override
    public String toString() {
        register();

        final StringBuilder sb = new StringBuilder();

        sb.append("static JNINativeMethod method_table[] = {\n");

        final int size = methods.size();
        final int max = size - 1;
        for (int i = 0, n = methods.size(); i < n; i++) {
            final MethodDefinition definition = methods.get(i);
            sb.append(String.format("   {\"%s\", \"%s\", (void*) %s}", definition.NAME, definition.DESC, definition.FUNCTION_NAME));
            if (i != max) sb.append(",\n");
        }

        sb.append("\n};");

        return sb.toString();
    }
}