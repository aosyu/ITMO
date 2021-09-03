package expression.generic_operations;

import expression.generic.Expression;

public abstract class BinaryOperation<T> implements Expression<T> {
    protected final Expression<T> operand1;
    protected final Expression<T> operand2;
    protected final Operation<T> operations;

    protected BinaryOperation(Expression<T> operand1, Expression<T> operand2, Operation<T> operations) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operations = operations;
    }

    protected abstract T result(T x, T y);

    @Override
    public T evaluate(T x, T y, T z) {
        return result(operand1.evaluate(x, y, z), operand2.evaluate(x, y, z));
    }

    @Override
    public T evaluate(T x) {
        return result(operand1.evaluate(x), operand2.evaluate(x));
    }
}
    