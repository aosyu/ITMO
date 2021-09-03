package expression.generic_operations;

import expression.generic.Expression;

public class Square<T> extends UnaryOperation<T> {
    public Square(Expression<T> operand, Operation<T> operations) {
        super(operand, operations);
    }

    @Override
    public T result(T x) {
        return operations.square(x);
    }
}