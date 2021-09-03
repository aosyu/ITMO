package queue;

import java.util.Objects;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    @Override
    public void push(final Object e) {
        Objects.requireNonNull(e);
        size++;
        pushImpl(e);
    }

    protected abstract void pushImpl(final Object e);

    @Override
    public abstract Object peek();

    @Override
    public Object remove() {
        assert size() > 0 : "Queue is empty";
        size--;
        return removeImpl();
    }

    protected abstract Object removeImpl();

    @Override
    public void enqueue(final Object e) {
        size++;
        enqueueImpl(e);
    }

    protected abstract void enqueueImpl(final Object e);

    @Override
    public abstract Object element();

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Object dequeue() {
        assert size() > 0 : "Queue is empty";
        size--;
        return dequeueImpl();
    }

    protected abstract Object dequeueImpl();

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0;
        clearImpl();
    }

    protected abstract void clearImpl();

    @Override
    public boolean contains(final Object e) {
        return findFirstOccurrence(e, false);
    }

    @Override
    public boolean removeFirstOccurrence(final Object e) {
        return findFirstOccurrence(e, true);
    }

    private boolean findFirstOccurrence(final Object e, final boolean needToRemove) {
        assert e != null : "Element can not be null";
        boolean found = false;
        final int currentSize = size();
        for (int i = 0; i < currentSize; i++) {
            final Object currentElement = dequeue();
            if (!found && currentElement.equals(e)) {
                found = true;
                if (needToRemove) {
                    continue;
                }
            }
            enqueue(currentElement);
        }
        return found;
    }
}
