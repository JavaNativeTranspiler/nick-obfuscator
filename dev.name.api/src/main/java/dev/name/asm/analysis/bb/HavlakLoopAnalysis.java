package dev.name.asm.analysis.bb;

import dev.name.util.collections.list.FastArrayList;
import dev.name.util.collections.set.FastHashSet;
import java.util.*;

public final class HavlakLoopAnalysis {
    private final FastHashSet<BasicBlock> blocks;
    private final FastHashSet<Loop> loops;
    private boolean analyzed;

    public HavlakLoopAnalysis(ControlFlowGraph cfg) {
        Context context = Objects.requireNonNull(Objects.requireNonNull(cfg).getContext());
        this.blocks = Objects.requireNonNull(context.getBlocks());
        this.loops = context.getLoops();
    }

    public void analyze() {
        if (analyzed) throw new IllegalStateException("Already analyzed");
        if (blocks.isEmpty()) return;

        for (BasicBlock header : blocks) {
            FastHashSet<BasicBlock> edges = new FastHashSet<>();

            for (BasicBlock pred : header.getPredecessors()) {
                if (!header.dominates(pred)) continue;
                edges.add(pred);
            }

            if (!edges.isEmpty()) {
                FastHashSet<BasicBlock> body = new FastHashSet<>();

                for (BasicBlock edge : edges) {
                    nodes(edge, header, body);
                }

                body.add(header);

                Loop loop = new Loop(header);
                loop.getBody().addAll(body);
                loop.setIrreducible(irreducible(loop));
                exits(loop);

                header.getLoops().add(loop);
                loops.add(loop);
            }
        }

        forest();
        //

        for (Loop loop : loops) {
            for (BasicBlock body : loop.getBody()) {
                body.getLoops().add(loop);
            }
        }

        for (BasicBlock block : blocks) {
            FastArrayList<Loop> loops = new FastArrayList<>();
            loops.addAll(block.getLoops());
            if (loops.isEmpty()) continue;

            if (loops.size() == 1) {
                block.setLoopHeader(loops.get(0));
                continue;
            }

            loops.sort(Comparator.comparingInt(l -> l.getBody().size()));

            block.setLoopHeader(loops.get(0));
            block.setLoopNestingDepth(loops.size());
        }

        //
        analyzed = true;
    }

    private void nodes(BasicBlock node, BasicBlock header, FastHashSet<BasicBlock> body) {
        if (!body.contains(node)) {
            body.add(node);
            if (node == header) return;

            for (BasicBlock pred : node.getPredecessors()) {
                nodes(pred, header, body);
            }
        }
    }

    private boolean irreducible(Loop loop) {
        BasicBlock header = loop.getHeader();
        FastHashSet<BasicBlock> body = loop.getBody();
        FastHashSet<BasicBlock> visited = new FastHashSet<>();

        for (BasicBlock block : body) {
            if (block == header) continue;

            for (BasicBlock pred : block.getPredecessors()) {
                if (body.contains(pred) || visited.contains(block)) continue;
                visited.add(block);
                return true;
            }
        }

        return false;
    }

    private void exits(Loop loop) {
        FastHashSet<BasicBlock> body = loop.getBody();
        FastHashSet<BasicBlock> exits = loop.getExits();

        for (BasicBlock block : body) {
            for (BasicBlock successor : block.getSuccessors()) {
                if (body.contains(successor)) continue;
                exits.add(successor);
            }
        }
    }

    private void forest() {
        FastArrayList<Loop> sorted = new FastArrayList<>();
        sorted.addAll(loops);
        sorted.sort((l1, l2) -> {
            int cmp = Integer.compare(l2.getBody().size(), l1.getBody().size());
            if (cmp != 0) return cmp;
            return Long.compare(l1.getHeader().getLabel(), l2.getHeader().getLabel());
        });

        for (int i = 0; i < sorted.size(); i++) {
            Loop outer = sorted.get(i);
            FastHashSet<BasicBlock> body = outer.getBody();

            for (int j = i + 1; j < sorted.size(); j++) {
                Loop inner = sorted.get(j);
                if (body.contains(inner.getHeader()) && body.containsAll(inner.getBody())) outer.addChild(inner);
            }
        }
    }
}