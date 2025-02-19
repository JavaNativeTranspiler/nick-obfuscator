package dev.name.transpiler.types;

import dev.name.transpiler.builder.TranspiledMethod;

public class Stack {
    private final TranspiledMethod method;

    private Stack(final TranspiledMethod method) {
        this.method = method;
    }

    public String access(final int ptr, final Type type) {
        return String.format("stack%d.%s", ptr, type.id);
    }

    public String initializer() {
        final StringBuilder builder = new StringBuilder();
/*        final int max = method.

        for (int i = 0; i < entries.size(); i++) {
            if (i == 0) builder.append("jvalue ");
            builder.append(entries.get(i));
            if (i == max) builder.append(';');
            else builder.append(", ");
        }*/

        return builder.toString();
    }
}