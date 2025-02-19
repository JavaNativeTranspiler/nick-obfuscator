package dev.name.util.asm;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.analysis.FixedInterpreter;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Flags;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.util.java.ClassPool;
import dev.name.util.lambda.Condition;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.*;
import java.util.function.Predicate;

import static dev.name.util.asm.Templates.*;

@SuppressWarnings("unused")
public final class Bytecode implements Opcodes {
    private static final Condition<Integer>
        ARITHMETIC = num -> (num >= IADD && num <= DREM) || (num >= ISHL && num <= LXOR),
        NEGATE = num -> num >= INEG && num <= DNEG,
        CONSTANTS = num -> (num == LDC) || (num >= ACONST_NULL && num <= DCONST_1) || num == BIPUSH || num == SIPUSH,
        CONVERSIONS = num -> num >= I2L && num <= I2S,
        LOAD = num -> num >= ILOAD && num <= ALOAD,
        STORE = num -> num >= ISTORE && num <= ASTORE,
        COMPARE = num -> (num >= LCMP && num <= DCMPG),
        JUMP_COMPARE = num -> (num >= IF_ICMPEQ && num <= IF_ICMPLE),
        EQUAL = num -> num >= IFEQ && num <= IFLE || num == IFNULL || num == IFNONNULL,
        INVOKE = num -> num >= INVOKEVIRTUAL && num <= INVOKEDYNAMIC,
        FIELD = num -> num >= GETSTATIC && num <= PUTFIELD,
        RETURN = num -> num >= IRETURN && num <= Opcodes.RETURN,
        STACK = num -> num >= POP && num <= SWAP;

    public static final int C1 = 4;
    public static final int C2 = 8;

    public static boolean isPrimitive(final Type type) {
        return type.getDescriptor().length() == 1;
    }

    public static int getSize(final Object obj) {
        return (obj instanceof Long || obj instanceof Double) ? C2 : C1;
    }

    public static int getSize(final int opcode) {
        return switch (opcode) {
            case LSTORE, DSTORE, LLOAD, DLOAD, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
                 IF_ACMPEQ, IF_ACMPNE -> C2;
            case ISTORE, FSTORE, ASTORE, ILOAD, FLOAD, ALOAD, IFEQ, IFLT, IFNE, IFGE, IFGT, IFLE, IFNULL, IFNONNULL -> C1;
            default -> throw new IllegalStateException("invalid size opcode");
        };
    }

    public static int getSize(final Node node) {
        if (isConstant(node)) return getSize(parseConstant(node));
        else return getSize(node.opcode);
    }

    public static boolean isLoadable(final Node node) {
        return isConstant(node) || isLoad(node);
    }

    public static boolean isArithmetic(final Node node) {
        return ARITHMETIC.test(node.opcode);
    }

    public static boolean isNegate(final Node node) {
        return NEGATE.test(node.opcode);
    }

    public static boolean isConstant(final Node node) {
        return node instanceof Constant || CONSTANTS.test(node.opcode);
    }

    public static boolean isConversion(final Node node) {
        return CONVERSIONS.test(node.opcode);
    }

    public static boolean isLoad(final Node node) {
        return LOAD.test(node.opcode);
    }

    public static boolean isStore(final Node node) {
        return STORE.test(node.opcode);
    }

    public static boolean isCompare(final Node node) {
        return COMPARE.test(node.opcode);
    }

    public static boolean isJumpCompare(final Node node) {
        return JUMP_COMPARE.test(node.opcode);
    }

    public static boolean isEqual(final Node node) {
        return EQUAL.test(node.opcode);
    }

    public static boolean isInvoke(final Node node) {
        return INVOKE.test(node.opcode);
    }

    public static boolean isField(final Node node) {
        return FIELD.test(node.opcode);
    }

    public static boolean isReturn(final Node node) {
        return RETURN.test(node.opcode);
    }

    public static boolean isStack(final Node node) {
        return STACK.test(node.opcode);
    }

    public static boolean isFP(final Object obj) {
        return obj instanceof Float || obj instanceof Double;
    }

