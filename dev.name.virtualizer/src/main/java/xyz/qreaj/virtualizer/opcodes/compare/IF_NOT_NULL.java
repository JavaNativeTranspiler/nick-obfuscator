package xyz.qreaj.virtualizer.opcodes.compare;

import xyz.qreaj.virtualizer.opcodes.type.SingleConditionJumpOpcode;

public class IF_NOT_NULL extends SingleConditionJumpOpcode {
    @Override
    public boolean getResult(final Object n) {
        return n != null;
    }
}