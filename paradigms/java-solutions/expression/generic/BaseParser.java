package expression.generic;

import expression.generic_exceptions.ParseException;

public class BaseParser {
    public static final char END = '\0';
    private final CharSource source;
    protected char ch = 0xffff;

    public BaseParser(CharSource source) {
        this.source = source;
    }

    protected void nextChar() {
        ch = source.hasNext() ? source.next() : END;
    }

    protected boolean test(char expected) {
        if (ch == expected) {
            nextChar();
            return true;
        }
        return false;
    }

    protected void expect(final char c) throws ParseException {
        if (ch != c) {
            throw error("Expected '" + c + "', found '" + ch + "'");
        }
        nextChar();
    }

    protected void expect(final String value) throws ParseException {
        for (char c : value.toCharArray()) {
            expect(c);
        }
        if (Character.isLetter(ch)) {
            throw error("Illegal character '" + ch + "' after operation");
        }
    }

    protected boolean eof() {
        return test(END);
    }

    protected ParseException error(final String message) {
        return source.error(message);
    }

    protected int getPosition() {
        return source.getPosition();
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }
}

