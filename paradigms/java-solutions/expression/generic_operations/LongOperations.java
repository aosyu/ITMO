package expression.generic_operations;

import expression.generic_exceptions.DivisionByZeroException;

public class LongOperations implements Operation<Long> {
    @Override
    public Long min(Long op1, Long op2) {
        return Long.min(op1, op2);
    }

    @Override
    public Long max(Long op1, Long op2) {
        return Long.max(op1, op2);
    }

    @Override
    public Long add(Long op1, Long op2) {
        return op1 + op2;
    }

    @Override
    public Long subtract(Long op1, Long op2) {
        return op1 - op2;
    }

    @Override
    public Long divide(Long op1, Long op2) {
        if (op2 == 0) {
                throw new DivisionByZeroException("Division by zero: " + op1 + "/" + op2);
            }
        return op1 / op2;
    }

    @Override
    public Long multiply(Long op1, Long op2) {
        return op1 * op2;
    }

    @Override
    public Long negate(Long op1) {
        return -op1;
    }

    @Override
    public Long parseValue(String v) {
        return Long.parseLong(v);
    }

    @Override
    public Long abs(Long op) {
        return Math.abs(op);
    }

    @Override
    public Long square(Long op) {
        return op * op;
    }

    @Override
    public Long mod(Long op1, Long op2) {
        if (op2 == 0) {
                throw new DivisionByZeroException("Mod by zero");
            }
        return op1 % op2;
    }
}