    public static Object parseConstant(final Node node) {
        if (!isConstant(node)) throw new IllegalStateException("hi i attempted to parse a nonconstant bye");
        return switch (node.opcode) {
            case ICONST_M1 -> -1;
            case ICONST_0 -> 0;
            case ICONST_1 -> 1;
            case ICONST_2 -> 2;
            case ICONST_3 -> 3;
            case ICONST_4 -> 4;
            case ICONST_5 -> 5;
            case LCONST_0 -> 0L;
            case LCONST_1 -> 1L;
            case FCONST_0 -> 0F;
            case FCONST_1 -> 1F;
            case FCONST_2 -> 2F;
            case DCONST_0 -> 0D;
            case DCONST_1 -> 1D;
            case BIPUSH, SIPUSH, LDC -> ((Constant) node).cst;
            default -> null;
        };
    }

    public static Number unbox(final Object o) {
        if (o instanceof Boolean b)         return b ? 1 : 0;
        else if (o instanceof Character c)  return (int) c;
        else if (o instanceof Byte b)       return (int) b;
        else if (o instanceof Short s)      return (int) s;
        else if (o instanceof Integer i)    return i;
        else if (o instanceof Long l)       return l;
        else if (o instanceof Float f)      return f;
        else if (o instanceof Double d)     return d;
        else throw new IllegalArgumentException("invalid type: " + o.getClass().getName());
    }


    public static Object evaluate(final int opcode, final Number first, final Number second) {
        return switch (opcode) {
            case IADD ->    first.intValue() + second.intValue();
            case ISUB ->    first.intValue() - second.intValue();
            case IMUL ->    first.intValue() * second.intValue();
            case IDIV ->    first.intValue() / second.intValue();
            case IREM ->    first.intValue() % second.intValue();
            case ISHL ->    first.intValue() << second.intValue();
            case ISHR ->    first.intValue() >> second.intValue();
            case IUSHR ->   first.intValue() >>> second.intValue();
            case IAND ->    first.intValue() & second.intValue();
            case IOR ->     first.intValue() | second.intValue();
            case IXOR ->    first.intValue() ^ second.intValue();
            case LADD ->    first.longValue() + second.longValue();
            case LSUB ->    first.longValue() - second.longValue();
            case LMUL ->    first.longValue() * second.longValue();
            case LDIV ->    first.longValue() / second.longValue();
            case LREM ->    first.longValue() % second.longValue();
            case LSHL ->    first.longValue() << second.intValue();
            case LSHR ->    first.longValue() >> second.intValue();
            case LUSHR ->   first.longValue() >>> second.intValue();
            case LAND ->    first.longValue() & second.longValue();
            case LOR ->     first.longValue() | second.longValue();
            case LXOR ->    first.longValue() ^ second.longValue();
            case FADD ->    first.floatValue() + second.floatValue();
            case FSUB ->    first.floatValue() - second.floatValue();
            case FMUL ->    first.floatValue() * second.floatValue();
            case FDIV ->    first.floatValue() / second.floatValue();
            case FREM ->    first.floatValue() % second.floatValue();
            case DADD ->    first.doubleValue() + second.doubleValue();
            case DSUB ->    first.doubleValue() - second.doubleValue();
            case DMUL ->    first.doubleValue() * second.doubleValue();
            case DDIV ->    first.doubleValue() / second.doubleValue();
            case DREM ->    first.doubleValue() % second.doubleValue();
            default -> null;
        };
    }

    public static Object cast(final int opcode, final Object obj) {
        return switch (opcode) {
            case I2L -> (long)       unbox(obj).intValue();
            case I2F -> (float)      unbox(obj).intValue();
            case I2D -> (double)     unbox(obj).intValue();
            case L2I -> (int)        unbox(obj).longValue();
            case L2F -> (float)      unbox(obj).longValue();
            case L2D -> (double)     unbox(obj).longValue();
            case F2I -> (int)        unbox(obj).floatValue();
            case F2L -> (long)       unbox(obj).floatValue();
            case F2D -> (double)     unbox(obj).floatValue();
            case D2I -> (int)        unbox(obj).doubleValue();
            case D2L -> (long)       unbox(obj).doubleValue();
            case D2F -> (float)      unbox(obj).doubleValue();
            case I2B -> (byte)       unbox(obj).intValue();
            case I2C -> (char)       unbox(obj).intValue();
            case I2S -> (short)      unbox(obj).intValue();
            default -> null;
        };
    }


