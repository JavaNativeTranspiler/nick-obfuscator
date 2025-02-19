package dev.name.transformer.transformers.obf.flow.number;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;
import dev.name.util.math.Math;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import org.objectweb.asm.Opcodes;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// TODO use less wrapping
public class NumberFlow implements Opcodes {
    private static final Random RANDOM = new SecureRandom();

    public static void apply(final Method method, final int idx, final long ky) {
        final Instructions instructions = method.instructions;
        if (method.access.isAbstract() || method.access.isNative() || instructions.size() <= 0) throw new IllegalArgumentException();
        if (!(instructions.first instanceof Label)) instructions.insert(new Label());
        Processor.process(instructions, Processor.Mode.PRE); // patch constants

        final LocalPool pool = new LocalPool(method);
        final Key key = new Key(ky, idx);

        instructions.insert(new Variable(LSTORE, key.index));
        instructions.insert(new Constant(key.val));

        final Map<Label, FlowData> labels = compute(method);

        labels.forEach((label, data) -> encrypt(label, key.index, data.key));
        Bytecode.wrapLabels(instructions);
        Processor.process(method, Processor.Mode.PRE); // dead code breaks other transformers so we die it

        for (final Node instruction : instructions.toArray())
            if (instruction instanceof Jump jmp) {
                final Label target = jmp.label;
                final Label parent = (Label) Bytecode.previous(jmp, k -> k instanceof Label);
                jmp.label = jump(instructions, labels.get(target), target, labels.get(parent), parent, key);
            } else if (instruction instanceof Lookup lookup) {
                final Label parent = (Label) Bytecode.previous(lookup, k -> k instanceof Label);
                for (int i = 0; i < lookup.labels.length; i++) {
                    final Label target = lookup.labels[i];
                    lookup.labels[i] = jump(instructions, labels.get(target), target, labels.get(parent), parent, key);
                }
                final Label target = lookup._default;
                lookup._default = jump(instructions, labels.get(target), target, labels.get(parent), parent, key);
            } else if (instruction instanceof Table table) {
                final Label parent = (Label) Bytecode.previous(table, k -> k instanceof Label);
                for (int i = 0; i < table.labels.length; i++) {
                    final Label target = table.labels[i];
                    table.labels[i] = jump(instructions, labels.get(target), target, labels.get(parent), parent, key);
                }
                final Label target = table._default;
                table._default = jump(instructions, labels.get(target), target, labels.get(parent), parent, key);
            }

        final Set<Label> visited = new HashSet<>();

        // Fix
        for (final Block block : method.blocks) {
            final Label begin = block.start;
            if (visited.contains(begin)) continue;
            final Label handler = block.handler;
            final long[] k = Math.xor(labels.get(handler).key, 1, labels.get(begin).key);
            final LocalPool.Local local = pool.allocate(LocalPool.LONG);

            begin.insertAfter(new Variable(LSTORE, local.index));
            begin.insertAfter(new Instruction(LXOR));
            begin.insertAfter(new Constant(k[1]));
            begin.insertAfter(new Variable(LLOAD, key.index));

            handler.insertAfter(new Variable(LSTORE, key.index));
            handler.insertAfter(new Variable(LLOAD, local.index));

            visited.add(begin);
        }
    }

    @AllArgsConstructor
    private static class FlowData {
        public final long key;
        public final Block handler;
    }

    @AllArgsConstructor
    private static class Key {
        public final long val;
        public final int index;
    }

    private static Label jump(final Instructions instructions, final FlowData data, final Label target, final FlowData parentData, final Label parent, final Key key) {
        final Label label = new Label();
        instructions.add(label);

        final long r = parent == null ? key.val : parentData.key;
        final long[] segments = Math.xor(data.key, 1, r);
        final int size = segments.length;
        final int end = size - 1;
        instructions.add(new Variable(LLOAD, key.index));

        for (int i = 1; i < size; i++) {
            instructions.add(new Constant(segments[i]));
            instructions.add(new Instruction(LXOR));
            instructions.add(new Variable(LSTORE, key.index));
            if (i != end) instructions.add(new Variable(LLOAD, key.index));
        }

        instructions.add(new Jump(GOTO, target));
        return label;
    }

    private static void encrypt(final Label label, final int index, final long key) {
        Node curr = label.next;

        // must fix replacement method. next -> null -> no obf
        while (curr != null && !(curr instanceof Label)) {
            Node next = curr.next;

            if (!(curr instanceof Constant constant)) {
                curr = next;
                continue;
            }

            final Object cst = constant.cst;

            if (!(cst instanceof Number number)) {
                curr = next;
                continue;
            }

            final long r = (number instanceof Double) ? Double.doubleToLongBits(number.doubleValue()) : (number instanceof Float) ? Float.floatToIntBits(number.floatValue()) : number.longValue();
            final long[] k = Math.xor(r, 1, key);

            curr.insertBefore(new Variable(LLOAD, index));
            curr.insertBefore(new Constant(k[1]));
            curr.insertBefore(new Instruction(LXOR));

            switch (number.getClass().getSimpleName()) {
                case "Boolean", "Integer" -> curr.replace(new Instruction(L2I));
                case "Byte" -> {
                    curr.insertBefore(new Instruction(L2I));
                    curr.replace(new Instruction(I2B));
                }
                case "Short" -> {
                    curr.insertBefore(new Instruction(L2I));
                    curr.replace(new Instruction(I2S));
                }
                case "Character" -> {
                    curr.insertBefore(new Instruction(L2I));
                    curr.replace(new Instruction(I2C));
                }
                case "Long" -> curr.delete();
                case "Float" -> {
                    curr.insertBefore(new Instruction(L2I));
                    curr.replace(new Invoke(INVOKESTATIC, "java/lang/Float", "intBitsToFloat", "(I)F"));
                }
                case "Double" -> curr.replace(new Invoke(INVOKESTATIC, "java/lang/Double", "longBitsToDouble", "(J)D"));
                default -> throw new IllegalArgumentException("did not expect: " + number.getClass().getSimpleName());
            }

            curr = next;
        }
    }

    private static Map<Label, FlowData> compute(final Method method) {
        final Map<Label, Block> exceptions = new Object2ObjectOpenHashMap<>();
        final Map<Label, FlowData> data = new Object2ObjectOpenHashMap<>();
        method.blocks.forEach(block -> exceptions.put(block.handler, block));

        for (final Node instruction : method.instructions)
            if (instruction instanceof Label label)
                data.put(label, new FlowData(RANDOM.nextLong(), exceptions.get(label)));

        for (final Block block : method.blocks)
            for (final Block merge : method.blocks)
                if (block.handler == merge.handler && block.start != merge.start)
                    data.put(merge.start, data.get(block.start));

        return data;
    }
}