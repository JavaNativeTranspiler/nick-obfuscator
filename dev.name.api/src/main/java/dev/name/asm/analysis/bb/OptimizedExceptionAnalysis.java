package dev.name.asm.analysis.bb;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;
import dev.name.util.collections.list.FastArrayList;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.java.Jar;

import java.util.Objects;
import java.util.Stack;

@SuppressWarnings("unused")
public final class OptimizedExceptionAnalysis {
    private final Jar jar;
    private final Context context;
    private final Method method;
    private final FastHashSet<BasicBlock> blocks;
    private boolean analyzed;

    private static final class ExceptionRange {
        public final String type;
        public final int start, end, handler;
        public final BasicBlock.ExceptionHandler eh;
        public final Block block;

        public ExceptionRange(Context context, Block block) {
            this.type = block.type;
            this.start = block.start.index();
            this.end = block.end.index();
            this.handler = block.handler.index();
            this.block = block;

            BasicBlock start = context.getBlockForNode(block.start);
            BasicBlock end = context.getBlockForNode(block.end);
            BasicBlock handler = context.getBlockForNode(block.handler);
            context.markErrorBlock(handler);

            boolean self = contains(handler);
            this.eh = handler.getExceptionBlock();
            BasicBlock.ExceptionHandler.ExceptionBlock eb = new BasicBlock.ExceptionHandler.ExceptionBlock(start, end, block, self);
            this.eh.getSources().add(eb);
            this.eh.getTypes().add(this.type);

            if (self) {
                assert type == null;
                context.getSelfProtectingHandlers().add(eb);
            }
        }

        public boolean contains(BasicBlock block) {
            return contains(block.getStart());
        }

        public boolean contains(Node node) {
            int index = node.index();
            return index >= start && index < end;
        }

        @Override
        public String toString() {
            return String.format("{%d, %d, %d, %s}", start, end, handler, type);
        }
    }

    public OptimizedExceptionAnalysis(ControlFlowGraph cfg) {
        this.jar = Objects.requireNonNull(Objects.requireNonNull(cfg).getJar());
        this.context = Objects.requireNonNull(cfg.getContext());
        this.method = Objects.requireNonNull(this.context.getMethod());
        this.blocks = Objects.requireNonNull(this.context.getBlocks());
    }

    public void analyze() {
        if (analyzed) throw new IllegalStateException("Already analyzed");
        if (blocks.isEmpty()) return;
        if (method.blocks.isEmpty()) return;

        try {
            context.setHasExceptionHandlers(true);

            FastHashSet<ExceptionRange> exceptions = new FastHashSet<>();

            for (Block block : method.blocks) {
                exceptions.add(new ExceptionRange(context, block));
            }

            for (BasicBlock block : blocks) {
                Stack<ExceptionRange> applicable = new Stack<>();

                for (ExceptionRange range : exceptions) {
                    // sanity -> check if it contains both start & end.
                    if (!range.contains(block)) continue;
                    applicable.add(range);
                }

                // jvm searches based on exception table | can impl analysis on this maybe
                // this won't work since it reverses it < --- > swap args
                // applicable.sort((r1, r2) -> Integer.compare(method.blocks.indexOf(r1.block), method.blocks.indexOf(r2.block)));
                applicable.sort((r1, r2) -> Integer.compare(method.blocks.indexOf(r2.block), method.blocks.indexOf(r1.block)));

                FastArrayList<String> visited = new FastArrayList<>();

                while (!applicable.empty()) {
                    ExceptionRange range = applicable.pop();
                    BasicBlock.ExceptionHandler eh = range.eh;
                    BasicBlock handler = eh.getHandler();

                    if (range.type == null) {
                        eh.getApplicable().add(block);
                        block.getExceptionHandlers().add(eh);

                        // prevent invalid loops on self-protecting handlers (finally blocks)
                        if (block != handler) block.addSuccessor(handler);
                        break;
                    }

                    String type = range.type;
                    if (visited.contains(type)) continue;

                    boolean pass = true;

                    for (String str : visited) {
                        String common = jar.getCommonSuperClass(type, str);

                        // subtype of an already activated exception block
                        if (isSubtype(str, type) && !common.equals(type)) {
                            pass = false;
                            break;
                        }
                    }

                    if (!pass) continue;

                    eh.getApplicable().add(block);
                    block.getExceptionHandlers().add(eh);

                    // prevent invalid loops on self-protecting handlers (finally blocks)
                    if (block != handler) block.addSuccessor(handler);
                    visited.add(type);
                }
            }
        } finally {
            sanity();
        }

        analyzed = true;
    }

    private void sanity() {
        for (BasicBlock block : blocks) {
            assert block != null;

            if (block.isExit()) {
                for (BasicBlock successor : block.getSuccessors()) {
                    assert successor != null;
                    assert successor.isError();
                }
            }

            if (block.isError()) {
                BasicBlock.ExceptionHandler eh = block.getExceptionBlock();
                assert eh != null;
                FastHashSet<BasicBlock> applicable = eh.getApplicable();
                assert applicable != null && !applicable.isEmpty();
                FastHashSet<String> types = eh.getTypes();
                assert types != null && !types.isEmpty();
                assert eh.getHandler() != null;

                if (block.isSelfHandling()) {
                    assert eh.getTypes().contains(null);
                    Node n = block.getStart();
                    assert n != null;
                    int idx = n.index();
                    assert eh.getSources().stream().anyMatch(eb -> {
                        assert eb != null;
                        BasicBlock s = eb.start(), e = eb.end();
                        assert s != null && e != null;
                        Node sn = s.getStart(), en = e.getStart();
                        assert sn != null && en != null;
                        int sni = sn.index(), eni = en.index();
                        return idx >= sni && idx < eni;
                    });
                }

                for (BasicBlock.ExceptionHandler.ExceptionBlock src : eh.getSources()) {
                    assert src.block() != null;
                    assert types.contains(src.block().type);
                    assert src.start() != null;
                    assert src.end() != null;
                }
            }
        }
    }

    /**
     * @param type The type we are checking to be a subtype of {@code sub}
     * @param sub  The type we are checking to be a supertype of {@code type}
     * @return whether {@code type} is a subtype of {@code sub}.
     */
    private boolean isSubtype(String type, String sub) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(sub);

        if (type.equals(sub)) return true;

        String common = jar.getCommonSuperClass(type, sub);
        return common.equals(sub);
    }

    private static boolean contains(ExceptionRange container, ExceptionRange contained) {
        return container.start <= contained.start && container.end >= contained.end;
    }
}