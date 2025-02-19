package dev.name.transformer.transformers.deobf.generic;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Var;
import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.processors.methods.DeadcodeProcessor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.pattern.Matcher;
import dev.name.asm.ir.pattern.Pattern;
import dev.name.asm.ir.types.Node;
import dev.name.transformer.Transformer;
import dev.name.util.asm.Bytecode;
import dev.name.util.asm.LabelCompressor;
import dev.name.util.collections.array.Arrays;
import dev.name.util.java.ClassPool;
import dev.name.util.lambda.Consumer;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.Opcodes;

import static dev.name.util.asm.Bytecode.C1;
import static dev.name.util.asm.Bytecode.C2;

@SuppressWarnings("all")
public final class Folder extends Transformer implements Opcodes {
    @RequiredArgsConstructor
    @SuppressWarnings("ClassCanBeRecord")
    private static final class Replacer {
        private final Pattern pattern;
        private final Consumer<Pattern.Range> replacement;

        public int execute(Method method) {
            int total = 0;

            for (Pattern.Range range : pattern.match_all(method)) {
                replacement.accept(range);
                total++;
            }

            return total;
        }
    }

    @Override
    public String name() {
        return "Folder";
    }

    @Override
    public void transform(ClassPool pool) {
        for (Class k : pool) {
            k.methods.forEach(Folder::fold);
        }
    }

    private static final Replacer X1_SWAP = new Replacer(Pattern.of(DUP_X1, POP), (range -> range.replace(new Instruction(SWAP))));
    private static final Replacer X1_DUP = new Replacer(Pattern.of(DUP, DUP_X1), (range -> range.replace(new Instruction(DUP2))));
    private static final Replacer X1_POP2 = new Replacer(Pattern.of(DUP_X1, POP2), (range -> range.replace(new Instruction(SWAP), new Instruction(POP))));
    private static final Replacer DUP_POP2 = new Replacer(Pattern.of(DUP, POP2), (range -> range.replace(new Instruction(POP))));
    private static final Replacer DUP_SWAP = new Replacer(Pattern.of(DUP, SWAP), (range -> range.end.delete()));
    private static final Replacer POP_POP = new Replacer(Pattern.of(POP, POP), (range -> range.replace(new Instruction(POP2))));
    private static final Replacer VAR_POP = new Replacer(Pattern.of(new Matcher(Bytecode::isLoadable), new Matcher(POP)), (Pattern.Range::clear));

    private static final Replacer VAR_POP2 = new Replacer(Pattern.of(new Matcher(Bytecode::isLoadable), new Matcher(POP2)), (range -> {
        switch (Bytecode.getSize(range.start)) {
            case C1 -> range.replace(new Instruction(POP));
            case C2 -> range.clear();
            default -> throw new RuntimeException();
        }
    }));

    private static final Replacer VAR_DUP = new Replacer(Pattern.of(new Matcher(Bytecode::isLoadable), new Matcher(DUP)), (range -> {
        if (range.start instanceof Variable var) {
            range.end.replace(new Variable(var.opcode, var.index));
        } else if (range.start instanceof Constant constant) {
            range.end.replace(new Constant(constant.cst));
        }
    }));

    private static final Replacer VAR_DUP2 = new Replacer(Pattern.of(new Matcher(node -> Bytecode.isLoadable(node) && Bytecode.getSize(node) == C2), new Matcher(DUP2)), (range -> {
        if (range.start instanceof Variable var) {
            range.end.replace(new Variable(var.opcode, var.index));
        } else if (range.start instanceof Constant constant) {
            range.end.replace(new Constant(constant.cst));
        }
    }));

    private static final Replacer REPLACE_SWAP = new Replacer(Pattern.of(Bytecode::isLoadable, Bytecode::isLoadable, node -> node.opcode == SWAP), range -> {
        Node start = range.start;
        Node next = start.next;
        next.delete();
        start.insertBefore(next);
        range.end.delete();
    });

    private static final Replacer COMPARE = new Replacer(Pattern.of(Bytecode::isNumericalConstant, Bytecode::isNumericalConstant, Bytecode::isCompare), range -> {
        range.replace(new Constant(Bytecode.cmp(range.end.opcode, ((Constant) range.start).cst, ((Constant) range.start.next).cst)));
    });

    private static final Replacer JUMP_COMPARE = new Replacer(Pattern.of(Bytecode::isNumericalConstant, Bytecode::isNumericalConstant, Bytecode::isJumpCompare), range -> {
        boolean cmp = Bytecode.jump_cmp(range.end.opcode, ((Constant) range.start).cst, ((Constant) range.start.next).cst);
        if (cmp) range.replace(new Jump(GOTO, ((Jump) range.end).label));
        else range.clear();
    });

    private static final Replacer EQUAL = new Replacer(Pattern.of(Bytecode::isNumericalConstant, Bytecode::isEqual), range ->  {
        boolean equ = Bytecode.equ(range.end.opcode, ((Constant) range.start).cst);
        if (equ) range.replace(new Jump(GOTO, ((Jump) range.end).label));
        else range.clear();
    });

    private static final Replacer[] STACK_OPTIMIZERS = {
            X1_SWAP,
            X1_DUP,
            X1_POP2,
            DUP_POP2,
            DUP_SWAP,
            POP_POP,
            VAR_POP,
            VAR_POP2,
            VAR_DUP,
            VAR_DUP2,
            REPLACE_SWAP
    };

