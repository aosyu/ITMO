package expression.generic_exceptions;

public class UnclosedParenthesesException extends ParseException {
    public UnclosedParenthesesException(int pos) {
        super(pos + ": No closing parenthesis");
    }
}
