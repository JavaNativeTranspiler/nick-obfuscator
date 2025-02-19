package dev.name.asm.ir.instructions.builders;

import dev.name.asm.ir.components.Field;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.InstructionBuilder;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Descriptor;
import dev.name.asm.ir.types.Node;

import java.util.function.Consumer;

public class BasicInstructionBuilder extends InstructionBuilder {
    private final Instructions instructions = new Instructions();

    @Override
    public InstructionBuilder nop() {
        instructions.add(new Instruction(NOP));
        return this;
    }

    @Override
    public InstructionBuilder aconst_null() {
        instructions.add(new Constant(null));
        return this;
    }

    @Override
    public InstructionBuilder iconst_m1() {
        instructions.add(new Constant(-1));
        return this;
    }

    @Override
    public InstructionBuilder iconst_0() {
        instructions.add(new Constant(0));
        return this;
    }

    @Override
    public InstructionBuilder iconst_1() {
        instructions.add(new Constant(1));
        return this;
    }

    @Override
    public InstructionBuilder iconst_2() {
        instructions.add(new Constant(2));
        return this;
    }

    @Override
    public InstructionBuilder iconst_3() {
        instructions.add(new Constant(3));
        return this;
    }

    @Override
    public InstructionBuilder iconst_4() {
        instructions.add(new Constant(4));
        return this;
    }

    @Override
    public InstructionBuilder iconst_5() {
        instructions.add(new Constant(5));
        return this;
    }

    @Override
    public InstructionBuilder lconst_0() {
        instructions.add(new Constant(0L));
        return this;
    }

    @Override
    public InstructionBuilder lconst_1() {
        instructions.add(new Constant(1L));
        return this;
    }

    @Override
    public InstructionBuilder fconst_0() {
        instructions.add(new Constant(0.0F));
        return this;
    }

    @Override
    public InstructionBuilder fconst_1() {
        instructions.add(new Constant(1.0F));
        return this;
    }

    @Override
    public InstructionBuilder fconst_2() {
        instructions.add(new Constant(2.0F));
        return this;
    }

    @Override
    public InstructionBuilder dconst_0() {
        instructions.add(new Constant(0.0D));
        return this;
    }

    @Override
    public InstructionBuilder dconst_1() {
        instructions.add(new Constant(1.0D));
        return this;
    }

    @Override
    public InstructionBuilder bipush(final int operand) {
        instructions.add(new Constant(operand));
        return this;
    }

    @Override
    public InstructionBuilder sipush(final int operand) {
        instructions.add(new Constant(operand));
        return this;
    }

    @Override
    public InstructionBuilder ldc(final Object cst) {
        instructions.add(new Constant(cst));
        return this;
    }

    @Override
    public InstructionBuilder iload(final int index) {
        instructions.add(new Variable(ILOAD, index));
        return this;
    }

    @Override
    public InstructionBuilder lload(final int index) {
        instructions.add(new Variable(LLOAD, index));
        return this;
    }

    @Override
    public InstructionBuilder fload(final int index) {
        instructions.add(new Variable(FLOAD, index));
        return this;
    }

    @Override
    public InstructionBuilder dload(final int index) {
        instructions.add(new Variable(DLOAD, index));
        return this;
    }

    @Override
    public InstructionBuilder aload(final int index) {
        instructions.add(new Variable(ALOAD, index));
        return this;
    }

    @Override
    public InstructionBuilder iaload() {
        instructions.add(new Instruction(IALOAD));
        return this;
    }

    @Override
    public InstructionBuilder laload() {
        instructions.add(new Instruction(LALOAD));
        return this;
    }

    @Override
    public InstructionBuilder faload() {
        instructions.add(new Instruction(FALOAD));
        return this;
    }

    @Override
    public InstructionBuilder daload() {
        instructions.add(new Instruction(DALOAD));
        return this;
    }

    @Override
    public InstructionBuilder aaload() {
        instructions.add(new Instruction(AALOAD));
        return this;
    }

    @Override
    public InstructionBuilder baload() {
        instructions.add(new Instruction(BALOAD));
        return this;
    }

    @Override
    public InstructionBuilder caload() {
        instructions.add(new Instruction(CALOAD));
        return this;
    }

