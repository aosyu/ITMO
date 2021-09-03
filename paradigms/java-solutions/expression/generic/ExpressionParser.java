package expression.generic;

import expression.generic_exceptions.*;
import expression.generic_operations.*;

import java.util.Set;


public class ExpressionParser<T> implements Parser {
    private final Operation<T> operations;

    public ExpressionParser(Operation<T> mode) {
        this.operations = mode;
    }

    @Override
    public Expression<T> parse(final String expression) throws ParseException {
        return parse(new StringSource(expression));
    }

    public Expression<T> parse(CharSource source) throws ParseException {
        return new TripleExpressionParser<T>(source, operations).parseExpression();
    }


    private static class TripleExpressionParser<T> extends BaseParser {
        private final Operation<T> operations;

        private final Set<String> variables = Set.of(
                "x",
                "y",
                "z"
        );

        private final Set<Character> allowedChars = Set.of(
                ')',
                '(',
                '\0',
                '+',
                '-',
                '*',
                '/'
        );

        public TripleExpressionParser(CharSource source, Operation<T> operations) {
            super(source);
            this.operations = operations;
            nextChar();
        }

        public Expression<T> parseExpression() throws ParseException {
            final Expression<T> result = parseMinMax();
            if (!eof()) {
                throw error("End of expression expected");
            }
            return result;
        }

        private void operationChecker() throws ParseException {
            if (Character.isLetter(ch) || Character.isDigit(ch)) {
                throw new InvalidExpressionException(String.format("%s: %s '%s' %s", getPosition(), "Illegal character", ch, "after operation"));
            }
        }

        private Expression<T> parseMinMax() throws ParseException {
            Expression<T> result = parseSubAdd();
            while (true) {
                String operation = parseVar();
                if (operation.equals("min")) {
                    operationChecker();
                    result = new Min<>(result, parseSubAdd(), operations);
                } else if (operation.equals("max")) {
                    operationChecker();
                    result = new Max<>(result, parseSubAdd(), operations);
                } else if (operation.isEmpty()) {
                    return result;
                } else {
                    throw new InvalidExpressionException(String.format("%s: %s '%s'", getPosition(), "Unexpected letter combination", operation));
                }
            }
        }

        private Expression<T> parseSubAdd() throws ParseException {
            Expression<T> result = parseDivMulMod();
            while (true) {
                if (test('+')) {
                    result = new Add<>(result, parseDivMulMod(), operations);
                } else if (test('-')) {
                    result = new Subtract<>(result, parseDivMulMod(), operations);
                } else {
                    return result;
                }
            }
        }

        private Expression<T> parseDivMulMod() throws ParseException {
            Expression<T> result = parseValue();

            while (true) {
                if (test('/')) {
                    result = new Divide<>(result, parseValue(), operations);
                } else if (test('*')) {
                    result = new Multiply<>(result, parseValue(), operations);
                } else if (test('m')) {
                    expect("od");
                    result = new Mod<>(result, parseValue(), operations);
                } else {
                    return result;
                }
            }
        }

        private Expression<T> parseValue() throws ParseException {
            skipWhitespaces();
            Expression<T> result;
            if (test('(')) {
                result = parseMinMax();
                if (!test(')')) {
                    throw new UnclosedParenthesesException(getPosition());
                }
            } else if (test('-')) {
                if (Character.isDigit(ch)) {
                    result = parseConst("-");
                } else {
                    result = new Negate<>(parseValue(), operations);
                }
            } else if (test('a')) {
                expect("bs");
                result = new Abs<>(parseValue(), operations);
            } else if (test('s')) {
                expect("quare");
                result = new Square<>(parseValue(), operations);
            } else if (Character.isDigit(ch)) {
                result = parseConst("");
            } else if (Character.isLetter(ch)) {
                String var = parseVar();
                if (!variables.contains(var) || (!Character.isWhitespace(ch) && !allowedChars.contains(ch))) {
                    throw new IllegalVariableNameException(getPosition(), var);
                }
                result = new GenericVariable<>(var);
            } else {
                throw new InvalidExpressionException(getPosition() + ": " + (!eof() ? "Unexpected character '" + ch + "'" : "Unfinished expression"));
            }
            skipWhitespaces();
            return result;
        }

        private GenericConst<T> parseConst(String sign) throws IllegalConstArgumentException {
            StringBuilder result = new StringBuilder(sign);
            while (Character.isDigit(ch)) {
                result.append(ch);
                nextChar();
            }
            skipWhitespaces();
            try {
                return new GenericConst<>(operations.parseValue(result.toString()));
            } catch (NumberFormatException e) {
                throw new IllegalConstArgumentException(getPosition(), result.toString());
            }
        }

        private String parseVar() {
            skipWhitespaces();
            StringBuilder var = new StringBuilder();
            while (Character.isLetter(ch)) {
                var.append(ch);
                nextChar();
            }
            return var.toString();
        }


        private void skipWhitespaces() {
            while (test(' ') || test('\r') || test('\n') || test('\t')) {
                // skip
            }
        }
    }
}

