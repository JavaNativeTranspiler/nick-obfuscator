package dev.name.asm.analysis.bb;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Bootstrap;
import dev.name.asm.ir.types.Node;
import dev.name.util.collections.set.FastHashSet;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

import static dev.name.asm.analysis.bb.ControlFlowGraph.terminatesMethod;

@RequiredArgsConstructor
public final class Sanity implements Opcodes {
    public static final boolean SANITY = true;

    private final Method method;

    static void sanity(final String message) {
        if (!SANITY) return;
        System.out.printf("[CFG] [SANITY] %s%n%s%n", message, Thread.currentThread().getStackTrace()[2].toString());
    }

    void verify() {
        if (!SANITY) return;

        Instructions instructions = method.instructions;

        if (instructions == null) {
            sanity("no instructions present for method");
            return;
        }

        int size = instructions.size();

        if (size < 0) {
            sanity("negative instruction list size");
            return;
        }

        if (instructions.first == null && size > 0) {
            sanity("unlinked first node in instruction list");
            return;
        }

        if (instructions.last == null && size > 0) {
            sanity("unlinked last node in instruction list");
            return;
        }

        for (int i = 0; i < size; i++) {
            Node node = instructions.get(i);

            if (node == null) {
                sanity("null instruction present");
                continue;
            }

            if (node.index() != i) sanity("instruction index mismatch at");
            saneNode(node);
        }

        if (method.blocks == null) {
            sanity("method does not have any block list present");
            return;
        }

        for (Block block : method.blocks) {
            if (block == null) {
                sanity("null exception block");
                return;
            }

            if (block.start == null || block.end == null || block.handler == null) sanity("invalid exception handler block: missing labels");
            if (block.start.index() >= block.end.index()) sanity("invalid exception handler range: start >= end");
            if (block.handler.index() < 0 || block.handler.index() >= instructions.size()) sanity("handler label points outside method bounds");
        }
    }

    private void saneArgs(Object[] args) {
        if (args == null) {
            sanity("null arguments");
            return;
        }

        for (Object arg : args) {
            if (arg == null) {
                sanity("null argument");
                continue;
            }

            if (arg instanceof Handle handle) saneHandle(handle);
            else if (arg instanceof ConstantDynamic cd) saneConstantDynamic(cd);
        }
    }

    private void saneConstantDynamic(ConstantDynamic cd) {
        if (cd == null) {
            sanity("null constant dynamic");
            return;
        }

        int n_args = cd.getBootstrapMethodArgumentCount();
        if (cd.getName() == null) sanity("invalid constant dynamic name");
        if (cd.getDescriptor() == null) sanity("invalid constant dynamic desc");
        if (n_args < 0) sanity("negative bootstrap argument size");
        if (cd.getSize() != 1 && cd.getSize() != 2) sanity("invalid constant dynamic size");
        saneHandle(cd.getBootstrapMethod());

        if (n_args > 0) {
            Object[] args = new Object[n_args];

            for (int i = 0; i < n_args; i++) {
                args[i] = cd.getBootstrapMethodArgument(i);
            }

            saneArgs(args);
        }
    }

    private void saneTag(int tag, String msg) {
        if (tag < H_GETFIELD || tag > H_INVOKEINTERFACE) sanity(msg);
    }

    private void saneHandle(Handle handle) {
        if (handle == null) {
            sanity("null handle");
            return;
        }

        if (handle.getOwner() == null) sanity("invalid handle owner");
        if (handle.getDesc() == null) sanity("invalid handle descriptor");
        if (handle.getName() == null) sanity("invalid handle name");
        saneTag(handle.getTag(), "invalid handle tag");
    }