    @Override
    public InstructionBuilder saload() {
        instructions.add(new Instruction(SALOAD));
        return this;
    }

    @Override
    public InstructionBuilder istore(final int index) {
        instructions.add(new Variable(ISTORE, index));
        return this;
    }

    @Override
    public InstructionBuilder lstore(final int index) {
        instructions.add(new Variable(LSTORE, index));
        return this;
    }

    @Override
    public InstructionBuilder fstore(final int index) {
        instructions.add(new Variable(FSTORE, index));
        return this;
    }

    @Override
    public InstructionBuilder dstore(final int index) {
        instructions.add(new Variable(DSTORE, index));
        return this;
    }

    @Override
    public InstructionBuilder astore(final int index) {
        instructions.add(new Variable(ASTORE, index));
        return this;
    }

    @Override
    public InstructionBuilder iastore() {
        instructions.add(new Instruction(IASTORE));
        return this;
    }

    @Override
    public InstructionBuilder lastore() {
        instructions.add(new Instruction(LASTORE));
        return this;
    }

    @Override
    public InstructionBuilder fastore() {
        instructions.add(new Instruction(FASTORE));
        return this;
    }

    @Override
    public InstructionBuilder dastore() {
        instructions.add(new Instruction(DASTORE));
        return this;
    }

    @Override
    public InstructionBuilder aastore() {
        instructions.add(new Instruction(AASTORE));
        return this;
    }

    @Override
    public InstructionBuilder bastore() {
        instructions.add(new Instruction(BASTORE));
        return this;
    }

    @Override
    public InstructionBuilder castore() {
        instructions.add(new Instruction(CASTORE));
        return this;
    }

    @Override
    public InstructionBuilder sastore() {
        instructions.add(new Instruction(SASTORE));
        return this;
    }

    @Override
    public InstructionBuilder pop() {
        instructions.add(new Instruction(POP));
        return this;
    }

    @Override
    public InstructionBuilder pop2() {
        instructions.add(new Instruction(POP2));
        return this;
    }

    @Override
    public InstructionBuilder dup() {
        instructions.add(new Instruction(DUP));
        return this;
    }

    @Override
    public InstructionBuilder dup_x1() {
        instructions.add(new Instruction(DUP_X1));
        return this;
    }

    @Override
    public InstructionBuilder dup_x2() {
        instructions.add(new Instruction(DUP_X2));
        return this;
    }

    @Override
    public InstructionBuilder dup2() {
        instructions.add(new Instruction(DUP2));
        return this;
    }

    @Override
    public InstructionBuilder dup2_x1() {
        instructions.add(new Instruction(DUP2_X1));
        return this;
    }

    @Override
    public InstructionBuilder dup2_x2() {
        instructions.add(new Instruction(DUP2_X2));
        return this;
    }

    @Override
    public InstructionBuilder swap() {
        instructions.add(new Instruction(SWAP));
        return this;
    }

    @Override
    public InstructionBuilder iadd() {
        instructions.add(new Instruction(IADD));
        return this;
    }

    @Override
    public InstructionBuilder ladd() {
        instructions.add(new Instruction(LADD));
        return this;
    }

    @Override
    public InstructionBuilder fadd() {
        instructions.add(new Instruction(FADD));
        return this;
    }

    @Override
    public InstructionBuilder dadd() {
        instructions.add(new Instruction(DADD));
        return this;
    }

    @Override
    public InstructionBuilder isub() {
        instructions.add(new Instruction(ISUB));
        return this;
    }

    @Override
    public InstructionBuilder lsub() {
        instructions.add(new Instruction(LSUB));
        return this;
    }

    @Override
    public InstructionBuilder fsub() {
        instructions.add(new Instruction(FSUB));
        return this;
    }

    @Override
    public InstructionBuilder dsub() {
        instructions.add(new Instruction(DSUB));
        return this;
    }

    @Override
    public InstructionBuilder imul() {
        instructions.add(new Instruction(IMUL));
        return this;
    }

    @Override
    public InstructionBuilder lmul() {
        instructions.add(new Instruction(LMUL));
        return this;
    }

    @Override
    public InstructionBuilder fmul() {
        instructions.add(new Instruction(FMUL));
        return this;
    }

