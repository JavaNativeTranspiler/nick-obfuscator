package dev.name.asm.ir.pattern;

import dev.name.asm.ir.types.Node;

import java.util.function.Predicate;

public record Matcher(int opcode, Predicate<Node> predicate, String tag) {
    public Matcher(final int opcode) {
        this(opcode, o -> true, null);
    }

    public Matcher(final int opcode, final String tag) {
        this(opcode, o -> true, tag);
    }

    public Matcher(final Predicate<Node> predicate) {
        this(Pattern.WILDCARD, predicate, null);
    }

    public Matcher(final Predicate<Node> predicate, final String tag) {
        this(Pattern.WILDCARD, predicate, tag);
    }

    public Matcher(final int opcode, final Predicate<Node> predicate) {
        this(opcode, predicate, null);
    }
}
