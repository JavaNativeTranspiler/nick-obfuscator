package dev.name.asm.analysis.bb;

import dev.name.asm.ir.nodes.Variable;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;
import dev.name.util.collections.list.FastArrayList;
import dev.name.util.collections.set.FastHashSet;
import dev.name.util.collections.set.LinkedFastHashSet;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

@Getter
@Setter
@SuppressWarnings({"FieldCanBeLocal", "SpellCheckingInspection", "unused"})
public final class BasicBlock implements Iterable<Node> {
    /**
     * <p>
     * Represents an exception handler in a program's control flow graph. <br>
     * This class contains information about the ranges of code covered by the handler, the types of exceptions it handles,
     * and the mapping of exception blocks (start and end of try blocks) to this handler.
     * </p>
     */
    @Getter
    public static final class ExceptionHandler {
        /**
         * Represents a mapping of the start and end of a try-catch block.
         * <p>
         * Each {@code ExceptionBlock} corresponds to a try block and its associated handler label.
         * </p>
         */
        public record ExceptionBlock(BasicBlock start, BasicBlock end, Block block, boolean selfHandling) {
            /**
             * Constructs an {@code ExceptionBlock}.
             *
             * @param start the starting {@link BasicBlock} of the try block (non-null)
             * @param end   the ending {@link BasicBlock} of the try block (non-null)
             * @param block the corresponding {@link Block} of the try block (non-null)
             * @param selfHandling whether this {@link Block} is self-handling.
             * @throws NullPointerException if either {@code start}, {@code end} or {@code block} is null
             */
            public ExceptionBlock {
                Objects.requireNonNull(start, "Start block cannot be null");
                Objects.requireNonNull(end, "End block cannot be null");
                Objects.requireNonNull(block, "Block cannot be null");
            }
        }

        /**
         * All {@link BasicBlock}s within the range covered by this exception handler.
         * <p>
         * The {@code applicable} set includes every basic block that falls within the range of code where this exception
         * handler is active and capable of catching exceptions.
         * </p>
         */
        private final FastHashSet<BasicBlock> applicable;

        /**
         * All {@link ExceptionBlock}s that map to this exception handler.
         * <p>
         * Each {@code ExceptionBlock} represents a pair of start and end basic blocks (try block boundaries)
         * that utilize this handler as their exception target.
         * </p>
         */
        private final FastHashSet<ExceptionBlock> sources;

        /**
         * The set of exception types this handler is capable of catching.
         * <p>
         * Each entry in this set is a fully qualified class name (as a {@code String}) representing
         * an exception type that this handler is able to process when thrown.
         * </p>
         */
        private final FastHashSet<String> types;

        /**
         * The {@link BasicBlock} that is executed when this exception handler is triggered.
         * <p>
         * This block is jumped to whenever an exception occurs within the {@code applicable} range and
         * matches one of the types in {@code types}.
         * </p>
         */
        private final BasicBlock handler;

        /**
         * Constructs an {@code ExceptionHandler} with the specified handler block.
         *
         * @param handler the {@link BasicBlock} that serves as the target for this exception handler (non-null)
         * @throws NullPointerException if {@code handler} is null
         */
        public ExceptionHandler(BasicBlock handler) {
            this.applicable = new FastHashSet<>();
            this.sources = new FastHashSet<>();
            this.types = new FastHashSet<>();
            this.handler = Objects.requireNonNull(handler, "Handler block cannot be null");
        }
    }

    /**
     * The parent {@link ControlFlowGraph} of this basic block.
     */
    private final ControlFlowGraph graph;

    /**
     * The set of {@link Node}s corresponding to this basic block.
     */
    private final LinkedFastHashSet<Node> instructions;

    /**
     * The first node of this basic block.
     * This is always an instance of {@link dev.name.asm.ir.nodes.Label}
     */
    private Node start;

    /**
     * The last node of this basic block.
     * This node will either terminate the flow, method, or branch to another basic block.
     * It will be one of the following:
     * <ul>
     *   <li>An instance of {@link dev.name.asm.ir.nodes.Jump}</li>
     *   <li>An instance of {@link dev.name.asm.ir.nodes.Lookup}</li>
     *   <li>An instance of {@link dev.name.asm.ir.nodes.Table}</li>
     *   <li>A node with an opcode of {@code IRETURN}, {@code ARETURN}, {@code FRETURN}, {@code DRETURN}, or {@code RETURN}</li>
     *   <li>A node with an {@code ATHROW} opcode</li>
     * </ul>
     */
    private Node end;