    @Override
    public InstructionBuilder dmul() {
        instructions.add(new Instruction(DMUL));
        return this;
    }

    @Override
    public InstructionBuilder idiv() {
        instructions.add(new Instruction(IDIV));
        return this;
    }

    @Override
    public InstructionBuilder ldiv() {
        instructions.add(new Instruction(LDIV));
        return this;
    }

    @Override
    public InstructionBuilder fdiv() {
        instructions.add(new Instruction(FDIV));
        return this;
    }

    @Override
    public InstructionBuilder ddiv() {
        instructions.add(new Instruction(DDIV));
        return this;
    }

    @Override
    public InstructionBuilder irem() {
        instructions.add(new Instruction(IREM));
        return this;
    }

    @Override
    public InstructionBuilder lrem() {
        instructions.add(new Instruction(LREM));
        return this;
    }

    @Override
    public InstructionBuilder frem() {
        instructions.add(new Instruction(FREM));
        return this;
    }

    @Override
    public InstructionBuilder drem() {
        instructions.add(new Instruction(DREM));
        return this;
    }

    @Override
    public InstructionBuilder ineg() {
        instructions.add(new Instruction(INEG));
        return this;
    }

    @Override
    public InstructionBuilder lneg() {
        instructions.add(new Instruction(LNEG));
        return this;
    }

    @Override
    public InstructionBuilder fneg() {
        instructions.add(new Instruction(FNEG));
        return this;
    }

    @Override
    public InstructionBuilder dneg() {
        instructions.add(new Instruction(DNEG));
        return this;
    }

    @Override
    public InstructionBuilder ishl() {
        instructions.add(new Instruction(ISHL));
        return this;
    }

    @Override
    public InstructionBuilder lshl() {
        instructions.add(new Instruction(LSHL));
        return this;
    }

    @Override
    public InstructionBuilder ishr() {
        instructions.add(new Instruction(ISHR));
        return this;
    }

    @Override
    public InstructionBuilder lshr() {
        instructions.add(new Instruction(LSHR));
        return this;
    }

    @Override
    public InstructionBuilder iushr() {
        instructions.add(new Instruction(IUSHR));
        return this;
    }

    @Override
    public InstructionBuilder lushr() {
        instructions.add(new Instruction(LUSHR));
        return this;
    }

    @Override
    public InstructionBuilder iand() {
        instructions.add(new Instruction(IAND));
        return this;
    }

    @Override
    public InstructionBuilder land() {
        instructions.add(new Instruction(LAND));
        return this;
    }

    @Override
    public InstructionBuilder ior() {
        instructions.add(new Instruction(IOR));
        return this;
    }

    @Override
    public InstructionBuilder lor() {
        instructions.add(new Instruction(LOR));
        return this;
    }

    @Override
    public InstructionBuilder ixor() {
        instructions.add(new Instruction(IXOR));
        return this;
    }

    @Override
    public InstructionBuilder lxor() {
        instructions.add(new Instruction(LXOR));
        return this;
    }

    @Override
    public InstructionBuilder iinc(final int index, final int amount) {
        // var pool ?
        instructions.add(new Increment(new Variable(ISTORE, index), amount));
        return this;
    }

    @Override
    public InstructionBuilder i2l() {
        instructions.add(new Instruction(I2L));
        return this;
    }

    @Override
    public InstructionBuilder i2f() {
        instructions.add(new Instruction(I2F));
        return this;
    }

    @Override
    public InstructionBuilder i2d() {
        instructions.add(new Instruction(I2D));
        return this;
    }

    @Override
    public InstructionBuilder l2i() {
        instructions.add(new Instruction(L2I));
        return this;
    }

    @Override
    public InstructionBuilder l2f() {
        instructions.add(new Instruction(L2F));
        return this;
    }

    @Override
    public InstructionBuilder l2d() {
        instructions.add(new Instruction(L2D));
        return this;
    }

    @Override
    public InstructionBuilder f2i() {
        instructions.add(new Instruction(F2I));
        return this;
    }

    @Override
    public InstructionBuilder f2l() {
        instructions.add(new Instruction(F2L));
        return this;
    }

    @Override
    public InstructionBuilder f2d() {
        instructions.add(new Instruction(F2D));
        return this;
    }

