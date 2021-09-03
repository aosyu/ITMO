package expression;

public class BitMinus implements ExpressionPriority {
    private final ExpressionPriority expression;

    public BitMinus(ExpressionPriority expression) {
        this.expression = expression;
    }

    @Override
    public Priority getPriority() {
        return Priority.SIX;
    }

    @Override
    public boolean associativity() {
        return false;
    }

    @Override
    public double evaluate(double x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int evaluate(int x) {
        return ~expression.evaluate(x);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return ~expression.evaluate(x, y, z);
    }
}

