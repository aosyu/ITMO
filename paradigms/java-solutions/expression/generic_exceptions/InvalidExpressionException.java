package expression.generic_exceptions;

public class InvalidExpressionException extends ParseException {
    public InvalidExpressionException(String message) {
        super(message);
    }
}