    @Override
    public InstructionBuilder d2i() {
        instructions.add(new Instruction(D2I));
        return this;
    }

    @Override
    public InstructionBuilder d2l() {
        instructions.add(new Instruction(D2L));
        return this;
    }

    @Override
    public InstructionBuilder d2f() {
        instructions.add(new Instruction(D2F));
        return this;
    }

    @Override
    public InstructionBuilder i2b() {
        instructions.add(new Instruction(I2B));
        return this;
    }

    @Override
    public InstructionBuilder i2c() {
        instructions.add(new Instruction(I2C));
        return this;
    }

    @Override
    public InstructionBuilder i2s() {
        instructions.add(new Instruction(I2S));
        return this;
    }

    @Override
    public InstructionBuilder lcmp() {
        instructions.add(new Instruction(LCMP));
        return this;
    }

    @Override
    public InstructionBuilder fcmpl() {
        instructions.add(new Instruction(FCMPL));
        return this;
    }

    @Override
    public InstructionBuilder fcmpg() {
        instructions.add(new Instruction(FCMPG));
        return this;
    }

    @Override
    public InstructionBuilder dcmpl() {
        instructions.add(new Instruction(DCMPL));
        return this;
    }

    @Override
    public InstructionBuilder dcmpg() {
        instructions.add(new Instruction(DCMPG));
        return this;
    }

    @Override
    public InstructionBuilder ifeq(final Label label) {
        instructions.add(new Jump(IFEQ, label));
        return this;
    }

    @Override
    public InstructionBuilder ifne(final Label label) {
        instructions.add(new Jump(IFNE, label));
        return this;
    }

    @Override
    public InstructionBuilder iflt(final Label label) {
        instructions.add(new Jump(IFLT, label));
        return this;
    }

    @Override
    public InstructionBuilder ifge(final Label label) {
        instructions.add(new Jump(IFGE, label));
        return this;
    }

    @Override
    public InstructionBuilder ifgt(final Label label) {
        instructions.add(new Jump(IFGT, label));
        return this;
    }

    @Override
    public InstructionBuilder ifle(final Label label) {
        instructions.add(new Jump(IFLE, label));
        return this;
    }

    @Override
    public InstructionBuilder if_icmpeq(final Label label) {
        instructions.add(new Jump(IF_ICMPEQ, label));
        return this;
    }

    @Override
    public InstructionBuilder if_icmpne(final Label label) {
        instructions.add(new Jump(IF_ICMPNE, label));
        return this;
    }

    @Override
    public InstructionBuilder if_icmplt(final Label label) {
        instructions.add(new Jump(IF_ICMPLT, label));
        return this;
    }

    @Override
    public InstructionBuilder if_icmpge(final Label label) {
        instructions.add(new Jump(IF_ICMPGE, label));
        return this;
    }

    @Override
    public InstructionBuilder if_icmpgt(final Label label) {
        instructions.add(new Jump(IF_ICMPGT, label));
        return this;
    }

    @Override
    public InstructionBuilder if_icmple(final Label label) {
        instructions.add(new Jump(IF_ICMPLE, label));
        return this;
    }

    @Override
    public InstructionBuilder if_acmpeq(final Label label) {
        instructions.add(new Jump(IF_ACMPEQ, label));
        return this;
    }

    @Override
    public InstructionBuilder if_acmpne(final Label label) {
        instructions.add(new Jump(IF_ACMPNE, label));
        return this;
    }

    @Override
    public InstructionBuilder jump(final Label label) {
        instructions.add(new Jump(GOTO, label));
        return this;
    }

