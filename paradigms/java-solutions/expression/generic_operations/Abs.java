package expression.generic_operations;

import expression.generic.Expression;

public class Abs<T> extends UnaryOperation<T> {
    public Abs(Expression<T> operand, Operation<T> operations) {
        super(operand, operations);
    }

    @Override
    public T result(T x) {
        return operations.abs(x);
    }
}