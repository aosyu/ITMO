package expression;

public class Add extends BinaryOperation {

    public Add(ExpressionPriority operand1, ExpressionPriority operand2) {
        super(operand1, operand2, "+");
    }

    @Override
    public int result(int x, int y) {
        return x + y;
    }

    @Override
    public double result(double x, double y) {
        return x + y;
    }

    @Override
    public Priority getPriority() {
        return Priority.FOUR;
    }

    @Override
    public boolean associativity() {
        return false;
    }

}