    private void saneNode(Node node) {
        if (node == null) {
            sanity("Undetermined node");
            return;
        }

        if (node.method == null) {
            sanity("node has no method parent");
            return;
        }

        if (node.parent == null) {
            sanity("node is not apart of a instruction list");
            return;
        }

        if (node.next == null && (!terminatesMethod(node) && !(node instanceof Lookup) && !(node instanceof Table) && !(node instanceof Jump jump && jump.unconditional()))) {
            sanity("Fall through method termination");
            return;
        }

        int index = node.index();

        if (index < 0) {
            sanity("node has negative index");
            return;
        }

        if (index > node.parent.size()) {
            sanity("node index exceeds parent");
            return;
        }

        if (node.previous == null && index != 0) {
            sanity("unlinked previous node but not first");
            return;
        }

        if (index == 0 && (node.previous != null || (node.next == null && !terminatesMethod(node)))) {
            sanity("badly linked first node");
            return;
        }

        int opcode = node.opcode;

        if (opcode != -1 && opcode < NOP || opcode > IFNONNULL) {
            sanity(String.format("opcode exceeds range: %d - %s | source: %s.%s %s", opcode, node, node.method.klass.name, node.method.name, node.method.desc));
            return;
        }

        switch (node.type()) {
            case Node.METHOD -> {
                Invoke invoke = (Invoke) node;
                if (opcode < INVOKEVIRTUAL || opcode > INVOKEINTERFACE) sanity("invalid opcode for invoke");
                if (invoke.owner == null) sanity("invoke has no owner");
                if (invoke.name == null) sanity("invoke has no name");
                if (invoke.desc == null) sanity("invoke has no descriptor");
            }
            case Node.ARRAY -> {
                Array array = (Array) node;
                if (opcode != MULTIANEWARRAY) sanity("invalid array opcode");
                if (array.dimensions < 1) sanity("array has invalid dimensions (< 1)");
                if (array.desc == null) sanity("invalid array type");
            }
            case Node.PRIM_ARRAY -> {
                Array.Primitive array = (Array.Primitive) node;
                if (opcode != NEWARRAY) sanity("invalid primitive array opcode");
                if (array.type < T_BOOLEAN || array.type > T_LONG) sanity("invalid primitive array type: " + array.type);
            }
            case Node.VARIABLE -> {
                Variable variable = (Variable) node;
                if (!((opcode >= ILOAD && opcode <= ALOAD) || (opcode >= ISTORE && opcode <= ASTORE))) sanity("invalid variable opcode");
                if (variable.index < 0) sanity("negative local index on variable");
            }
            case Node.FIELD -> {
                Accessor accessor = (Accessor) node;
                if (opcode < GETSTATIC || opcode > PUTFIELD) sanity("invalid opcode for accessor");
                if (accessor.owner == null) sanity("accessor has no owner");
                if (accessor.name == null) sanity("accessor has no name");
                if (accessor.desc == null) sanity("accessor has no descriptor");
            }
            case Node.DYNAMIC -> {
                Dynamic dynamic = (Dynamic) node;
                if (opcode != INVOKEDYNAMIC) sanity("invalid invokedynamic opcode");
                if (dynamic.name == null) sanity("null dynamic name");
                if (dynamic.desc == null) sanity("invalid descriptor for dynamic");
                if (dynamic.bootstrap != null) {
                    Bootstrap bootstrap = dynamic.bootstrap;
                    if (bootstrap.owner == null) sanity("invalid bootstrap owner");
                    if (bootstrap.name == null) sanity("invalid bootstrap name");
                    if (bootstrap.desc == null) sanity("invalid bootstrap desc");
                    saneTag(bootstrap.tag, "invalid bootstrap tag");
                } else sanity("null bootstrap method for dynamic");
                saneArgs(dynamic.args);
            }
            case Node.CONSTANT -> {
                Constant constant = (Constant) node;
                if (constant.cst instanceof Handle handle) saneHandle(handle);
                else if (constant.cst instanceof ConstantDynamic cd) saneConstantDynamic(cd);
            }
            case Node.TYPE -> {
                Type type = (Type) node;
                if (opcode != ANEWARRAY && opcode != CHECKCAST && opcode != INSTANCEOF && opcode != NEW) {
                    sanity("invalid opcode for type node");
                }
                if (type.desc == null) sanity("invalid descriptor for type node");
            }
            case Node.INCREMENT -> {
                Increment increment = (Increment) node;
                if (opcode != IINC) sanity("invalid increment opcode");
                if (increment.local == null || increment.local.index < 0) sanity("invalid local for increment");
            }
            case Node.JUMP -> {
                Jump jump = (Jump) node;
                if (opcode < IFEQ || opcode > GOTO && opcode != IFNULL && opcode != IFNONNULL) sanity("invalid jump opcode");
                if (jump.conditional() && jump.next == null) sanity("conditional jump fails to fallthrough");
                if (jump.label == null) sanity("unlinked jump label");
            }
            case Node.LOOKUP -> {
                Lookup lookup = (Lookup) node;
                if (opcode != LOOKUPSWITCH) sanity("invalid opcode for lookupswitch");
                if (lookup._default == null) sanity("unlinked default lookupswitch");

                Label[] labels = lookup.labels;
                int[] keys = lookup.keys;
                if (labels != null) {
                    if (keys != null) {
                        if (labels.length != keys.length) {
                            sanity("lookupswitch keys and label size mismatch");
                        }

                        int[] sorted = keys.clone();
                        Arrays.sort(sorted);

                        if (!Arrays.equals(sorted, keys)) {
                            sanity("lookupswitch keys unsorted");
                        }

                        FastHashSet<Integer> present = new FastHashSet<>();

                        for (int key : keys) {
                            if (present.add(key)) continue;
                            sanity("lookupswitch contains duplicate keys");
                        }
                    } else sanity("no keys present in lookupswitch");

                    for (Label label : labels) {
                        if (label != null) continue;
                        sanity("no label present for case in lookupswitch");
                    }
                } else sanity("no cases present in lookupswitch");
            }
            case Node.TABLE -> {
                Table table = (Table) node;
                if (opcode != TABLESWITCH) sanity("invalid opcode for tableswitch");
                if (table._default == null) sanity("unlinked default tableswitch");

                Label[] labels = table.labels;
                int min = table.min, max = table.max;

                if (min > max) {
                    sanity("tableswitch minimum exceeds maximum");
                }

                if (labels != null) {
                    int required = max - min + 1;

                    if (labels.length != required) {
                        sanity("tableswitch labels size mismatch");
                    }

                    for (Label label : labels) {
                        if (label != null) continue;
                        sanity("no label present for case in lookupswitch");
                    }
                } else sanity("no cases present in tableswitch");
            }
        }
    }
}