    public static Object negate(final int opcode, final Object obj) {
        return switch (opcode) {
            case INEG -> -((int)    unbox(obj).intValue());
            case FNEG -> -((float)  unbox(obj).floatValue());
            case LNEG -> -((long)   unbox(obj).longValue());
            case DNEG -> -((double) unbox(obj).doubleValue());
            default -> null;
        };
    }

    public static int cmp(final int opcode, final Object cst1, final Object cst2) {
        return switch (opcode) {
            case LCMP -> lcmp(unbox(cst1).longValue(), unbox(cst2).longValue());
            case FCMPG -> fcmpg(unbox(cst1).floatValue(), unbox(cst2).floatValue());
            case FCMPL -> fcmpl(unbox(cst1).floatValue(), unbox(cst2).floatValue());
            case DCMPG -> dcmpg(unbox(cst1).doubleValue(), unbox(cst2).doubleValue());
            case DCMPL -> dcmpl(unbox(cst1).doubleValue(), unbox(cst2).doubleValue());
            default -> throw new Error("not good");
        };
    }

    public static boolean jump_cmp(final int opcode, final Object cst1, final Object cst2) {
        return switch (opcode) {
            case IF_ICMPEQ -> if_icmpeq(unbox(cst1).intValue(), unbox(cst2).intValue());
            case IF_ICMPGE -> if_icmpge(unbox(cst1).intValue(), unbox(cst2).intValue());
            case IF_ICMPGT -> if_icmpgt(unbox(cst1).intValue(), unbox(cst2).intValue());
            case IF_ICMPLT -> if_icmplt(unbox(cst1).intValue(), unbox(cst2).intValue());
            case IF_ICMPLE -> if_icmple(unbox(cst1).intValue(), unbox(cst2).intValue());
            case IF_ICMPNE -> if_icmpne(unbox(cst1).intValue(), unbox(cst2).intValue());
            default -> throw new Error("not good.");
        };
    }

    public static boolean equ(final int opcode, final Object i1) {
        return switch (opcode) {
            case IFEQ -> ifeq(unbox(i1).intValue());
            case IFNE -> ifne(unbox(i1).intValue());
            case IFLT -> iflt(unbox(i1).intValue());
            case IFGE -> ifge(unbox(i1).intValue());
            case IFGT -> ifgt(unbox(i1).intValue());
            case IFLE -> ifle(unbox(i1).intValue());
            case IFNULL -> ifnull(i1);
            case IFNONNULL -> ifnonnull(i1);
            default -> throw new Error("shouldnt reach here: " + opcode);
        };
    }

    public static Set<Invoke> references(final ClassPool pool, final Method target) {
        final Set<Invoke> references = new HashSet<>();

        for (final Class node : pool)
            for (final Method method : node.methods)
                for (final Node instruction : method.instructions)
                    if (instruction instanceof Invoke invoke)
                        if (invoke.owner.equals(target.klass.name) && invoke.name.equals(target.name) && invoke.desc.equals(target.desc))
                            references.add(invoke);

        return references;
    }

    public static Method clinit(final Class klass) {
        for (final Method method : klass.methods)
            if (method.name.equals("<clinit>"))
                return method;

        return null;
    }

    public static Type convertType(final int opcode) {
        return switch (opcode) {
            case ALOAD, ASTORE: yield Type.getType(Object.class);
            case LLOAD, LSTORE, I2L, F2L, D2L: yield Type.LONG_TYPE;
            case FLOAD, FSTORE, I2F, L2F, D2F: yield Type.FLOAT_TYPE;
            case DLOAD, DSTORE, I2D, L2D, F2D: yield Type.DOUBLE_TYPE;
            case ILOAD, ISTORE, L2I, F2I, D2I: yield Type.INT_TYPE;
            case I2B: yield Type.BYTE_TYPE;
            case I2C: yield Type.CHAR_TYPE;
            case I2S: yield Type.SHORT_TYPE;
            default: throw new IllegalStateException("this is not good: " + opcode);
        };
    }

