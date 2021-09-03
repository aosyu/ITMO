package expression.generic;

import expression.generic_exceptions.ParseException;

public interface CharSource {
    boolean hasNext();
    char next();
    ParseException error(final String message);
    int getPosition();
}