    /**
     * A set of predecessor blocks in the control flow graph.
     * <p>
     * The {@code predecessors} set contains all {@link BasicBlock} instances that have a direct control flow edge
     * leading to this block. Predecessors represent the immediate predecessors in the flow of execution, making this
     * set essential for backward analyses such as liveness analysis, data flow propagation, or dominance computation.
     * </p>
     * <p>
     * The elements in this set are automatically updated when control flow edges are added or removed in the graph.
     * This ensures consistency between the graph structure and its associated metadata. Any modifications to the
     * control flow graph should be followed by updates to maintain this relationship.
     * </p>
     */
    private final FastHashSet<BasicBlock> predecessors;

    /**
     * A set of successor blocks in the control flow graph.
     * <p>
     * The {@code successors} set contains all {@link BasicBlock} instances that this block has a direct control flow
     * edge pointing to. Successors represent the immediate successors in the flow of execution, making this set
     * critical for forward analyses such as reachability, data flow propagation, or identifying loops via back edges.
     * </p>
     * <p>
     * The elements in this set are automatically updated when control flow edges are added or removed in the graph.
     * Consistency between this set and the graph structure must be maintained to ensure the correctness of analysis
     * algorithms.
     * </p>
     */
    private final FastHashSet<BasicBlock> successors;

    /**
     * A set of dominator blocks for this {@code BasicBlock}.
     * <p>
     * The {@code dominators} set contains all {@link BasicBlock} instances that dominate this block in the control
     * flow graph. A block is said to dominate another if every path to the latter passes through the former. This set
     * provides the full dominance information, as opposed to the {@code immediateDominator} field, which only
     * identifies the closest dominator.
     * </p>
     * <p>
     * Dominance information is critical for optimization and transformation tasks, such as constructing dominator
     * trees, identifying loops, and performing dead code elimination. The computation of this set is typically
     * performed as part of a dominance analysis algorithm and remains unchanged unless the control flow graph is
     * modified.
     * </p>
     */
    private final FastHashSet<BasicBlock> dominators;

    /**
     * The set of blocks that this block dominates
     */
    private final FastHashSet<BasicBlock> dominated;

    /**
     * The immediate dominator of this {@code BasicBlock} in the control flow graph.
     * <p>
     * The immediate dominator (commonly abbreviated as "idom") is the unique basic block that:
     * <ul>
     *   <li>Strictly dominates this block, meaning it must be executed on all paths to this block.</li>
     *   <li>Is the closest such block in terms of the control flow graph hierarchy.</li>
     * </ul>
     * The immediate dominator is a fundamental concept in dominance analysis, used to construct the dominator tree
     * of a program's control flow graph. For any block other than the entry block, there is exactly one immediate
     * dominator.
     * </p>
     * <p>
     * If this block is the entry block of the control flow graph, the {@code immediateDominator} will be {@code null}
     * since no other block dominates the entry point.
     * </p>
     * <p>
     * This field is typically computed during dominance analysis and remains immutable for the lifetime of the block
     * unless the graph structure is explicitly modified.
     * </p>
     */
    private BasicBlock immediateDominator;

    /**
     * A set representing the dominance frontier of this {@code BasicBlock}.
     * <p>
     * The {@code dominanceFrontier} set contains all {@link BasicBlock} instances that are not strictly dominated by
     * this block but have at least one predecessor that is. Formally, a block B is in the dominance frontier of a
     * block A if:
     * <ul>
     *   <li>A does not dominate B, and</li>
     *   <li>there exists a predecessor P of B such that A dominates P.</li>
     * </ul>
     * </p>
     * <p>
     * The dominance frontier is a key concept in compiler optimization, particularly in the construction of Static
     * Single Assignment (SSA) form. It identifies the minimal set of blocks where φ-nodes need to be inserted for
     * variables that are redefined in a program.
     * </p>
     * <p>
     * This set is computed during dominance analysis and is expected to remain consistent with the graph structure
     * unless the control flow is altered.
     * </p>
     */
    private final FastHashSet<BasicBlock> dominanceFrontier;

    /**
     * Represents a set of post-dominators for a specific basic block.
     * <p>
     * A post-dominator is a basic block that must be executed after every possible
     * execution path from a given basic block. This is a key concept in control
     * flow analysis, allowing for insights into program structure and flow dependencies.
     * </p>
     */
    //private final FastHashSet<BasicBlock> postDominators;

    /**
     * The set of blocks that this block post-dominates.
     */
    //private final FastHashSet<BasicBlock> postDominated;

    /**
     * The immediate post-dominator of the associated basic block.
     * <p>
     * The immediate post-dominator is the closest post-dominator of a basic block in the
     * control flow graph (CFG). It directly follows the block in the post-dominance tree
     * and provides a foundation for constructing post-dominance relationships.
     * </p>
     */
    private BasicBlock immediatePostDominator;

    /**
     * Represents the post-dominance frontier of a basic block.
     * <p>
     * The post-dominance frontier consists of all basic blocks where control flow
     * reconverges after diverging from the associated block. These blocks are critical
     * for identifying regions of influence in the CFG and are used in various
     * program analysis techniques, such as static single assignment (SSA) form conversion.
     * </p>
     */
    //private final FastHashSet<BasicBlock> postDominanceFrontier;

