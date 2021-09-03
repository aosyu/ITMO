package expression.generic_operations;

import expression.generic_exceptions.DivisionByZeroException;

public class ShortOperations implements Operation<Short> {
    @Override
    public Short min(Short op1, Short op2) {
        return (short) Integer.min(op1, op2);
    }

    @Override
    public Short max(Short op1, Short op2) {
        return (short) Integer.max(op1, op2);
    }

    @Override
    public Short add(Short op1, Short op2) {
        return (short) (op1 + op2);
    }

    @Override
    public Short subtract(Short op1, Short op2) {
        return (short) (op1 - op2);
    }

    @Override
    public Short divide(Short op1, Short op2) {
        if (op2 == 0) {
                throw new DivisionByZeroException("Division by zero: " + op1 + "/" + op2);
            }
        return (short) (op1 / op2);
    }

    @Override
    public Short multiply(Short op1, Short op2) {
        return (short) (op1 * op2);
    }

    @Override
    public Short negate(Short op1) {
        return (short) -op1;
    }

    @Override
    public Short parseValue(String v) {
        return (short) Integer.parseInt(v);
    }

    @Override
    public Short abs(Short op) {
        return (short) Math.abs(op);
    }

    @Override
    public Short square(Short op) {
        return (short) (op * op);
    }

    @Override
    public Short mod(Short op1, Short op2) {
        if (op2 == 0) {
            throw new DivisionByZeroException("Mod by zero");
        }
        return (short) (op1 % op2);
    }
}
