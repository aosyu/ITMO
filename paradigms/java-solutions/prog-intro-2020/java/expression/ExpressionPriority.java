package expression;

public interface ExpressionPriority extends Expression, DoubleExpression, TripleExpression {
    Priority getPriority();
    boolean associativity();
}
