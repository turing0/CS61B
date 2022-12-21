package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private static class Node<T> {
        private final T value;
        private Node<T> prev, next;

        Node(T v, Node<T> p, Node<T> n) {
            value = v;
            prev = p;
            next = n;
        }
    }

    private final Node<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node<>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        sentinel.next = new Node<>(item, sentinel, sentinel.next);
        sentinel.prev = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev.next = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev = sentinel.prev.next;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size > 0) {
            Node<T> p = sentinel;
            for (int i = 0; i < size; i++) {
                p = p.next;
                System.out.print(p.value + " ");
            }
            System.out.print("\n");
        }
    }

    @Override
    public T removeFirst() {
        if (size > 0) {
            T v = sentinel.next.value;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size -= 1;
            return v;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (size > 0) {
            T v = sentinel.prev.value;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
            return v;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if (index >= 0 && index < size) {
            Node<T> p = sentinel;
            for (int i = 0; i <= index; i++) {
                p = p.next;
            }
            return p.value;
        }
        return null;
    }

    public T getRecursive(int index) {

        return getRecursiveHelper(sentinel.next, index);
    }

    private T getRecursiveHelper(Node<T> cur, int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        if (index == 0) {
            return cur.value;
        }
        return getRecursiveHelper(cur.next, index - 1);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node<T> p;

        LinkedListDequeIterator() {
            p = sentinel.next;
        }

        public boolean hasNext() {
            return p == sentinel;
        }

        public T next() {
            T v = p.value;
            p = p.next;
            return v;
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        LinkedListDeque<?> lo = (LinkedListDeque<?>) o;
        if (lo.size == size) {
            for (int i = 0; i < size; i++) {
                if (lo.get(i) != get(i)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

}
