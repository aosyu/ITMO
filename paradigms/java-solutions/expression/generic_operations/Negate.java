package expression.generic_operations;

import expression.generic.Expression;

public class Negate<T> extends UnaryOperation<T> {
    public Negate(Expression<T> operand, Operation<T> operations) {
        super(operand, operations);
    }

    @Override
    public T result(T x) {
        return operations.negate(x);
    }
}
