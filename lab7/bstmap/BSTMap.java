package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private Node root;
    private int size;

    private class Node {
        private K key;
        private V value;
        private Node left;
        private Node right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return containsKeyHelper(root, key);
    }
    private boolean containsKeyHelper(Node node, K key) {
        if (find(root, key) != null) {
            return true;
        }
        return false;
    }
    private Node find(Node node, K key) {
        if (node == null) {
            return null;
        }
        if (key.compareTo(node.key) < 0) {
            return find(node.left, key);
        }
        if (key.compareTo(node.key) > 0) {
            return find(node.right, key);
        }
        return node;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        Node node = find(root, key);
        if (node != null) {
            return node.value;
        }
        return null;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
    }
    private Node putHelper(Node node, K key, V value) {
        if (node == null) {
            size += 1;
            return new Node(key, value);
        }
        if (key.compareTo(node.key) < 0) {
            node.left = putHelper(node.left, key, value);
        }
        if (key.compareTo(node.key) > 0) {
            node.right = putHelper(node.right, key, value);
        }
        return node;
    }

    public void printInOrder() {
        if (root != null) {
            printHelper(root);
        }
    }
    private void printHelper(Node node) {
        if (node.left != null) {
            printHelper(node.left);
        }
        System.out.println(node.value);
        if (node.right != null) {
            printHelper(node.right);
        }
    }

    @Override
    public Set<K> keySet() {
        return keySetHelper(root);
    }
    private Set<K> keySetHelper(Node node) {
        if (node == null) return new HashSet<>();
        Set<K> s = new HashSet<>();
        s.add(node.key);
        if(node.left != null) s.addAll(keySetHelper(node.left));
        if(node.right != null) s.addAll(keySetHelper(node.right));
        return s;
    }

    @Override
    public V remove(K key) {
        Node node = find(root, key);
        if (node != null) {
            root = delete(root, key);
            size -= 1;
            return node.value;
        }
        return null;
    }
    private Node delete(Node node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = delete(node.left, key);
        else if (cmp > 0) node.right = delete(node.right, key);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node successor = min(node.right);
            node.key = successor.key;
            node.value = successor.value;
            successor = delete(successor, successor.key);

        }

        return node;
    }
    private Node min(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public V remove(K key, V value) {
        Node node = find(root, key);
        if (node != null && node.value == value) {
            delete(root, key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIter();
    }

    private class BSTMapIter implements Iterator<K> {
        private List<Node> lst;

        public BSTMapIter() {
            lst = new ArrayList<>();
            if (root != null) lst.add(root);
        }

        @Override
        public boolean hasNext() {
            return !lst.isEmpty();
        }

        @Override
        public K next() {
            Node node = lst.remove(0);
            if (node.left != null) lst.add(node.left);
            if (node.right != null) lst.add(node.right);
            return node.key;
        }
    }

}
