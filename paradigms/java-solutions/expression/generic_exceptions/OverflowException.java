package expression.generic_exceptions;

public class OverflowException extends EvaluatingException {
    public OverflowException(String message) {
        super(message);
    }
}
