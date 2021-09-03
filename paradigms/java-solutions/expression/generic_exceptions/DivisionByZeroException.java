package expression.generic_exceptions;

public class DivisionByZeroException extends EvaluatingException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
