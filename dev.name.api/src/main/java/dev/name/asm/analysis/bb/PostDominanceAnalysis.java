package dev.name.asm.analysis.bb;

import dev.name.util.collections.set.FastHashSet;
import lombok.Getter;
import java.util.*;

@Getter
public final class PostDominanceAnalysis {
    private final BasicBlock root;
    private final FastHashSet<BasicBlock> blocks;
    private boolean analyzed;

    public PostDominanceAnalysis(ControlFlowGraph cfg) {
        Context context = Objects.requireNonNull(Objects.requireNonNull(cfg).getContext());
        this.root = Objects.requireNonNull(context.getEntryBlock());
        this.blocks = Objects.requireNonNull(context.getBlocks());
    }

    public void analyze() {
        if (analyzed) throw new IllegalStateException("Already analyzed");
        if (blocks.isEmpty()) return;

        analyzed = true;
    }
}