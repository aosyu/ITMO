package expression.generic_exceptions;

public class ModByZeroException extends EvaluatingException {
    public ModByZeroException(String message) {
        super(message);
    }
}
