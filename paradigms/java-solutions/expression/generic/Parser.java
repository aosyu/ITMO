package expression.generic;

import expression.generic_exceptions.ParseException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser {
    TripleExpression<?> parse(String expression) throws ParseException;

    interface Test {}
}
