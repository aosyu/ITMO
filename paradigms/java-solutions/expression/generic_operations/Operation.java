package expression.generic_operations;

public interface Operation<T> {
    T min(T op1, T op2);
    T max(T op1, T op2);
    T add(T op1, T op2);
    T subtract(T op1, T op2);
    T divide(T op1, T op2);
    T multiply(T op1, T op2);
    T negate(T op1);
    T parseValue(String v);
    T abs(T op);
    T square(T op);
    T mod(T op1, T op2);
}
