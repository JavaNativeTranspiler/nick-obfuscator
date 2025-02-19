package dev.name.asm.ir.pattern;

import com.google.common.collect.ImmutableSet;
import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.types.Node;
import dev.name.util.java.ClassPool;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("all")
public class Pattern {
    public static int WILDCARD = 256;
    private final Matcher[] matchers;
    private final String[] tags;

    public class Range {
        @Getter public final Node start, end;
        private final int startIndex, endIndex;
        @Getter private final Node[] cache;
        private final String[] tags;

        private Range(final Node start, final Node end, final String[] tags, final int startIndex, final int endIndex) {
            this.start = start;
            this.end = end;
            this.tags = tags;
            this.startIndex = startIndex;
            this.endIndex = endIndex;

            final Node[] instructions = new Node[endIndex - startIndex];
            Node curr = start;
            int index = 0;

            while (curr != end) {
                instructions[index++] = curr;
                curr = curr.next;
            }

            instructions[index] = end;

            this.cache = instructions;
        }

        public Node get(final int index) {
            return cache[index];
        }

        public Node get(final String tag) {
            for (int i = 0; i < tags.length; i++)
                if (tag.equals(tags[i]))
                    return cache[i];

            return null;
        }

        public void clear() {
            for (Node node : cache) node.delete();
        }

        public void replace(final Instructions instructions) {
            start.insertBefore(instructions);
            clear();
        }

        public void replace(final Node... replacement) {
            replace(new Instructions(replacement));
        }
    }

    public Pattern(final Matcher... matchers) {
        if (matchers == null) throw new NullPointerException("null pattern");
        this.matchers = matchers;

        final String[] tags = new String[matchers.length];

        for (int i = 0; i < matchers.length; i++)
            tags[i] = matchers[i].tag();

        this.tags = tags;
    }

    public static Pattern of(final Matcher... matchers) {
        return new Pattern(matchers);
    }

    public static Pattern of(final int... opcodes) {
        final List<Matcher> matchers = new ArrayList<>();
        for (final int opcode : opcodes) matchers.add(new Matcher(opcode));
        return of(matchers.toArray(new Matcher[0]));
    }

    public static Pattern of(final Predicate<Node>... matches) {
        final List<Matcher> matchers = new ArrayList<>();
        for (final Predicate<Node> match : matches) matchers.add(new Matcher(match));
        return of(matchers.toArray(new Matcher[0]));
    }

    public Range match(final Instructions minstructions, final int offset) {
        final Node[] instructions = minstructions.toArray();
        final int pattern_size = matchers.length;
        if (pattern_size == 0 || pattern_size > instructions.length) return null;
        for (int i = offset; i <= instructions.length - pattern_size; i++) {
            if (!verify(instructions[i], matchers[0])) continue;
            boolean match = true;

            for (int j = 1; j < pattern_size; j++)
                if (!verify(instructions[i + j], matchers[j])) {
                    match = false;
                    break;
                }

            if (match) return new Range(instructions[i], instructions[i + pattern_size - 1], this.tags, i, i + pattern_size);
        }

        return null;
    }

    public ImmutableSet<Range> match_all(final Instructions instructions) {
        final int pattern_size = matchers.length;
        if (pattern_size == 0 || pattern_size > instructions.size()) return ImmutableSet.of();
        final ImmutableSet.Builder<Range> builder = ImmutableSet.builder();
        int offset = 0;

        Range current;

        while ((current = match(instructions, offset)) != null) {
            builder.add(current);
            offset = current.endIndex;
        }

        return builder.build();
    }

    public ImmutableSet<Range> match_all(final Method method) {
        return match_all(method.instructions);
    }

    public ImmutableSet<Range> match_all(final Class klass) {
        final ImmutableSet.Builder<Range> builder = ImmutableSet.builder();

        for (final Method method : klass.methods)
            builder.addAll(match_all(method));

        return builder.build();
    }

    public ImmutableSet<Range> match_all(final ClassPool pool) {
        final ImmutableSet.Builder<Range> builder = ImmutableSet.builder();

        for (final Class klass : pool) {
            final ImmutableSet<Range> ranges = match_all(klass);
            builder.addAll(ranges);
        }

        return builder.build();
    }

    private boolean verify(final Node node, final Matcher matcher) {
        return node.opcode == WILDCARD || (node.opcode == matcher.opcode() || matcher.opcode() == WILDCARD) && matcher.predicate().test(node);
    }
}