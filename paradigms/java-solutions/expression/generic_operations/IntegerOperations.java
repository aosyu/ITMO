package expression.generic_operations;

import expression.generic_exceptions.DivisionByZeroException;
import expression.generic_exceptions.OverflowException;

public class IntegerOperations implements Operation<Integer> {
    private final boolean checkNeeded;

    public IntegerOperations(boolean checkNeeded) {
        this.checkNeeded = checkNeeded;
    }

    @Override
    public Integer min(Integer op1, Integer op2) {
        return Math.min(op1, op2);
    }

    @Override
    public Integer max(Integer op1, Integer op2) {
        return Math.max(op1, op2);
    }

    @Override
    public Integer add(Integer op1, Integer op2) {
        if (checkNeeded && ((op2 > 0 && op1 > Integer.MAX_VALUE - op2) || (op2 < 0 && op1 < Integer.MIN_VALUE - op2))) {
            throw new OverflowException("Overflow during addition: " + op1 + " + " + op2);
        }
        return op1 + op2;
    }

    @Override
    public Integer subtract(Integer op1, Integer op2) {
        if (checkNeeded && ((op2 < 0 && op1 > Integer.MAX_VALUE + op2) || (op2 > 0 && op1 < Integer.MIN_VALUE + op2))) {
            throw new OverflowException("Subtraction overflow: " + op1 + "-" + op2);
        }
        return op1 - op2;
    }

    @Override
    public Integer divide(Integer op1, Integer op2) {
        if (op1 == Integer.MIN_VALUE && op2 == -1) {
            throw new OverflowException("Overflow during division: " + op1 + " / " + op2);
        }
        if (op2 == 0) {
            throw new DivisionByZeroException("Division by zero");
        }
        return op1 / op2;
    }

    @Override
    public Integer multiply(Integer op1, Integer op2) {
        int maxValue = Integer.signum(op1) == Integer.signum(op2) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (checkNeeded && (op1 != 0 && op1 != -1
                && ((op2 > 0 && op2 > maxValue / op1) || (op2 < 0 && op2 < maxValue / op1))
                || (op1 == -1 && op2 == Integer.MIN_VALUE))) {
            throw new OverflowException("Overflow during multiplication: " + op1 + " * " + op2);
        }
        return op1 * op2;
    }

    @Override
    public Integer negate(Integer op1) {
        if (checkNeeded && (op1 == Integer.MIN_VALUE)) {
                throw new OverflowException("Negation overflow: " + op1);
            }
        return -op1;
    }

    @Override
    public Integer parseValue(String v) {
        return Integer.parseInt(v);
    }

    @Override
    public Integer abs(Integer op) {
        return Math.abs(op);
    }

    @Override
    public Integer square(Integer op) {
        if (checkNeeded && (((op > 0 && op > Integer.MAX_VALUE / op) || (op < 0 && op < Integer.MIN_VALUE / Math.abs(op))))) {
                throw new OverflowException("Overflow during square: " + op);
            }
        return op * op;
    }

    @Override
    public Integer mod(Integer op1, Integer op2) {
        if (op2 == 0) {
            throw new DivisionByZeroException("Mod by zero");
        }
        return op1 % op2;
    }
}
