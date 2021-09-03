package expression;

public interface Result extends ExpressionPriority {
    int result(int x, int y);
    double result(double x, double y);
}
