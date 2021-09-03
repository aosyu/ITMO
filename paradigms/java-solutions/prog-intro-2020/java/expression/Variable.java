package expression;

public class Variable implements ExpressionPriority, DoubleExpression, TripleExpression {
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (var) {
            case "x":
                return x;
            case "y":
                return y;
            default:
                return z;
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.SEVEN;
    }

    @Override
    public boolean associativity() {
        return false;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public int hashCode() {
        return var.hashCode();
    }

    @Override
    public boolean equals(Object y) {
        if (y == null || y.getClass() != Variable.class) {
            return false;
        }
        return var.equals(y.toString());
    }

    @Override
    public double evaluate(double x) {
        return x;
    }
}
