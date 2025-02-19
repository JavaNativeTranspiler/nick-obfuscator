package dev.name.transformer.transformers.obf.hash;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Flags;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;
import dev.name.transformer.Transformer;
import dev.name.util.collections.generic.Pair;
import dev.name.util.java.ClassPool;
import dev.name.util.math.Random;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.name.util.asm.Bytecode.previous;

// TODO only thing that could break this is if we have a instruction that could potentially jump to a exception handler.
public class HashTransformer extends Transformer implements Opcodes, Random {
    public static int NO_DETOUR = 0b10001000100101010;

    private static final int MAGNITUDE_MIN = 0;
    private static final int MAGNITUDE_MAX = 1;
    private static final int KEY_MIN = 3;
    private static final int KEY_MAX = 7;

    private static void apply(final Method method) {
        final Instructions instructions = method.instructions;
        if (method.access.isAbstract() || method.access.isNative() || instructions.size() <= 0 || instructions.count(node -> node instanceof Constant constant && (constant.cst instanceof Number || constant.cst instanceof Boolean || constant.cst instanceof Character)) <= 0) return;

        if (!(instructions.first instanceof Label)) instructions.insert(new Label());
        Processor.process(instructions, Processor.Mode.PRE);

        final LocalPool pool = new LocalPool(method);
        final LocalPool.Local[] keys = new LocalPool.Local[RANDOM.nextInt(KEY_MIN, KEY_MAX)];
        for (int i = 0, n = keys.length; i < n; i++) keys[i] = pool.allocate(LocalPool.LONG);

        final Map<Label, HashBlock> blocks = new LinkedHashMap<>();

        for (final Node node : instructions) {
            if (!(node instanceof Label label)) continue;
            blocks.put(label, new HashBlock(method, label, keys, RANDOM.nextInt(MAGNITUDE_MIN, MAGNITUDE_MAX)));
        }

        method.blocks.forEach(block -> {
            // same handler different starts
            method.blocks.stream()
                    .filter(merge -> block.handler == merge.handler && block.start != merge.start)
                    .forEach(merge -> blocks.get(block.start).merge(blocks.get(merge.start)));

            // same start different handlers
            method.blocks.stream()
                    .filter(merge -> block.start == merge.start && block.handler != merge.handler)
                    .forEach(merge -> blocks.get(block.start).merge(blocks.get(merge.start)));
        });

        final HashBlock first = blocks.get((Label) instructions.first);
        instructions.insert(first.initializer());

        final Block[] _blocks = method.blocks.toArray(new Block[0]);

        // looping encrypt does not work with instruction reinterpolation.
        blocks.values().forEach(HashBlock::encrypt);
        Bytecode.wrapLabels(instructions, l -> !l.flags.has(NO_DETOUR));
        Processor.process(method, Processor.Mode.PRE);

        for (final Block block : _blocks)
            block.handler.flags.set(Flags.Instruction.HANDLER, true);

        for (final Node instruction : instructions.toArray()) {
            if (instruction.flags.has(NO_DETOUR)) continue;
            switch (instruction.type()) {
                case Node.JUMP -> {
                    final Jump jmp = (Jump) instruction;
                    jmp.label = detour(instructions, first, blocks.get(previousLabel(jmp)), blocks.get(jmp.label));
                }
                case Node.LOOKUP -> {
                    final Lookup lookup = (Lookup) instruction;
                    final Label parent = previousLabel(lookup);
                    Arrays.setAll(lookup.labels, i -> detour(instructions, first, blocks.get(parent), blocks.get(lookup.labels[i])));
                    lookup._default = detour(instructions, first, blocks.get(parent), blocks.get(lookup._default));
                }
                case Node.TABLE -> {
                    final Table table = (Table) instruction;
                    final Label parent = previousLabel(table);
                    Arrays.setAll(table.labels, i -> detour(instructions, first, blocks.get(parent), blocks.get(table.labels[i])));
                    table._default = detour(instructions, first, blocks.get(parent), blocks.get(table._default));
                }
            }
        }

        /*final Map<Label, List<Label>> recovery = new HashMap<>();

        for (final Block block : _blocks) {
            final Label start = block.start;
            List<Label> labels = recovery.get(start);
            if (labels == null) labels = new ArrayList<>();
            labels.add(block.handler);
            recovery.put(start, labels);
        }*/

        for (final Block block : _blocks) {
            final Label begin = block.start, handler = block.handler;
            //if (!visited.add(begin)) continue;

            final LocalPool.Local recovery = pool.allocate(LocalPool.LONG);
            final Pair<Instructions, Long> saved = blocks.get(begin).save(recovery);

            begin.insertAfter(saved.left);
            handler.insertAfter(blocks.get(handler).recover(recovery, saved.right));
        }
    }

    private static Label previousLabel(final Node base) {
        final Node prev = previous(base, l -> l instanceof Label label && !label.flags.has(NO_DETOUR));
        return prev != null ? (Label) prev : null;
    }

    private static Label detour(final Instructions instructions, final HashBlock fallback, final HashBlock curr, final HashBlock dest) {
        final Label l = new Label();
        instructions.add(l);
        final HashBlock block = curr != null ? curr : fallback;
        instructions.add(block.jump(dest));
        return l;
    }

    @Override
    public String name() {
        return "Hash Transformer";
    }

    @Override
    public void transform(final ClassPool pool) {
        pool.forEach(klass -> klass.methods.forEach(HashTransformer::apply));
    }
}