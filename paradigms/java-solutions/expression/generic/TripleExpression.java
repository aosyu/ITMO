package expression.generic;

public interface TripleExpression<T> {
    T evaluate(T x, T y, T z);
}