    private static final Replacer LOOKUP = new Replacer(Pattern.of(Bytecode::isNumericalConstant, node -> node instanceof Lookup), range -> {
        Constant constant = (Constant) range.start;
        Lookup lookup = (Lookup) constant.next;
        int index = Arrays.indexOf(lookup.keys, (int) constant.cst);

        if (index != -1) range.replace(lookup.labels[index].jump(GOTO));
        else range.replace(lookup._default.jump(GOTO));
    });

    private static final Replacer TABLE = new Replacer(Pattern.of(Bytecode::isNumericalConstant, node -> node instanceof Table), range -> {
        Constant constant = (Constant) range.start;
        Table table = (Table) constant.next;
        int index = ((int) constant.cst) - table.min;
        if (index >= 0 && index < table.labels.length) range.replace(table.labels[index].jump(GOTO));
        else range.replace(table._default.jump(GOTO));
    });

    private static final Replacer FALL_LOOKUP = new Replacer(Pattern.of(node -> node instanceof Lookup lookup && lookup.labels.length == 0), range -> {
        Lookup lookup = (Lookup) range.start;
        lookup.insertBefore(new Instruction(POP));
        lookup.replace(lookup._default.jump(GOTO));
    });

    private static final Replacer FALL_TABLE = new Replacer(Pattern.of(node -> node instanceof Table table && table.labels.length == 0), range -> {
        Table table = (Table) range.start;
        table.insertBefore(new Instruction(POP));
        table.replace(table._default.jump(GOTO));
    });

    private static final Replacer[] SWITCHES =
            {
                    LOOKUP,
                    TABLE,
                    FALL_LOOKUP,
                    FALL_TABLE
            };

    private static final Pattern[] JUNK_STACK =
            {
                    Pattern.of(DUP, POP),
                    Pattern.of(DUP2, POP2),
                    Pattern.of(SWAP, SWAP)
            };

    private static final Pattern ARITHMETIC_PATTERN = Pattern.of(
            new Matcher(Bytecode::isNumericalConstant, "C1"),
            new Matcher(Bytecode::isNumericalConstant, "C2"),
            new Matcher(Bytecode::isArithmetic, "OP")
    );

    private static final Pattern CONVERSION_PATTERN = Pattern.of(
            Bytecode::isNumericalConstant,
            Bytecode::isConversion
    );

    private static final Pattern NEGATE_PATTERN = Pattern.of(
            Bytecode::isNumericalConstant,
            Bytecode::isNegate
    );

    private static final Replacer INLINE = new Replacer(Pattern.of(
            Bytecode::isConstant,
            node -> Bytecode.isStore(node) && inlineCandidate(node.parent.getMethod(), ((Variable) node).index)
    ), range -> {
        Node start = range.start;
        Instructions list = start.parent;
        Object val = ((Constant) start).cst;
        int index = ((Variable) range.end).index;

        list.forEach(node -> node instanceof Variable variable && variable.index == index && Bytecode.isLoad(variable), node -> {
            node.replace(new Constant(val));
        });

        range.clear();
    });

    public static int fold(final Method method) {
        int total = 0;

        while (true) {
            int changes = 0;

            changes += conversion(method);
            changes += arithmetic(method);
            changes += stack(method);
            changes += negate(method);
            changes += COMPARE.execute(method);
            changes += JUMP_COMPARE.execute(method);
            changes += EQUAL.execute(method);
            changes += INLINE.execute(method);

            for (Replacer replacer : SWITCHES) {
                changes += replacer.execute(method);
            }

            // inline

            DeadcodeProcessor.process(method);
            LabelCompressor.compress(method);

            if (changes == 0) break;
            total += changes;
        }

        return total;
    }

    private static int conversion(Method method) {
        int changes = 0;

        for (Pattern.Range range : CONVERSION_PATTERN.match_all(method)) {
            Constant constant = (Constant) range.start;
            Object val = constant.cst;
            range.replace(new Constant(Bytecode.cast(range.end.opcode, val)));
            changes++;
        }

        return changes;
    }

    private static int arithmetic(Method method) {
        int changes = 0;

        for (Pattern.Range match : ARITHMETIC_PATTERN.match_all(method)) {
            Constant c1 = (Constant) match.get("C1");
            Constant c2 = (Constant) match.get("C2");
            Instruction operation = (Instruction) match.get("OP");
            Object result = Bytecode.evaluate(operation.opcode, Bytecode.unbox(c1.cst), Bytecode.unbox(c2.cst));
            match.replace(new Constant(result));
        }

        return changes;
    }

    private static int stack(Method method) {
        int changes = 0;

        for (Pattern pattern : JUNK_STACK) {
            ImmutableSet<Pattern.Range> matches = pattern.match_all(method);
            matches.forEach(Pattern.Range::clear);
            changes += matches.size();
        }

        for (Replacer replacer : STACK_OPTIMIZERS) {
            changes += replacer.execute(method);
        }

        return changes;
    }

    private static int negate(Method method) {
        int changes = 0;

        for (Pattern.Range range : NEGATE_PATTERN.match_all(method)) {
            Object val = ((Constant) range.start).cst;
            range.replace(new Constant(Bytecode.negate(range.end.opcode, val)));
            changes++;
        }

        return changes;
    }

    private static boolean inlineCandidate(Method method, int index) {
        return method.instructions.count(node -> node instanceof Variable variable && variable.index == index && Bytecode.isStore(variable)) == 1 && method.instructions.count(node -> node instanceof Increment incr && incr.local.index == index) == 0;
    }
}