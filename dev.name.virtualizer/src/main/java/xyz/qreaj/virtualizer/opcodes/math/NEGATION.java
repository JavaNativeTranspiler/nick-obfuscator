package xyz.qreaj.virtualizer.opcodes.math;

import xyz.qreaj.virtualizer.opcodes.type.ArithmeticOpcode;
import xyz.qreaj.virtualizer.utils.NumberType;

public class NEGATION extends ArithmeticOpcode {
    public NEGATION() {
        this.inputIs2ArgsElseOne = false;
    }

    @Override
    public Object calculate(final Object o1, final Object o2) {
        final Number n1 = (o1 instanceof Character c) ? (int) c : (Number) o1;

        return switch (NumberType.type(o1)) {
            case BYTE -> -n1.byteValue();
            case SHORT -> -n1.shortValue();
            case CHAR, INT -> -n1.intValue();
            case FLOAT -> -n1.floatValue();
            case LONG -> -n1.longValue();
            case DOUBLE -> -n1.doubleValue();
            default -> throw new IllegalArgumentException("Unsupported operand types: " + o1.getClass() + ", " + o2.getClass());
        };
    }
}