    /**
     * The set of {@link ExceptionHandler}'s that this basic block falls within the range of.
     */
    private final FastHashSet<ExceptionHandler> exceptionHandlers;

    /**
     * The loop header of the loop this {@code BasicBlock} belongs to, if any.
     * <p>
     * The loop header is defined as the entry block of a natural loop in the control flow graph. A natural loop is
     * identified by the presence of a back edge, which is a control flow edge pointing back to a block that dominates
     * the source of the edge.
     * </p>
     * <p>
     * All basic blocks within a natural loop share the same loop header, providing a consistent reference point for
     * identifying the loop to which the block belongs. This field is used in loop analysis to:
     * <ul>
     *   <li>Identify the structure of loops for optimization purposes, such as loop unrolling or invariant code motion.</li>
     *   <li>Determine the nesting relationships between loops.</li>
     *   <li>Facilitate transformations that depend on precise loop boundaries.</li>
     * </ul>
     * If this block does not belong to any loop, the {@code loopHeader} will be {@code null}.
     * </p>
     * <p>
     * The value of this field is computed as part of a loop detection algorithm, such as finding strongly connected
     * components in the control flow graph, and it is expected to remain unchanged unless the graph structure is
     * modified.
     * </p>
     */
    private Loop loopHeader;

    /**
     * The active loops in this basic block.
     */
    private FastHashSet<Loop> loops;

    /**
     * Whether this basic block is an entrypoint of the method. <br>
     * If {@code true}, this basic block contains the first instruction of the method.
     */
    private boolean isEntry;

    /**
     * Note: An exit can have successors if in an exception block. {TODO optimize exceptions}
     * Whether this basic block is an exit from the method. <br>
     * This is {@code true}
     * if this basic block contains any form of a {@code return} or {@code ATHROW} instruction.
     */
    private boolean isExit;

    /**
     * Whether this basic block is an exception handler block. <br>
     */
    private boolean isError;

    /**
     * Whether this exception is self-handling. <br>
     * An exception is self handling if it falls within it's own range.
     */
    private boolean isSelfHandling;

    /**
     * The {@link ExceptionHandler} for this basic block if this block is an exception handler.
     */
    private ExceptionHandler exceptionBlock;

    /**
     * Represents a unique identifier for the basic block within the program. <br>
     * This identifier is used to uniquely distinguish between blocks and is
     * stored as a `long` to accommodate large datasets or specialized numbering schemes.
     */
    private long label;

    /**
     * The total number of instructions within the basic block. <br>
     * This value reflects the block's complexity and is used for performance
     * analysis and optimization strategies.
     */
    private long totalInstructions;

    /**
     * The depth of the basic block within the control flow graph (CFG). <br>
     * The entry block typically has the lowest depth. <br> This value helps in
     * analyzing the program's structure and optimizing deeply nested constructs.
     */
    private int depth;

    /**
     * The nesting level of the basic block within loops in the program.
     * A depth of 1 represents blocks directly inside a single loop, with higher
     * values for blocks within nested loops. <br> This is critical for loop optimizations
     * such as fusion, tiling, or vectorization.
     */
    private int loopNestingDepth;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Semi-dominator of this basic block. <br>
     * The semi-dominator of a block `x` is the block with the smallest DFS index
     * on any path from the entry block to `x` (excluding `x` itself). <br>
     * This is a crucial component in the computation of dominators using the Lengauer-Tarjan algorithm.
     */
    int semi;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Size of this basic block. <br>
     * This is a crucial component in the computation of dominators using the Lengauer-Tarjan algorithm.
     */
    int size;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Child of this basic block. <br>
     * This is a crucial component in the computation of dominators using the Lengauer-Tarjan algorithm.
     */
    BasicBlock child;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Ancestor of this basic block in the union-find data structure. <br>
     * Used during the optimization of the semi-dominator calculation in the Lengauer-Tarjan algorithm.
     * It represents a pointer to another block in the same equivalence class, forming a union-find tree.
     */
    BasicBlock ancestor;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Label of this basic block in the union-find data structure.
     * Helps in efficiently finding the block with the minimum DFS index in a union-find class.
     * The label points to the block that currently serves as the "representative" with
     * the smallest DFS index within this class.
     */
    BasicBlock lbl;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Parent of this basic block in the DFS spanning tree. <br>
     * Points to the block from which this block was first discovered during the depth-first search.
     * It forms the backbone of the DFS tree structure.
     */
    BasicBlock parent;

