package queue;

/*
Model:
    [a1, a2, ... an]
    n -- размер очереди

Inv:
    n >= 0
    forall i = 1..n: a[i] != null

    enqueue – добавить элемент в очередь;
    element – первый элемент в очереди;
    dequeue – удалить и вернуть первый элемент в очереди;
    size – текущий размер очереди;
    isEmpty – является ли очередь пустой;
    clear – удалить все элементы из очереди;


    push – добавить элемент в начало очереди
    peek – вернуть последний элемент в очереди
    remove – вернуть и удалить последний элемент из очереди

Immutable: n == n' && forall i = 1..n: a[i] == a'[i]

Pred: e != null
Post: n = n' + 1 && a[n] == e && forall i = 1..n': a[i] == a'[i]
enqueue(e)

Pred: n > 0
Post: R == a[1] && Immutable
element()

Pred: n > 0
Post: R == a[1] && forall i = 2..n: a'[i] = a[i] && n = n' - 1
dequeue()

Pred: true
Post: Immutable && R == n
size()

Pred: true
Post: R == (n == 0) && Immutable
isEmpty()

Pred: true
Post: n == 0
clear()


Pred: e != null
Post: a'[1] = e && n = n' + 1 && forall i = 1..n': a'[i + 1] = a[i]
push(e)

Pred: n > 0
Post: R == a[n] && Immutable
peek()

Pred: n > 0
Post: R == a'[n'] && n = n' - 1 && forall i = 1..n: a'[i] = a[i]
remove()
 */

import java.util.Objects;

public class ArrayQueueADT {
    private int head;
    private int tail;
    // head ... tail
    private Object[] elements = new Object[2];

    /*
        Pred: true
        Post: n == 0 && R новый
     */
    static ArrayQueueADT create() {
        return new ArrayQueueADT();
    }

    /*
        Pred: e != null && queue != null
        Post: a'[1] = e && n = n' + 1 && forall i = 1..n': a'[i + 1] = a[i]
     */
    public static void push(ArrayQueueADT queue, Object e) {
        ensureCapacity(queue);
        queue.head = (queue.head - 1 + queue.elements.length) % queue.elements.length;
        queue.elements[queue.head] = Objects.requireNonNull(e);;
    }

    /*
        Pred: n > 0 && queue != null
        Post: R == a[n] && Immutable
     */
    public static Object peek(ArrayQueueADT queue) {
        assert size(queue) > 0 : "Queue is empty";
        return queue.elements[(queue.tail - 1 + queue.elements.length) % queue.elements.length];
    }

    /*
        Pred: n > 0 && queue != null
        Post: R == a'[n'] && n = n' - 1 && forall i = 1..n: a'[i] = a[i]
     */
    public static Object remove(ArrayQueueADT queue) {
        assert size(queue) > 0 : "Queue is empty";
        queue.tail = (queue.tail - 1 + queue.elements.length) % queue.elements.length;
        return queue.elements[queue.tail];
    }

    /*
        Pred: e != null && queue != null
        Post: n = n' + 1 && a[n] == e && forall i = 1..n': a[i] == a'[i]
     */
    public static void enqueue(ArrayQueueADT queue, Object e) {
        ensureCapacity(queue);
        queue.elements[queue.tail] = Objects.requireNonNull(e);;
        queue.tail = (queue.tail + 1) % queue.elements.length;
    }

    /*
        Pred: queue != null
        Post: Immutable && R == n
     */
    public static int size(ArrayQueueADT queue) {
        return queue.tail - queue.head + (queue.head > queue.tail ? queue.elements.length : 0);
    }

    /*
        Pred: n > 0 && queue != null
        Post: R == a[1] && Immutable
     */
    public static Object element(ArrayQueueADT queue) {
        assert size(queue) > 0 : "Queue is empty";
        return queue.elements[queue.head];
    }

    /*
        Pred: n > 0 && queue != null
        Post: R == a[1] && forall i = 2..n: a'[i] = a[i] && n = n' - 1
     */
    public static Object dequeue(ArrayQueueADT queue) {
        assert size(queue) > 0 : "Queue is empty";
        Object x = queue.elements[queue.head];
        queue.head = (queue.head + 1) % queue.elements.length;
        return x;
    }

    /*
        Pred: queue != null
        Post: R == (n == 0) && Immutable
     */
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.head == queue.tail;
    }

    /*
        Pred: queue != null
        Post: n == 0
     */
    public static void clear(ArrayQueueADT queue) {
        queue.head = 0;
        queue.tail = 0;
    }

    /*
        Pred: queue != null
        Post: n > n' + 1
     */
    private static void ensureCapacity(ArrayQueueADT queue) {
        if (size(queue) + 1 == queue.elements.length) {
            int size = size(queue);
            Object[] temp = new Object[queue.elements.length * 2];
            if (queue.head < queue.tail) {
                System.arraycopy(queue.elements, queue.head, temp, 0, queue.tail - queue.head);
            } else {
                System.arraycopy(queue.elements, queue.head, temp, 0, queue.elements.length - queue.head);
                System.arraycopy(queue.elements, 0, temp, queue.elements.length - queue.head, queue.tail);
            }
            queue.elements = temp;
            queue.tail = size;
            queue.head = 0;
        }
    }
}