    private static int getArrayType(final java.lang.Class<?> type) {
        return switch (type.getName()) {
            case "boolean" -> T_BOOLEAN;
            case "char" -> T_CHAR;
            case "byte" -> T_BYTE;
            case "short" -> T_SHORT;
            case "int" -> T_INT;
            case "float" -> T_FLOAT;
            case "long" -> T_LONG;
            case "double" -> T_DOUBLE;
            default -> -1;
        };
    }

    public static Node previous(final Node base, final Predicate<Node> predicate) {
        Node prev = base.previous;

        while (prev != null) {
            if (predicate.test(prev)) return prev;
            prev = prev.previous;
        }

        return null;
    }

    public static Node next(final Node base, final Predicate<Node> predicate) {
        Node next = base.next;

        while (next != null) {
            if (predicate.test(next)) return next;
            next = next.next;
        }

        return null;
    }

    public static int[] resolveParameterIndexes(final Type[] args) {
        final int[] res = new int[args.length];

        int index = 0;

        for (int i = 0; i < args.length; i++) {
            res[i] = index;

            final String descriptor = args[i].getDescriptor();

            if (descriptor.equals("J") || descriptor.equals("D")) index += 2;
            else index += 1;
        }

        return res;
    }

    public static Frame<BasicValue>[] analyze(final Method method) throws AnalyzerException {
        final MethodNode methodNode = new MethodNode(ASM9, method.access.getAccess(), method.name, method.desc, method.signature, method.exceptions.toArray(new String[0]));
        method.accept(methodNode);
        final Analyzer<BasicValue> analyzer = new Analyzer<>(new FixedInterpreter());
        methodNode.maxStack += 50;
        analyzer.analyze(method.klass.name, methodNode);
        methodNode.maxStack -= 50;
        return analyzer.getFrames();
    }

