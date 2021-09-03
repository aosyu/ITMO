package queue;

import java.util.Objects;

public class LinkedQueue extends AbstractQueue {
    private Node head;
    private Node tail;

    protected void pushImpl(final Object e) {
        head = new Node(e, head, null);
        if (head.next != null) {
            head.next.prev = head;
        } else {
            tail = head;
        }
    }

    public Object peek() {
        assert size() > 0 : "Queue is empty";
        return tail.value;
    }

    protected Object removeImpl() {
        final Object res = tail.value;
        tail = tail.prev;
        clearIfEmpty();
        return res;
    }

    protected void enqueueImpl(final Object e) {
        tail = new Node(Objects.requireNonNull(e), null, tail);
        if (tail.prev != null) {
            tail.prev.next = tail;
        } else {
            head = tail;
        }
    }

    public Object element() {
        assert size() > 0 : "Queue is empty";
        return head.value;
    }

    protected Object dequeueImpl() {
        final Object res = head.value;
        head = head.next;
        clearIfEmpty();
        return res;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    protected void clearImpl() {
        head = tail = null;
    }

    private void clearIfEmpty() {
        if (isEmpty()) {
            clearImpl();
        }
    }

    private static class Node {
        private final Object value;
        private Node next;
        private Node prev;

        private Node(final Object value, final Node next, final Node prev) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }
}
