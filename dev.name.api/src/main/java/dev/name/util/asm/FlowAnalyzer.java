package dev.name.util.asm;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Jump;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.nodes.Lookup;
import dev.name.asm.ir.nodes.Table;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.Getter;
import org.objectweb.asm.Opcodes;

import static dev.name.util.asm.Bytecode.isReturn;

@Getter
@SuppressWarnings("all")
public class FlowAnalyzer implements Opcodes {
    private final Method method;
    private final ObjectLinkedOpenHashSet<Node> frames = new ObjectLinkedOpenHashSet<>();

    public FlowAnalyzer(final Method method) {
        this.method = method;
    }

    public void analyze() {
        execute(method.instructions.first);
        exceptions();
    }

    private void execute(final Node begin) {
        try {
            Node node = begin;

            while (true) {
                if (node == null) break;
                if (node.opcode != NOP) frames.add(node);
                if (isReturn(node) || node.opcode == ATHROW) break;
                if (node instanceof Jump jump && jump(jump)) break;
                if (node instanceof Lookup lookup) {
                    lookupswitch(lookup);
                    break;
                } else if (node instanceof Table table) {
                    tableswitch(table);
                    break;
                }
                node = node.next;
            }
        } catch (final StackOverflowError _t) {  }
    }

    private void jmp(final Label label) {
        if (!frames.contains(label)) execute(label);
    }

    private boolean jump(final Jump jmp) {
        if (jmp.opcode == JSR) throw new Error("unexpected jsr");
        jmp(jmp.label);
        return jmp.opcode == GOTO;
    }

    private void lookupswitch(final Lookup lookup) {
        jmp(lookup._default);
        for (final Label label : lookup.labels) jmp(label);
    }

    private void tableswitch(final Table table) {
        jmp(table._default);
        for (final Label label : table.labels) jmp(label);
    }

    private void exceptions() {
        for (final Block block : this.method.blocks)
            jmp(block.handler);
    }
}