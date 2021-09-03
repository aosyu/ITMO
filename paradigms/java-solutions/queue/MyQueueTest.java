package queue;

public class MyQueueTest {
    public static void main(String[] args) {
        Queue queue1 = new LinkedQueue();
        Queue queue2 = new ArrayQueue();

        fill(queue2);
        dump(queue2);

        fill(queue1);
        dump(queue1);
    }

    private static void dump(final Queue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.dequeue());
        }
    }

    private static void fill(final Queue queue) {
        for (int i = 0; i < 7; i++) {
            queue.push(Math.pow(10, i));
        }
        for (int i = 0; i < 7; i++) {
            queue.enqueue(Math.pow(10, i));
        }
    }
}
