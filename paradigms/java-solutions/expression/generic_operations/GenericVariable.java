package expression.generic_operations;

import expression.generic.Expression;

public class GenericVariable<T> implements Expression<T> {
    private final String var;

    public GenericVariable(String var) {
        this.var = var;
    }

    @Override
    public T evaluate(T x) {
        return x;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        switch (var) {
            case "x":
                return x;
            case "y":
                return y;
            default:
                return z;
        }
    }
}
