package dev.name.transpiler.types;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;

import java.util.HashMap;
import java.util.Map;

public class LabelMap {
    private static final Map<Label, String> IDENTIFIERS = new HashMap<>();

    private LabelMap(final Method method) {
        for (int i = 0, n = method.blocks.size(); i < n; i++) {
            final Block block = method.blocks.get(i);
            IDENTIFIERS.put(block.start, String.format("EX_START%d", i));
            IDENTIFIERS.put(block.end, String.format("EX_END%d", i));
            IDENTIFIERS.put(block.handler, String.format("EX_HANDLER%d", i));
        }

        int index = 0;
        for (final Node node : method.instructions) {
            if (!(node instanceof Label label)) continue;
            if (IDENTIFIERS.containsKey(label)) continue;
            IDENTIFIERS.put(label, String.format("L%d", index++));
        }
    }

    public static LabelMap create(final Method method) {
        return new LabelMap(method);
    }

    public String id(final Label label) {
        return IDENTIFIERS.get(label);
    }

    public String bind(final Label label) {
        return String.format("%s:", id(label));
    }

    public String goto_(final Label label) {
        return String.format("goto %s", id(label));
    }
}