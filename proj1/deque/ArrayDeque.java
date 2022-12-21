package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    private T[] array;
    private int nextFirst, nextLast;
    private int size, capacity;

    public ArrayDeque() {
        array = (T[]) new Object[8];
        capacity = 8;
        nextFirst = 8 / 2;
        nextLast = nextFirst + 1;
        size = 0;
    }

    private void resize(int cap) {
        T[] newArray = (T[]) new Object[cap];
        for (int i = 0; i < size; i++) {
            newArray[i] = get(i);
        }
        array = newArray;
        nextFirst = cap - 1;
        nextLast = size;
        capacity = cap;

    }

    @Override
    public void addFirst(T item) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        array[nextFirst] = item;
        nextFirst = (nextFirst + capacity - 1) % capacity;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        array[nextLast] = item;
        nextLast = (nextLast + 1) % capacity;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                T v = get(i);
                System.out.print(v + " ");
            }
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size > 0) {
            int i = (nextFirst + 1) % capacity;
            T v = array[i];
            nextFirst = i;
            size -= 1;
            return v;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (size > 0) {
            int i = (nextLast + capacity - 1) % capacity;
            T v = array[i];
            nextLast = i;
            size -= 1;
            return v;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if (size > 0 && index < size) {
            return array[(nextFirst + 1 + index) % capacity];
        }
        return null;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int p;

        ArrayDequeIterator() {
            p = 0;
        }

        public boolean hasNext() {
            return p < size;
        }

        public T next() {
            T v = get(p);
            p += 1;
            return v;
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<?> ao = (ArrayDeque<?>) o;
        if (ao.size == size) {
            for (int i = 0; i < size; i++) {
                if (get(i) != ao.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


}
