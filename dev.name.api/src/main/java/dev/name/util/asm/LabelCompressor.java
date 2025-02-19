package dev.name.util.asm;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Jump;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.nodes.Lookup;
import dev.name.asm.ir.nodes.Table;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;
import dev.name.util.collections.list.FastArrayList;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.lambda.BiConsumer;
import dev.name.util.lambda.Function;

import java.util.List;

public final class LabelCompressor {
    private static final BiConsumer<FastHashSet<Label>, List<Block>> exceptions = (labels, blocks) -> {
        for (Block block : blocks) {
            labels.addAll(block.start, block.end, block.handler);
        }
    };

    private static final BiConsumer<FastHashSet<Label>, Lookup> lookup = (labels, lookup) -> {
        labels.addAll(lookup.labels);
        labels.add(lookup._default);
    };

    private static final BiConsumer<FastHashSet<Label>, Table> table = (labels, table) -> {
        labels.addAll(table.labels);
        labels.add(table._default);
    };

    private static final BiConsumer<FastHashSet<Label>, Jump> jump = (labels, jump) -> {
        labels.add(jump.label);
    };

    private static final BiConsumer<FastHashSet<Label>, Instructions> instructions = (labels, instructions) -> {
        for (Node node : instructions)
            switch (node.type()) {
                case Node.JUMP -> jump.accept(labels, (Jump) node);
                case Node.LOOKUP -> lookup.accept(labels, (Lookup) node);
                case Node.TABLE -> table.accept(labels, (Table) node);
            }
    };

    private static final Function<Instructions, FastArrayList<Label>> collect = (instructions) -> {
        final FastArrayList<Label> list = new FastArrayList<>();
        instructions.forEach(node -> node instanceof Label, node -> list.add((Label) node));
        return list;
    };

    public static void compress(Method method) {
        final FastHashSet<Label> referenced = new FastHashSet<>();
        exceptions.accept(referenced, method.blocks);
        instructions.accept(referenced, method.instructions);
        final FastArrayList<Label> labels = collect.evaluate(method.instructions);

        for (Label l : labels) {
            if (referenced.contains(l)) continue;
            l.delete();
        }
    }
}