package dev.name.transpiler.types;

import dev.name.asm.ir.types.Node;
import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.analysis.BasicValue;

@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class StackFrame {
    public final int ptr;
    public final Node node;
}