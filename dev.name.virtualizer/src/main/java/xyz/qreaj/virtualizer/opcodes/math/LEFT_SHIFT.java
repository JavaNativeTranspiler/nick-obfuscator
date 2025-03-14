package xyz.qreaj.virtualizer.opcodes.math;

import xyz.qreaj.virtualizer.opcodes.type.ArithmeticOpcode;
import xyz.qreaj.virtualizer.utils.NumberType;

public class LEFT_SHIFT extends ArithmeticOpcode {
    @Override
    public Object calculate(final Object o1, final Object o2) {
        final Number n1 = (o1 instanceof Character c) ? (int) c : (Number) o1;
        final Number n2 = (o2 instanceof Character c) ? (int) c : (Number) o2;

        return switch (NumberType.type(o1)) {
            case BYTE -> n1.byteValue() << n2.intValue();
            case SHORT -> n1.shortValue() << n2.intValue();
            case CHAR, INT -> n1.intValue() << n2.intValue();
            case LONG -> n1.longValue() << n2.intValue();
            default -> throw new IllegalArgumentException("Unsupported operand types: " + o1.getClass() + ", " + o2.getClass());
        };
    }
}