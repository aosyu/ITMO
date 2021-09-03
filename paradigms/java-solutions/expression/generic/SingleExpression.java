package expression.generic;

public interface SingleExpression<T> {
    T evaluate(T x);
}
