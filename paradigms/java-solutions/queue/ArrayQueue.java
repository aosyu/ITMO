package queue;

import java.util.Objects;

public class ArrayQueue extends AbstractQueue {
    private int head;
    private int tail;
    private Object[] elements = new Object[2];

    protected void pushImpl(final Object e) {
        ensureCapacity();
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = e;
    }

    public Object peek() {
        assert size() > 0 : "Queue is empty";
        return elements[(tail - 1 + elements.length) % elements.length];
    }

    protected Object removeImpl() {
        tail = (tail - 1 + elements.length) % elements.length;
        return elements[tail];
    }

    protected void enqueueImpl(final Object e) {
        ensureCapacity();
        elements[tail] = Objects.requireNonNull(e);
        tail = (tail + 1) % elements.length;
    }

    public Object element() {
        assert size() > 0 : "Queue is empty";
        return elements[head];
    }

    protected Object dequeueImpl() {
        final Object x = elements[head];
        head = (head + 1) % elements.length;
        return x;
    }

    protected void clearImpl() {
        elements = new Object[2];
        head = tail = 0;
    }

    private void ensureCapacity() {
        if (size() == elements.length) {
            final int s = size();
            final Object[] temp = new Object[elements.length * 2];
            if (head < tail) {
                System.arraycopy(elements, head, temp, 0, tail - head);
            } else {
                System.arraycopy(elements, head, temp, 0, elements.length - head);
                System.arraycopy(elements, 0, temp, elements.length - head, tail);
            }
            elements = temp;
            tail = s - 1;
            head = 0;
        }
    }
}