    /**
     * Note: This field is only used during dominance analysis and becomes {@code null} after. <br>
     * Bucket for storing blocks that have this block as their semi-dominator.
     * Used during the second phase of the dominator computation.
     * When a block's dominator is finalized, this bucket allows all its "child" blocks
     * (those for which this block is the semi-dominator) to efficiently update their dominator information.
     */
    FastArrayList<BasicBlock> bucket;

    public BasicBlock(ControlFlowGraph graph) {
        this.graph = graph;
        this.instructions = new LinkedFastHashSet<>();

        this.predecessors = new FastHashSet<>();
        this.successors = new FastHashSet<>();

        this.dominators = new FastHashSet<>();
        this.dominated = new FastHashSet<>();
        this.dominanceFrontier = new FastHashSet<>();

       /* this.postDominators = new FastHashSet<>();
        this.postDominated = new FastHashSet<>();
        this.postDominanceFrontier = new FastHashSet<>();*/

        this.exceptionHandlers = new FastHashSet<>();
        this.loops = new FastHashSet<>();
    }

    /**
     * Adds a predecessor {@link BasicBlock} to this basic block.
     * <p>
     * A predecessor is a block that flows directly into the current basic block in the control flow graph.
     * This method adds the given predecessor to the set of predecessors of this basic block and
     * also ensures that this block is added as a successor to the predecessor.
     * </p>
     *
     * @param predecessor the {@link BasicBlock} to add as a predecessor (non-null)
     * @throws NullPointerException if {@code predecessor} is {@code null}
     */
    public void addPredecessor(BasicBlock predecessor) {
        if (!this.predecessors.add(predecessor)) return;
        predecessor.addSuccessor(this);
    }

    /**
     * Removes a predecessor {@link BasicBlock} from this basic block.
     * <p>
     * A predecessor is a block that flows directly into the current basic block in the control flow graph.
     * This method removes the given predecessor from the set of predecessors of this basic block and
     * also ensures that this block is removed from the successors of the predecessor.
     * </p>
     *
     * @param predecessor the {@link BasicBlock} to remove as a predecessor (non-null)
     * @throws NullPointerException if {@code predecessor} is {@code null}
     */
    public void removePredecessor(BasicBlock predecessor) {
        if (!this.predecessors.remove(predecessor)) return;
        predecessor.removeSuccessor(this);
    }

    /**
     * Adds a successor {@link BasicBlock} to this basic block.
     * <p>
     * A successor is a block that is directly reachable from the current basic block in the control flow graph.
     * This method adds the given successor to the set of successors of this basic block and
     * also ensures that this block is added as a predecessor to the successor.
     * </p>
     *
     * @param successor the {@link BasicBlock} to add as a successor (non-null)
     * @throws NullPointerException if {@code successor} is {@code null}
     */
    public void addSuccessor(BasicBlock successor) {
        if (!this.successors.add(successor)) return;
        successor.addPredecessor(this);
    }

    /**
     * Removes a successor {@link BasicBlock} from this basic block.
     * <p>
     * A successor is a block that is directly reachable from the current basic block in the control flow graph.
     * This method removes the given successor from the set of successors of this basic block and
     * also ensures that this block is removed from the predecessors of the successor.
     * </p>
     *
     * @param successor the {@link BasicBlock} to remove as a successor (non-null)
     * @throws NullPointerException if {@code successor} is {@code null}
     */
    public void removeSuccessor(BasicBlock successor) {
        if (!this.successors.remove(successor)) return;
        successor.removePredecessor(this);
    }

    /**
     * @param blocks the {@link BasicBlock}s we are checking if we dominate.
     * @throws NullPointerException if {@code  block} is null.
     * @return {@code true} if this block dominates {@code  block}
     */
    public boolean dominates(FastHashSet<BasicBlock> blocks) {
        for (BasicBlock block : blocks) {
            if (block.getDominators().contains(this)) continue;
            return false;
        }

        return true;
    }

    /**
     * @param blocks the {@link BasicBlock}s we are checking if we dominate.
     * @throws NullPointerException if {@code  block} is null.
     * @return {@code true} if this block dominates {@code  block}
     */
    @SuppressWarnings("all")
    public boolean dominates(BasicBlock... blocks) {
        for (BasicBlock block : blocks) {
            if (dominated.contains(block)) continue;
            return false;
        }

        return true;
    }

    public void addDominator(BasicBlock dom) {
        dominators.add(dom);
        dom.dominated.add(this);
    }

    /*public void addPostDominator(BasicBlock pdom) {
        postDominators.add(pdom);
        pdom.postDominated.add(this);
    }*/

    /**
     * Returns an iterator over the {@link Node} elements contained in this {@code BasicBlock}.
     * <p>
     * The returned iterator allows sequential access to all the instructions within this basic block.
     * Each {@link Node} represents a specific instruction or operation that constitutes the logical
     * flow of this block.
     * </p>
     */
    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return instructions.iterator();
    }
}