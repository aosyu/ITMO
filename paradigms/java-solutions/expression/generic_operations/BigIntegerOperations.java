package expression.generic_operations;

import expression.generic_exceptions.DivisionByZeroException;
import expression.generic_exceptions.ModByZeroException;

import java.math.BigInteger;

public class BigIntegerOperations implements Operation<BigInteger> {
    @Override
    public BigInteger min(BigInteger op1, BigInteger op2) {
        return op1.compareTo(op2) < 0 ? op1 : op2;
    }

    @Override
    public BigInteger max(BigInteger op1, BigInteger op2) {
        return op1.compareTo(op2) > 0 ? op1 : op2;
    }

    @Override
    public BigInteger add(BigInteger op1, BigInteger op2) {
        return op1.add(op2);
    }

    @Override
    public BigInteger subtract(BigInteger op1, BigInteger op2) {
        return op1.subtract(op2);
    }

    @Override
    public BigInteger divide(BigInteger op1, BigInteger op2) {
        if (op2.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException("Division by zero: " + op1 + "/" + op2);
        }
        return op1.divide(op2);
    }

    @Override
    public BigInteger multiply(BigInteger op1, BigInteger op2) {
        return op1.multiply(op2);
    }

    @Override
    public BigInteger negate(BigInteger op1) {
        return op1.negate();
    }

    @Override
    public BigInteger parseValue(String v) {
        return new BigInteger(v);
    }

    @Override
    public BigInteger abs(BigInteger op) {
        return op.compareTo(BigInteger.ZERO) > 0 ? op : op.negate();
    }

    @Override
    public BigInteger square(BigInteger op) {
        return op.multiply(op);
    }

    @Override
    public BigInteger mod(BigInteger op1, BigInteger op2) {
        if (op2.compareTo(BigInteger.ZERO) < 0) {
            throw new ModByZeroException("Operand 2 should be positive");
        }
        if (op2.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException("Mod by zero");
        }
        return op1.mod(op2);
    }
}
