package expression;

public class BitXor extends BinaryOperation {
    public BitXor(ExpressionPriority operand1, ExpressionPriority operand2) {
        super(operand1, operand2, "^");
    }

    @Override
    public int result(int x, int y) {
        return x ^ y;
    }

    @Override
    public double result(double x, double y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Priority getPriority() {
        return Priority.TWO;
    }


    @Override
    public boolean associativity() {
        return true;
    }
}