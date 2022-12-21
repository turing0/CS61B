package deque;

public class LinkedListDeque<T> implements Deque<T> {

    private class Node {
        public T value;
        public Node prev, next;

        public Node(T v, Node p, Node n) {
            value = v;
            prev = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null,null,null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel, sentinel.next);
        sentinel.prev = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev.next = new Node(item, sentinel.prev, sentinel);
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
            Node p = sentinel;
            for(int i=0;i<size;i++) {
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
        if (index>=0 && index<size) {
            Node p = sentinel;
            for(int i=0;i<=index;i++) {
                p = p.next;
            }
            return p.value;
        }
        return null;
    }

//    public Iterator<T> iterator() {
//
//    }

    public boolean equals(Object o) {
        if (o instanceof LinkedListDeque && ((LinkedListDeque<?>) o).size == size) {

            return true;
        }

        return false;
    }

}