    @Override
    public InstructionBuilder jsr(final Label label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstructionBuilder ret(final Label label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstructionBuilder tableswitch(final Table table) {
        instructions.add(table);
        return this;
    }

    @Override
    public InstructionBuilder tableswitch(final int min, final int max, final Label _default, final Label... labels) {
        instructions.add(new Table(min, max, _default, labels));
        return this;
    }

    @Override
    public InstructionBuilder lookupswitch(final Lookup lookup) {
        instructions.add(lookup);
        return this;
    }

    @Override
    public InstructionBuilder lookupswitch(final Label _default, final int[] keys, final Label... labels) {
        instructions.add(new Lookup(_default, keys, labels));
        return this;
    }

    @Override
    public InstructionBuilder ireturn() {
        instructions.add(new Instruction(IRETURN));
        return this;
    }

    @Override
    public InstructionBuilder lreturn() {
        instructions.add(new Instruction(LRETURN));
        return this;
    }

    @Override
    public InstructionBuilder freturn() {
        instructions.add(new Instruction(FRETURN));
        return this;
    }

    @Override
    public InstructionBuilder dreturn() {
        instructions.add(new Instruction(DRETURN));
        return this;
    }

    @Override
    public InstructionBuilder areturn() {
        instructions.add(new Instruction(ARETURN));
        return this;
    }

    @Override
    public InstructionBuilder _return() {
        instructions.add(new Instruction(RETURN));
        return this;
    }

    @Override
    public InstructionBuilder access(final Accessor field) {
        instructions.add(field);
        return this;
    }

    @Override
    public InstructionBuilder getfield(final String owner, final String name, final String desc) {
        instructions.add(new Accessor(GETFIELD, owner, name, desc));
        return this;
    }

    @Override
    public InstructionBuilder getfield(final String owner, final String name, final Class<?> klass) {
        return getfield(owner, name, Descriptor.of(klass));
    }

    @Override
    public InstructionBuilder putfield(final String owner, final String name, final String desc) {
        instructions.add(new Accessor(PUTFIELD, owner, name, desc));
        return this;
    }

    @Override
    public InstructionBuilder putfield(final String owner, final String name, final Class<?> klass) {
        return putfield(owner, name, Descriptor.of(klass));
    }

    @Override
    public InstructionBuilder getstatic(final String owner, final String name, final String desc) {
        instructions.add(new Accessor(GETSTATIC, owner, name, desc));
        return this;
    }

    @Override
    public InstructionBuilder getstatic(final String owner, final String name, final Class<?> klass) {
        return getstatic(owner, name, Descriptor.of(klass));
    }

    @Override
    public InstructionBuilder putstatic(final String owner, final String name, final String desc) {
        instructions.add(new Accessor(PUTSTATIC, owner, name, desc));
        return this;
    }

    @Override
    public InstructionBuilder putstatic(final String owner, final String name, final Class<?> klass) {
        return putstatic(owner, name, Descriptor.of(klass));
    }

    @Override
    public InstructionBuilder invoke(final Invoke method) {
        instructions.add(method);
        return this;
    }

    @Override
    public InstructionBuilder invokestatic(final String owner, final String name, final String desc, final boolean _interface) {
        instructions.add(new Invoke(INVOKESTATIC, owner, name, desc, _interface));
        return this;
    }

    @Override
    public InstructionBuilder invokevirtual(final String owner, final String name, final String desc, final boolean _interface) {
        instructions.add(new Invoke(INVOKEVIRTUAL, owner, name, desc, _interface));
        return this;
    }

    @Override
    public InstructionBuilder invokespecial(final String owner, final String name, final String desc) {
        instructions.add(new Invoke(INVOKESPECIAL, owner, name, desc));
        return this;
    }

    @Override
    public InstructionBuilder invokeinterface(final String owner, final String name, final String desc) {
        instructions.add(new Invoke(INVOKEINTERFACE, owner, name, desc));
        return this;
    }

    @Override
    public InstructionBuilder invokedynamic(final Dynamic dynamic) {
        instructions.add(dynamic);
        return this;
    }

    @Override
    public InstructionBuilder _new(final String desc) {
        instructions.add(new Type(NEW, desc));
        return this;
    }

    @Override
    public InstructionBuilder _new(final Class<?> klass) {
        return _new(klass.getName().replace('.', '/'));
    }

    @Override
    public InstructionBuilder newarray(final String desc) {
        instructions.add(new Type(NEWARRAY, desc));
        return this;
    }

    @Override
    public InstructionBuilder newarray(final Class<?> klass) {
        return newarray(klass.getName().replace('.', '/'));
    }

    @Override
    public InstructionBuilder anewarray(final String desc) {
        instructions.add(new Type(ANEWARRAY, desc));
        return this;
    }

    @Override
    public InstructionBuilder anewarray(final Class<?> klass) {
        return anewarray(Descriptor.of(klass));
    }

    @Override
    public InstructionBuilder arraylength() {
        instructions.add(new Instruction(ARRAYLENGTH));
        return this;
    }

    @Override
    public InstructionBuilder athrow() {
        instructions.add(new Instruction(ATHROW));
        return this;
    }

    @Override
    public InstructionBuilder checkcast(final String desc) {
        instructions.add(new Type(CHECKCAST, desc));
        return this;
    }

    @Override
    public InstructionBuilder checkcast(final Class<?> klass) {
        return checkcast(klass.getName().replace('.', '/'));
    }

    @Override
    public InstructionBuilder _instanceof(final String desc) {
        instructions.add(new Type(INSTANCEOF, desc));
        return this;
    }

    @Override
    public InstructionBuilder _instanceof(Class<?> klass) {
        return _instanceof(klass.getName().replace('.', '/'));
    }

    @Override
    public InstructionBuilder monitorenter() {
        instructions.add(new Instruction(MONITORENTER));
        return this;
    }

    @Override
    public InstructionBuilder monitorexit() {
        instructions.add(new Instruction(MONITOREXIT));
        return this;
    }

    @Override
    public InstructionBuilder multianewarray(final Array array) {
        instructions.add(array);
        return this;
    }

    @Override
    public InstructionBuilder multianewarray(final String desc, final int dimensions) {
        instructions.add(new Array(desc, dimensions));
        return this;
    }

    @Override
    public InstructionBuilder multianewarray(final Class<?> klass, final int dimensions) {
        return multianewarray(klass.getName().replace('.', '/'), dimensions);
    }

    @Override
    public InstructionBuilder ifnull(final Label label) {
        instructions.add(new Jump(IFNULL, label));
        return this;
    }

    @Override
    public InstructionBuilder ifnonnull(final Label label) {
        instructions.add(new Jump(IFNONNULL, label));
        return this;
    }

    @Override
    public InstructionBuilder bind(final Label label) {
        instructions.add(label);
        return this;
    }

    @Override
    public Label label() {
        final Label label = new Label();
        instructions.add(label);
        return label;
    }

    @Override
    public Label newlabel() {
        return new Label();
    }

    @Override
    public InstructionBuilder get(final Field field) {
        instructions.add(field.getter());
        return this;
    }

    @Override
    public InstructionBuilder put(final Field field) {
        instructions.add(field.setter());
        return this;
    }

    @Override
    public InstructionBuilder invoke(final Method method) {
        instructions.add(method.invoker());
        return this;
    }

    @Override
    public InstructionBuilder load(final Variable variable) {
        instructions.add(variable);
        return this;
    }

    @Override
    public InstructionBuilder store(final Variable variable) {
        instructions.add(variable);
        return this;
    }

    @Override
    public InstructionBuilder increment(final Increment incr) {
        instructions.add(incr);
        return this;
    }

    @Override
    public InstructionBuilder constant(final Constant constant) {
        instructions.add(constant);
        return this;
    }

    @Override
    public InstructionBuilder frame(final Frame frame) {
        instructions.add(frame);
        return this;
    }

    @Override
    public InstructionBuilder add(final Node node) {
        instructions.add(node);
        return this;
    }

    @Override
    public InstructionBuilder add(final Instructions instructions) {
        this.instructions.add(instructions);
        return this;
    }

    @Override
    public InstructionBuilder insertBefore(final Node existing, final Node node) {
        existing.insertBefore(node);
        return this;
    }

    @Override
    public InstructionBuilder insertBefore(final Node existing, final Instructions instructions) {
        existing.insertBefore(instructions);
        return this;
    }

    @Override
    public InstructionBuilder insertAfter(final Node existing, final Node node) {
        existing.insertAfter(node);
        return this;
    }

    @Override
    public InstructionBuilder insertAfter(final Node node, final Instructions instructions) {
        node.insertAfter(instructions);
        return this;
    }

    @Override
    public InstructionBuilder set(final Node node, final Node replacement) {
        node.replace(replacement);
        return this;
    }

    @Override
    public InstructionBuilder set(final Node node, final Instructions instructions) {
        node.replace(instructions);
        return this;
    }

    @Override
    public Instructions build() {
        return instructions;
    }

    @Override
    public void forEach(final Consumer<Node> consumer) {
        instructions.forEach(consumer);
    }
}