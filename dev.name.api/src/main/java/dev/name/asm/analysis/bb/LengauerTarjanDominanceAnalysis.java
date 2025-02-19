package dev.name.asm.analysis.bb;

import dev.name.util.collections.list.FastArrayList;
import dev.name.util.collections.set.FastHashSet;
import lombok.Getter;
import java.util.*;

@Getter
public final class LengauerTarjanDominanceAnalysis {
    private final BasicBlock root;
    private final FastHashSet<BasicBlock> blocks;
    private final BasicBlock[] vertex;
    private boolean analyzed;
    private int n;

    public LengauerTarjanDominanceAnalysis(ControlFlowGraph cfg) {
        Context context = Objects.requireNonNull(Objects.requireNonNull(cfg).getContext());
        this.root = Objects.requireNonNull(context.getEntryBlock());
        this.blocks = Objects.requireNonNull(context.getBlocks());
        this.vertex = new BasicBlock[blocks.size() + 1];
    }

    public void analyze() {
        if (analyzed) throw new IllegalStateException("Already analyzed");
        if (blocks.isEmpty()) return;

        try {
            for (BasicBlock w : blocks) {
                w.bucket = new FastArrayList<>();
                w.semi = 0;
            }

            n = 0;
            dfs(root);

            for (int i = n; i >= 2; i--) {
                BasicBlock w = vertex[i];

                for (BasicBlock v : w.getPredecessors()) {
                    BasicBlock u = eval(v);
                    if (u.semi < w.semi) w.semi = u.semi;
                }

                vertex[w.semi].bucket.add(w);
                link(w.parent, w);

                for (BasicBlock v : w.parent.bucket) {
                    BasicBlock u = eval(v);
                    v.setImmediateDominator(u.semi < v.semi ? u : w.parent);
                }

                w.parent.bucket.clear();
            }

            for (int i = 2; i < n; i++) {
                BasicBlock w = vertex[i];
                BasicBlock idom = w.getImmediateDominator();
                if (idom != vertex[w.semi]) w.setImmediateDominator(idom.getImmediateDominator());
            }

            root.setImmediateDominator(root);
            populate();
            frontiers();
        } finally {
            clean();
            sanity();
        }

        analyzed = true;
    }

    private void sanity() {
        if (!Sanity.SANITY) return;

        for (BasicBlock block : blocks) {
            assert block != null;
            assert block.getImmediateDominator() != null;
            assert root.dominates(block);
            assert block.getDominators().contains(root);

            BasicBlock curr = block;
            assert curr.dominates(curr);

            while (curr != root) {
                BasicBlock imm = curr.getImmediateDominator();
                assert imm != null;
                assert imm.dominates(curr);
                assert imm.dominates(block);
                curr = imm;
            }

            for (BasicBlock frontier : block.getDominanceFrontier()) {
                assert frontier != null;
                assert blocks.contains(frontier);
                assert frontier.getPredecessors().contains(block) || frontier.getPredecessors().stream().anyMatch(block::dominates);
            }

            for (BasicBlock dom : block.getDominators()) {
                assert dom != null;
                assert dom.dominates(block);
            }
        }
    }

    private void populate() {
        root.setImmediateDominator(root);
        root.addDominator(root);

        FastArrayList<BasicBlock> sorted = new FastArrayList<>();
        sorted.sort(Comparator.comparingInt(BasicBlock::getSemi));

        for (BasicBlock block : blocks) {
            if (block == root) continue;

            BasicBlock current = block;

            while (current != root) {
                block.addDominator(current);
                current = current.getImmediateDominator();
            }

            block.addDominator(root);
        }
    }


    private void frontiers() {
        for (BasicBlock b : blocks) {
            FastHashSet<BasicBlock> pred = b.getPredecessors();
            if (pred.size() < 2) continue;

            BasicBlock idom = b.getImmediateDominator();

            for (BasicBlock p : pred) {
                BasicBlock runner = p;

                while (runner != idom && runner != root) {
                    runner.getDominanceFrontier().add(b);
                    runner = runner.getImmediateDominator();
                }
            }
        }
    }

    private void clean() {
        for (BasicBlock block : blocks) {
            block.bucket = null;
            block.child = null;
            block.lbl = null;
            block.ancestor = null;
            block.parent = null;
            block.size = 0;
            block.semi = 0;
        }
    }

    private void dfs(BasicBlock v) {
        v.semi = ++n;
        vertex[n] = v.lbl = v;
        v.ancestor = root;
        v.child = root;
        v.size = 1;

        for (BasicBlock w : v.getSuccessors()) {
            if (w.semi == 0) {
                w.parent = v;
                dfs(w);
            }
        }
    }

    private void compress(BasicBlock v) {
        if (v.ancestor.ancestor == root) return;
        compress(v.ancestor);
        if (v.ancestor.lbl.semi < v.lbl.semi) v.lbl = v.ancestor.lbl;
        v.ancestor = v.ancestor.ancestor;
    }

    private BasicBlock eval(BasicBlock v) {
        if (v.ancestor == root) return v.lbl;
        else {
            compress(v);
            return v.ancestor.lbl.semi >= v.lbl.semi ? v.lbl : v.ancestor.lbl;
        }
    }

    private void link(BasicBlock v, BasicBlock w) {
        BasicBlock s = w;

        while (w.lbl.semi < s.child.lbl.semi) {
            if ((s.size + s.child.child.size) >= (2 * s.child.size)) {
                s.child.ancestor = s;
                s.child = s.child.child;
            } else {
                s.child.size = s.size;
                s = s.ancestor = s.child;
            }
        }

        s.lbl = w.lbl;
        v.size += w.size;

        if (v.size < (2 * w.size)) {
            BasicBlock t = s;
            s = v.child;
            v.child = t;
        }

        while (s != root) {
            s.ancestor = v;
            s = s.child;
        }
    }
}