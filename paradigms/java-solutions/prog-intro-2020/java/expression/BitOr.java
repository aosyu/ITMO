package expression;

public class BitOr extends BinaryOperation {
    public BitOr(ExpressionPriority operand1, ExpressionPriority operand2) {
        super(operand1, operand2, "|");
    }

    @Override
    public int result(int x, int y) {
        return x | y;
    }

    @Override
    public double result(double x, double y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Priority getPriority() {
        return Priority.ONE;
    }


    @Override
    public boolean associativity() {
        return true;
    }
}