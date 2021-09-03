package expression.generic_operations;

import expression.generic.Expression;

public class GenericConst<T> implements Expression<T> {
    private final T constValue;

    public GenericConst(T value) {
        constValue = value;
    }

    @Override
    public T evaluate(T x) {
        return constValue;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return constValue;
    }
}
