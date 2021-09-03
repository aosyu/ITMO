package expression;

public abstract class BinaryOperation implements Result, DoubleExpression, TripleExpression {
    protected final ExpressionPriority operand1;
    protected final ExpressionPriority operand2;
    protected final String expressionSign;

    protected BinaryOperation(ExpressionPriority operand1, ExpressionPriority operand2, String expressionSign) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.expressionSign = expressionSign;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", operand1, expressionSign, operand2);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return result(operand1.evaluate(x, y, z), operand2.evaluate(x, y, z));
    }

    @Override
    public int evaluate(int x) {
        return result(operand1.evaluate(x), operand2.evaluate(x));
    }

    @Override
    public double evaluate(double x) {
        return result(operand1.evaluate(x), operand2.evaluate(x));
    }

    @Override
    public String toMiniString() {
        if (operand1.getPriority().compareTo(this.getPriority()) >= 0) {
            if (needSpecialBrackets()) {
                return String.format("%s %s (%s)", operand1.toMiniString(), expressionSign, operand2.toMiniString());
          }
            return String.format("%s %s %s", operand1.toMiniString(), expressionSign, operand2.toMiniString());
       } else {
            if (needSpecialBrackets()) {
                return String.format("(%s) %s (%s)", operand1.toMiniString(), expressionSign, operand2.toMiniString());
            }
            return String.format("(%s) %s %s", operand1.toMiniString(), expressionSign, operand2.toMiniString());
        }
    }

    private boolean needSpecialBrackets() {
        return this.getPriority() == operand2.getPriority() && (this.associativity() || operand2.associativity())
                || operand2.getPriority().compareTo(this.getPriority()) < 0;
    }


    @Override
    public boolean equals(Object y) {
        if (y == null || y.getClass() != this.getClass()) {
            return false;
        }
        BinaryOperation y1 = (BinaryOperation) y;
        return operand1.equals(y1.operand1) && operand2.equals(y1.operand2) && expressionSign.equals(y1.expressionSign);
    }


    @Override
    public int hashCode() {
        return 31 * (31 * (31 * operand1.hashCode() + 31) + operand2.hashCode()) + this.getClass().hashCode();
    }

}
