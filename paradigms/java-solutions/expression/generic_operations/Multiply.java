package expression.generic_operations;

import expression.generic.Expression;

public class Multiply<T> extends BinaryOperation<T> {
    public Multiply(Expression<T> operand1, Expression<T> operand2, Operation<T> operations) {
        super(operand1, operand2, operations);
    }

    @Override
    public T result(T x, T y) {
        return operations.multiply(x, y);
    }
}