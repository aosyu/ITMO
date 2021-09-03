package expression.generic_operations;

public class DoubleOperations implements Operation<Double> {
    @Override
    public Double min(Double op1, Double op2) {
        return Double.min(op1, op2);
    }

    @Override
    public Double max(Double op1, Double op2) {
        return Double.max(op1, op2);
    }

    @Override
    public Double add(Double op1, Double op2) {
        return op1 + op2;
    }

    @Override
    public Double subtract(Double op1, Double op2) {
        return op1 - op2;
    }

    @Override
    public Double divide(Double op1, Double op2) {
        return op1 / op2;
    }

    @Override
    public Double multiply(Double op1, Double op2) {
        return op1 * op2;
    }

    @Override
    public Double negate(Double op1) {
        return -op1;
    }

    @Override
    public Double parseValue(String v) {
        return Double.parseDouble(v);
    }

    @Override
    public Double abs(Double op) {
        return Math.abs(op);
    }

    @Override
    public Double square(Double op) {
        return op * op;
    }

    @Override
    public Double mod(Double op1, Double op2) {
        return op1 % op2;
    }
}
