package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private final double maxLoad;

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] b = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            b[i] = createBucket();
        }
        return b;
    }

    @Override
    public void clear() {
        buckets = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        Node node = getNode(key);
        if (node == null) return false;
        return true;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        if (node == null) return null;
        return node.value;
    }

    private Node getNode(K key) {
        if (size == 0) return null;
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        Node node = getNode(key);

        if (node == null) { // add
            int index = Math.floorMod(key.hashCode(), buckets.length);
            buckets[index].add(createNode(key, value));
            size += 1;
            if (size / buckets.length > maxLoad) {
                resize(buckets.length * 2);
            }
        } else { // replace
            node.value = value;
        }
    }

    private void resize(int capacity) {

    }

    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<>();
        for (Collection<Node> items : buckets) {
            for (Node node : items) {
                s.add(node.key);
            }
        }
        return s;
    }

    @Override
    public V remove(K key) {
        Node node = getNode(key);
        if (node == null) return null;
        int index = Math.floorMod(key.hashCode(), buckets.length);
        buckets[index].remove(node);
        size -= 1;
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        Node node = getNode(key);
        if (node != null && node.value == value) {
            return remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MHMIterator();
    }

    private class MHMIterator implements Iterator {
        private List<Node> lst;

        public MHMIterator() {
            lst = new ArrayList<>();
            for (Collection<Node> items : buckets) {
                for (Node node : items) {
                    lst.add(node);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return lst.size() != 0;
        }

        @Override
        public Object next() {
            return lst.remove(0);
        }
    }
}
