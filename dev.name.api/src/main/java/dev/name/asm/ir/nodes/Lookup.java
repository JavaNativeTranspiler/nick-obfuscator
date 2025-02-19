package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

@SuppressWarnings("unused")
public final class Lookup extends Node {
    public Label _default;
    public int[] keys;
    public Label[] labels;

    public Lookup() {
        super(LOOKUPSWITCH);
    }

    public Lookup(final Label _default, final int[] keys, final Label... labels) {
        super(LOOKUPSWITCH);
        this._default = _default;
        this.keys = keys;
        this.labels = labels;
    }

    public Lookup(final int[] keys, final Label... labels) {
        this(null, keys, labels);
    }

    public Lookup(final int[] keys) {
        this(null, keys);
    }

    public Lookup(final Label... labels) {
        this(null, new int[0], labels);
    }

    @Override
    public int type() {
        return Node.LOOKUP;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (_default == null || keys == null || labels == null || (keys.length != labels.length)) throw new IllegalStateException();
        visitor.visitLookupSwitchInsn(this._default.form(), this.keys, Arrays.stream(labels).map(Label::form).toArray(org.objectweb.asm.Label[]::new));
        super.annotations(visitor);
    }
}