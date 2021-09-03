package expression.generic_exceptions;

public class IllegalVariableNameException extends ParseException {
    public IllegalVariableNameException(int pos, String var) {
        super(pos + ": Illegal variable name: '" + var + "'");
    }
}
