package expression.generic_exceptions;

public class IllegalConstArgumentException extends ParseException {
    public IllegalConstArgumentException(int pos, String result) {
        super(pos + ": Constant overflow: " + result);
    }
}
