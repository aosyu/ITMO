package expression.parser;

import expression.*;


public class ExpressionParser {
    public static ExpressionPriority parse(final String expression) {
        return parse(new StringSource(expression));
    }

    public static ExpressionPriority parse(CharSource source) {
        return new TripleExpressionParser(source).parseExpression();
    }


    private static class TripleExpressionParser extends BaseParser {

        public TripleExpressionParser(CharSource source) {
            super(source);
            nextChar();
        }

        public ExpressionPriority parseExpression() {
            final ExpressionPriority result = parseBitOr();
            if (!eof()) {
                throw error("End of expression expected");
            }
            return result;
        }

        private ExpressionPriority parseBitOr() {
            ExpressionPriority result = parseBitXor();
            
            while (true) {
                if (test('|')) {
                    result = new BitOr(result, parseBitXor());
                } else {
                    return result;
                }
            }
        }

        private ExpressionPriority parseBitXor() {
            ExpressionPriority result = parseBitAnd();
             // :NOTE: Лишний
            while (true) {
                if (test('^')) {
                    result = new BitXor(result, parseBitAnd());
                } else {
                    return result;
                }
            }
        }

        private ExpressionPriority parseBitAnd() {
            ExpressionPriority result = parseSubAdd();
           
            while (true) {
                if (test('&')) {
                    result = new BitAnd(result, parseSubAdd());
                } else {
                    return result;
                }
            }
        }

        private ExpressionPriority parseSubAdd() {
            ExpressionPriority result = parseDivMul();
            
            while (true) {
                if (test('+')) {
                    result = new Add(result, parseDivMul());
                } else if (test('-')) {
                    result = new Subtract(result, parseDivMul());
                } else {
                    return result;
                }
            }
        }

        private ExpressionPriority parseDivMul() {
            ExpressionPriority result = parseValue();
            
            while (true) {
                if (test('/')) {
                    result = new Divide(result, parseValue());
                } else if (test('*')) {
                    result = new Multiply(result, parseValue());
                } else {
                    return result;
                }
            }
        }

        private ExpressionPriority parseValue() {
            skipWhitespace();
            ExpressionPriority result;
            if (test('(')) {
                result = parseBitOr();
                nextChar();
            } else if (test('-')) {
                if (Character.isDigit(ch)) {
                    result = parseConst("-");
                } else {
                    result = new UnaryMinus(parseValue());
                }
            } else if (test('~')) {
                if (Character.isDigit(ch)) {
                    result = parseConst("~");
                } else {
                    result = new BitMinus(parseValue());
                }
            } else if (test('c')) {
                expect("ount");
                result = new Count(parseValue());
            } else if (Character.isDigit(ch)) {
                result = parseConst("");
            } else {
                result = new Variable(ch + "");
                nextChar();
            }
            skipWhitespace();
            return result;
        }

        // :NOTE: Исправить
        private ExpressionPriority parseConst(String sign) {
            StringBuilder result = new StringBuilder(sign);
            while (Character.isDigit(ch)) {
                result.append(ch);
                nextChar();
            }
            skipWhitespace();
            return new Const((int) Long.parseLong(result.toString()));
        }
        
        private void skipWhitespace() {
            while (test(' ') || test('\r') || test('\n') || test('\t')) {
                // skip
            }
        }
    }
}

