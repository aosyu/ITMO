package expression.generic;

import expression.generic_exceptions.EvaluatingException;
import expression.generic_exceptions.InvalidModeException;
import expression.generic_exceptions.ParseException;
import expression.generic_exceptions.TabulateException;
import expression.generic_operations.*;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final Map<String, Operation<?>> modes = Map.of(
            "i", new IntegerOperations(true),
            "u", new IntegerOperations(false),
            "l", new LongOperations(),
            "d", new DoubleOperations(),
            "bi", new BigIntegerOperations(),
            "s", new ShortOperations()
    );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws TabulateException {
        if (modes.containsKey(mode)) {
            return makeTable(modes.get(mode), expression, x1, x2, y1, y2, z1, z2);
        } else {
            throw new InvalidModeException("Invalid mode: '" + mode + "'");
        }
    }

    private <T> Object[][][] makeTable(Operation<T> mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        ExpressionParser<T> parser = new ExpressionParser<>(mode);
        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        TripleExpression<T> tripleExpression;

        try {
            tripleExpression = parser.parse(expression);
        } catch (ParseException e) {
            return null;
        }

        for (int i = x1; i < x2 + 1; i++) {
            for (int j = y1; j < y2 + 1; j++) {
                for (int k = z1; k < z2 + 1; k++) {
                    T x = mode.parseValue(Integer.toString(i));
                    T y = mode.parseValue(Integer.toString(j));
                    T z = mode.parseValue(Integer.toString(k));
                    try {
                        table[i - x1][j - y1][k - z1] = tripleExpression.evaluate(x, y, z);
                    } catch (EvaluatingException e) {
                        table[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }
        return table;
    }

    public static void main(String[] args) {
        String mode = args[0];
        if (mode.charAt(0) != '-') {
            System.out.println("Invalid mode: " + mode);
            return;
        }
        String expression = args[1];

        GenericTabulator tabulator = new GenericTabulator();
        try {
            Object[][][] table = tabulator.tabulate(mode.substring(1), expression, -2, 2, -2, 2, -2, 2);
            for (Object[][] objects : table) {
                for (Object[] object : objects) {
                    for (Object o : object) {
                        System.out.print(o + " ");
                    }
                    System.out.println();
                }
                System.out.println();
            }
        } catch (TabulateException e) {
            System.out.println(e.getMessage());
        }
    }
}
