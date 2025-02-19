package dev.name.transpiler.types;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Increment;
import dev.name.asm.ir.nodes.Variable;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.name.asm.ir.types.LocalPool.*;

@SuppressWarnings("unused")
public class VarPool {
    @RequiredArgsConstructor
    @SuppressWarnings("ClassCanBeRecord")
    private static final class Entry {
        public final String name;
        public final boolean parameter;
    }

    private final Map<Integer, Entry> VARIABLES = new HashMap<>();
    private final Method method;

    private VarPool(final Method method) {
        this.method = method;
        for (final Node node : method.instructions) {
            if (node instanceof Increment increment) cache(increment.local);
            else if (node instanceof Variable variable) cache(variable);
        }
    }

    private void cache(final Variable variable) {
        VARIABLES.putIfAbsent(variable.index, new Entry(String.format("%s%d", type(variable.opcode), variable.index), false));
    }

    private static String type(final int opcode) {
        return switch (LocalPool.translate(opcode)) {
            case INT -> "I";
            case FLOAT -> "F";
            case OBJECT -> "A";
            case LONG -> "J";
            case DOUBLE -> "D";
            default -> throw new IllegalArgumentException("bad opcode: " + opcode);
        };
    }

    public String id(final int index) {
        return VARIABLES.get(index).name;
    }

    public String id(final Variable variable) {
        return id(variable.index);
    }

    public void merge(final Parameter[] parameters) {
        for (int i = 0, index = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            VARIABLES.put(index, new Entry(parameter.name, true));
            final JNIType type = parameter.type;
            index += (type == JNIType.LONG || type == JNIType.DOUBLE) ? 2 : 1;
        }
    }

    public String initializer() {
        final StringBuilder builder = new StringBuilder();

        final List<String> entries = VARIABLES.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(entry -> !entry.parameter)
                .map(entry -> String.format("%s{}", entry.name))
                .toList();

        final int max = entries.size() - 1;

        for (int i = 0; i < entries.size(); i++) {
            if (i == 0) builder.append("jvalue ");
            builder.append(entries.get(i));
            if (i == max) builder.append(';');
            else builder.append(", ");
        }

        return builder.toString();
    }

    public static VarPool create(final Method method) {
        return new VarPool(method);
    }
}