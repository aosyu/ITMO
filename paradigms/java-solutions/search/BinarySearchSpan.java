package search;

public class BinarySearchSpan {

    // Pred: a.length > 0 && a != null && (for int i = 0; i < a.length - 1    a[i] >= a[i + 1]) && forall i=0..a.len a[i] - int
    // Post: R == (i: (i >= 0 && i < a.length && a[i] <= x && (a[i - 1] > x || i == 0)) ||
    // (i == a.length && a[a.length - 1] > x))
    public static int iterativeSearch(int x, int[] a) {

        // Pred: true
        // Post: left = -1 && right = a.length && a.length > 0
        int left = -1;
        int right = a.length;

        // Pred: left != right - 1 && (left + right >= 0)
        // Post: left == right - 1 && ((right >= 0 && right < a.length && (a[left] > x || right == 0) && a[right] <= x) ||
        // (right == a.length && a[left] > x)) => right == R
        while (left != right - 1) {

            // Pred: left + right >= 0
            // Post: mid >= left && mid < right && mid >= 0 && mid < a.length
            int mid = (left + right) / 2;

            // Pred: mid >= 0 && mid < a.length && mid >= left && mid < right
            // Post: отрезок left...right уменьшился || выход из цикла 
            if (a[mid] <= x) {
                // Pred: a[mid] <= x && mid >= 0 && mid < a.length && mid < right
                // Post: (right = mid && right'>mid -> right уменьшился) && a[right] <= x && right >= 0 && right < a.length 
                right = mid;
            } else {
                // Pred: a[mid] > x && (mid > left || (mid == left && mid < right    ->   mid != right   ->    mid == right - 1)) 
                // Post: left = mid && a[left] > x && (left увеличивается || (left == right - 1 ->  выходим из цикла)
                left = mid;
            }
        }
        return right;
    }


    // Pred: a.length > 0 && left >= -1 && right > 0 && right <= a.length && left < right && a != null && (for int i = 0; i < a.length - 1    a[i] >= a[i + 1]) && forall i=0..a.len a[i] - int
    // Post: R == (i: (i >= 0 && i < a.length && a[i] <= x && a[i + 1] < x) ||
    // (i == a.length && a[a.length - 1] > x))
    public static int recursiveSearch(int x, int[] a, int left, int right) {

        // Pred: left == right - 1 && ((right >= 0 && right < a.length && a[right] <= x && a[right + 1] < x) ||
        // (right == a.length && a[left] > x)) => right == R
        if (left == right - 1) {
            return right;
        }

        // Post: mid >= left && mid < right && mid >= 0 && mid < a.length
        int mid = (left + right) / 2;

        // Pred: mid >= 0 && mid < a.length && mid >= left && mid < right
        if (a[mid] < x) {
            // Pred: a[mid] < x && mid >= 0 && mid < a.length && mid < right
            // Post: (right = mid && right'>mid -> right уменьшился) && a[right] < x && right >= 0 && right < a.length
            return recursiveSearch(x, a, left, mid);
        } else {
            // Pred: a[mid] >= x && (mid > left || (mid == left && mid < right    ->   mid != right   ->    mid == right - 1))
            // Post: left = mid && a[left] >= x && (left увеличивается || (left == right - 1 ->  выходим из рекурсии)
            return recursiveSearch(x, a, mid, right);
        }
    }


    // Pred: args.length > 0 && args != null && for int i = 1; i < args.length - 1    args[i] >= args[i + 1] && forall i=0..args.len args[i] - int
    // Post: R == (l, length): (l >= 0 && l < args.length && args[l] <= x && (args[l - 1] > x || l == 0) && (for int j = l; j < l + length    args[j] == x)) ||
    // (l == args.length && args[args.length - 1] > x && length = 0)
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            a[i - 1] = Integer.parseInt(args[i]);
        }
        // :NOTE: Отсорированоость a?
        int left = iterativeSearch(x, a);
        int length = recursiveSearch(x, a, -1, a.length) - left;
        System.out.println(left + " " + length);
        //System.out.println(iterativeSearch(x, a));
        //System.out.println(recursiveSearch(x, a, -1, a.length));
    }
}
