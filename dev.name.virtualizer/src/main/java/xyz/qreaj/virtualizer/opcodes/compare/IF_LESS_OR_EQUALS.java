package xyz.qreaj.virtualizer.opcodes.compare;

import xyz.qreaj.virtualizer.opcodes.type.DoubleConditionJumpOpcode;
import xyz.qreaj.virtualizer.utils.NumberType;

public class IF_LESS_OR_EQUALS extends DoubleConditionJumpOpcode {
    @Override
    public boolean evaluate(final Object o1, final Object o2) {
        final Number n1 = (o1 instanceof Character c) ? (int) c : (Number) o1;
        final Number n2 = (o2 instanceof Character c) ? (int) c : (Number) o2;

        return switch (NumberType.type(o1)) {
            case BYTE -> n1.byteValue() <= n2.byteValue();
            case SHORT -> n1.shortValue() <= n2.shortValue();
            case CHAR, INT -> n1.intValue() <= n2.intValue();
            case FLOAT -> n1.floatValue() <= n2.floatValue();
            case LONG -> n1.longValue() <= n2.longValue();
            case DOUBLE -> n1.doubleValue() <= n2.doubleValue();
            default -> throw new IllegalArgumentException("Unsupported operand types: " + o1.getClass() + ", " + o2.getClass());
        };
    }
}