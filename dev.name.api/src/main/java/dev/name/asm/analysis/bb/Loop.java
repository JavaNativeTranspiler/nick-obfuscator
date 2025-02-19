package dev.name.asm.analysis.bb;

import dev.name.util.collections.set.FastHashSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@SuppressWarnings("unused")
public final class Loop {
    private final BasicBlock header;
    private final FastHashSet<BasicBlock> body;
    private final FastHashSet<BasicBlock> exits;
    private final FastHashSet<Loop> children;
    private Loop parent;
    @Setter private boolean irreducible;
    private int depth;

    public Loop(BasicBlock header) {
        this.header = header;
        this.body = new FastHashSet<>();
        this.exits = new FastHashSet<>();
        this.children = new FastHashSet<>();
        this.body.add(header);
        this.depth = 1;
    }

    public void addChild(Loop child) {
        if (children.add(child) && child.parent == null) {
            child.parent = this;
            child.depth = this.depth + 1;
        }
    }
}