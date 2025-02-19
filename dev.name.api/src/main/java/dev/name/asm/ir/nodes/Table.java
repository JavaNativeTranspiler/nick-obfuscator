package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

@SuppressWarnings("unused")
public final class Table extends Node {
    public int min, max;
    public Label _default;
    public Label[] labels;

    public Table() {
        super(TABLESWITCH);
    }

    public Table(final int min, final int max, final Label _default, final Label... labels) {
        super(TABLESWITCH);
        this.min = min;
        this.max = max;
        this._default = _default;
        this.labels = labels;
    }

    public Table(final int min, final int max, final Label _default) {
        this(min, max, _default, new Label[0]);
    }

    public Table(final int min, final int max, final Label... labels) {
        this(min, max, null, labels);
    }

    public Table(final int min, final int max) {
        this(min, max, null, new Label[0]);
    }

    @Override
    public int type() {
        return Node.TABLE;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (min > max) throw new IndexOutOfBoundsException();
        if (_default == null || labels == null || labels.length == 0) throw new IllegalStateException();
        visitor.visitTableSwitchInsn(this.min, this.max, this._default.form(), Arrays.stream(this.labels).map(Label::form).toArray(org.objectweb.asm.Label[]::new));
        super.annotations(visitor);
    }
}