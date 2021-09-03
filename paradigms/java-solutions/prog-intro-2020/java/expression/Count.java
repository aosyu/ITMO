package expression;

public class Count implements ExpressionPriority {
    private final ExpressionPriority expression;

    public Count(ExpressionPriority expression) {
        this.expression = expression;
    }

    @Override
    public Priority getPriority() {
        return Priority.SIX;
    }

    @Override
    public boolean associativity() {
        return false;
    }

    @Override
    public double evaluate(double x) {
        int ans = 0;
        String res = Integer.toBinaryString((int) x);
        for (int i = 0; i < res.length(); i++) {
            if (res.charAt(i) == '1') {
                ans++;
            }
        }
        return ans;
    }

    @Override
    public int evaluate(int x) {
        int ans = 0;
        String res = Integer.toBinaryString(x);
        for (int i = 0; i < res.length(); i++) {
            if (res.charAt(i) == '1') {
                ans++;
            }
        }
        return ans;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int temp = expression.evaluate(x, y, z);
        String res = Integer.toBinaryString(temp);
        int ans = 0;
        for (int i = 0; i < res.length(); i++) {
            if (res.charAt(i) == '1') {
                ans++;
            }
        }
        return ans;
    }
}

