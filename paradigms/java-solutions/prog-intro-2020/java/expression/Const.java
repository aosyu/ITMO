package expression;

public class Const implements ExpressionPriority, DoubleExpression, TripleExpression {
    private final double x;

    public Const(int x) {
        this.x = x;
    }

    public Const(double x) {
        this.x = x;
    }

    @Override
    public String toString() {
        if (x == (int) x) {
            return Integer.toString((int) x);
        }
        return Double.toString(x);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (int) this.x;
    }


    @Override
    public int evaluate(int x) {
        return (int) this.x;
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
    public int hashCode() {
        return (int) this.x;
    }

    @Override
    public boolean equals(Object y) {
        if (y == null || y.getClass() != Const.class) {
            return false;
        }
        return this.toString().equals(y.toString());
    }

    @Override
    public double evaluate(double x) {
        return this.x;
    }
}
