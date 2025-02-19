package xyz.qreaj.virtualizer.opcodes.compare;

import xyz.qreaj.virtualizer.opcodes.type.DoubleConditionJumpOpcode;

public class IF_NOT_EQUALS extends DoubleConditionJumpOpcode {
    @Override
    public boolean evaluate(final Object n1, final Object n2) {
        return !n1.equals(n2);
    }
}