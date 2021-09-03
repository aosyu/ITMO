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

public class ArrayQueueModule {
    private static int head;
    private static int tail;
    // head ... tail
    private static Object[] elements = new Object[2];

    /*
        Pred: e != null
        Post: a'[1] = e && n = n' + 1 && forall i = 1..n': a'[i + 1] = a[i]
     */
    public static void push(Object e) {
        ensureCapacity();
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = Objects.requireNonNull(e);;
    }

    /*
        Pred: n > 0
        Post: R == a[n] && Immutable
     */
    public static Object peek() {
        assert size() > 0 : "Queue is empty";
        return elements[(tail - 1 + elements.length) % elements.length];
    }

    /*
        Pred: n > 0
        Post: R == a'[n'] && n = n' - 1 && forall i = 1..n: a'[i] = a[i]
     */
    public static Object remove() {
        assert size() > 0 : "Queue is empty";
        tail = (tail - 1 + elements.length) % elements.length;
        return elements[tail];
    }

    /*
        Pred: e != null
        Post: n = n' + 1 && a[n] == e && forall i = 1..n': a[i] == a'[i]
     */
    public static void enqueue(Object e) {
        ensureCapacity();
        elements[tail] = Objects.requireNonNull(e);;
        tail = (tail + 1) % elements.length;
    }

    /*
        Pred: true
        Post: Immutable && R == n
     */
    public static int size() {
        return tail - head + (head > tail ? elements.length : 0);
    }

    /*
        Pred: n > 0
        Post: R == a[1] && Immutable
     */
    public static Object element() {
        assert size() > 0 : "Queue is empty";
        return elements[head];
    }

    /*
        Pred: n > 0
        Post: R == a[1] && forall i = 2..n: a'[i] = a[i] && n = n' - 1
     */
    public static Object dequeue() {
        assert size() > 0 : "Queue is empty";
        Object x = elements[head];
        head = (head + 1) % elements.length;
        return x;
    }

    /*
        Pred: true
        Post: R == (n == 0) && Immutable
     */
    public static boolean isEmpty() {
        return head == tail;
    }

    /*
        Pred: true
        Post: n == 0
     */
    public static void clear() {
        head = 0;
        tail = 0;
    }

    /*
        Pred: true
        Post: n > n' + 1
     */
    private static void ensureCapacity() {
        if (size() + 1 == elements.length) {
            int size = size();
            Object[] temp = new Object[elements.length * 2];
            if (head < tail) {
                System.arraycopy(elements, head, temp, 0, tail - head);
            } else {
                System.arraycopy(elements, head, temp, 0, elements.length - head);
                System.arraycopy(elements, 0, temp, elements.length - head, tail);
            }
            elements = temp;
            tail = size;
            head = 0;
        }
    }
}
