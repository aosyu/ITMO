package expression.generic_operations;

import expression.generic.Expression;

public class Divide<T> extends BinaryOperation<T> {
    public Divide(Expression<T> operand1, Expression<T> operand2, Operation<T> operations) {
        super(operand1, operand2, operations);
    }

    @Override
    public T result(T x, T y) {
        return operations.divide(x, y);
    }
}