    // TODO wrap exception handlers, reduce junk
    public static void wrapStack(final Method method, final Frame<BasicValue>[] frames, final LocalPool pool) {
        final Map<Node, Frame<BasicValue>> frame_map = new HashMap<>();
        final Node[] arr = method.instructions.toArray();

        for (int i = 0; i < frames.length; i++) frame_map.put(arr[i], frames[i]);

        final Map<Label, List<LocalPool.Local>> localMap = new HashMap<>();

        for (final Node instruction : method.instructions) {
            if (instruction instanceof Lookup lookup) {
                final Frame<BasicValue> frame = frame_map.get(lookup);
                if (frame == null || frame.getStackSize() <= 0) throw new IllegalStateException();

                final LocalPool.Entry entry = pool.nextEntry();
                // discard lookup index
                for (int i = 0; i < frame.getStackSize() - 1; i++) entry.next(LocalPool.type(frame.getStack(i)));
                final List<LocalPool.Local> locals = new ArrayList<>(List.of(entry.get()));

                localMap.put(lookup._default, locals);
                for (final Label label : lookup.labels) localMap.put(label, locals);

                Collections.reverse(locals);

                for (final LocalPool.Local local : locals) {
                    final boolean c2 = local.type == LONG || local.type == DOUBLE;
                    lookup.insertBefore(new Instruction(c2 ? DUP_X2 : DUP_X1));
                    lookup.insertBefore(new Instruction(POP));
                    lookup.insertBefore(local.store());
                }

                Collections.reverse(locals);

                continue;
            }

            if (instruction instanceof Table table) {
                final Frame<BasicValue> frame = frame_map.get(table);
                if (frame == null || frame.getStackSize() <= 0) throw new IllegalStateException();

                final LocalPool.Entry entry = pool.nextEntry();
                // discard table index
                for (int i = 0; i < frame.getStackSize() - 1; i++) entry.next(LocalPool.type(frame.getStack(i)));
                final List<LocalPool.Local> locals = new ArrayList<>(List.of(entry.get()));

                localMap.put(table._default, locals);
                for (final Label label : table.labels) localMap.put(label, locals);

                Collections.reverse(locals);

                for (final LocalPool.Local local : locals) {
                    final boolean c2 = local.type == LONG || local.type == DOUBLE;
                    table.insertBefore(new Instruction(c2 ? DUP_X2 : DUP_X1));
                    table.insertBefore(new Instruction(POP));
                    table.insertBefore(local.store());
                }

                Collections.reverse(locals);
            }
        }

        for (final Node instruction : method.instructions) {
            if (!(instruction instanceof Label label) || localMap.containsKey(label)) continue;

            final Frame<BasicValue> frame = frame_map.get(label);
            if (frame == null || frame.getStackSize() <= 0) continue; // deadcode

            final LocalPool.Entry entry = pool.nextEntry();
            for (int i = 0; i < frame.getStackSize(); i++) entry.next(LocalPool.type(frame.getStack(i)));
            localMap.put(label, new ArrayList<>(List.of(entry.get())));
        }

        localMap.forEach((label, locals) -> {
            Collections.reverse(locals);

            for (final Node node : method.instructions.toArray()) {
                if (!(node instanceof Jump jump) || !jump.label.equals(label)) continue;

                if (jump.opcode == GOTO) {
                    for (final LocalPool.Local local : locals) jump.insertBefore(local.store());
                    continue;
                }

                final Label wrapper = jump.label;
                if (!wrapper.flags.has(Flags.Instruction.WRAPPED_CONDITION)) continue;

                Collections.reverse(locals);

                for (final LocalPool.Local local : locals) wrapper.insertAfter(local.store());

                Collections.reverse(locals);
            }

            // handle case where it jumps to a handler ? ignored. above
            if (label.flags.has(Flags.Instruction.HANDLER))  {
                Collections.reverse(locals);
                return;
            }

            for (final LocalPool.Local local : locals) label.insertAfter(local.load());

            Collections.reverse(locals);
        });
    }

    public static void wrapLabels(final Instructions instructions) {
        for (final Node instruction : instructions) {
            if (!(instruction instanceof Label label)) continue;
            label.insertBefore(label.jump(GOTO));
        }
    }

    public static void wrapLabels(final Instructions instructions, final Predicate<Label> pred) {
        for (final Node instruction : instructions) {
            if (!(instruction instanceof Label label)) continue;
            if (!pred.test(label)) continue;
            label.insertBefore(label.jump(GOTO));
        }
    }

    public static void wrapConditionals(final Instructions instructions) {
        for (final Node instruction : instructions) {
            if (!(instruction instanceof Jump jump)) continue;
            if (jump.opcode == GOTO) continue;

            final InstructionBuilder builder = InstructionBuilder.generate();
            final Label esc = builder.newlabel();
            final Label _true = builder.newlabel();

            builder.add(_true.jump(jump.opcode));
            builder.jump(esc);
            builder.bind(_true);
            builder.jump(jump.label);
            builder.bind(esc);

            _true.flags.set(Flags.Instruction.WRAPPED_CONDITION, true);

            jump.replace(builder.build());
        }
    }

    public static void wrapSwitches(final Instructions instructions) {
        for (final Node instruction : instructions)
            if (instruction instanceof Table table) {
                for (int i = 0; i < table.labels.length; i++) {
                    final Label wrapper = new Label();
                    instructions.add(wrapper);
                    instructions.add(table.labels[i].jump(GOTO));
                    table.labels[i] = wrapper;
                }
            } else if (instruction instanceof Lookup lookup) {
                for (int i = 0; i < lookup.labels.length; i++) {
                    final Label wrapper = new Label();
                    instructions.add(wrapper);
                    instructions.add(lookup.labels[i].jump(GOTO));
                    lookup.labels[i] = wrapper;
                }
            }
    }
}