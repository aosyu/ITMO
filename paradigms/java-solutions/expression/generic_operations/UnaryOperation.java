package expression.generic_operations;

import expression.generic.Expression;

public abstract class UnaryOperation<T> implements Expression<T> {
    protected final Expression<T> expression;
    protected final Operation<T> operations;

    public UnaryOperation(Expression<T> expression, Operation<T> operations) {
        this.expression = expression;
        this.operations = operations;
    }

    protected abstract T result(T x);

    @Override
    public T evaluate(T x) {
        return result(expression.evaluate(x));
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return result(expression.evaluate(x, y, z));
    }
}
