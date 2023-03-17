package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph;

import java.util.Comparator;

// An implementation of an adaptable priority queue using an array-based heap
public class HeapAdaptablePriorityQueue<K, V> extends HeapPriorityQueue<K, V>
        implements AdaptablePriorityQueue<K, V> {

    // nested Adaptable PQEntry class
    protected static class AdaptablePQEntry<K, V> extends PQEntry<K, V> {
        private int index;

        public AdaptablePQEntry(K key, V value, int j) {
            super(key, value);
            index = j;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int j) {
            index = j;
        }
    } // end of nested Adaptable PQEntry class

    // Creates an empty adaptable priority queue using natural ordering of keys
    public HeapAdaptablePriorityQueue() {
        super();
    }

    // Creates an empty adaptable priority queue using the given comparator
    public HeapAdaptablePriorityQueue(Comparator<K> comp) {
        super(comp);
    }

    // Validates an entry to ensure it is location-aware
    protected AdaptablePQEntry<K, V> validate(Entry<K, V> entry) throws IllegalArgumentException {
        if (!(entry instanceof AdaptablePQEntry))
            throw new IllegalArgumentException("Invalid Entry");
        AdaptablePQEntry<K, V> locator = (AdaptablePQEntry<K, V>) entry;
        int j = locator.getIndex();
        if (j >= heap.size() || heap.get(j) != locator)
            throw new IllegalArgumentException("Invalid Entry");
        return locator;
    }

    // Exchanges the entries at indices i and j of the array list
    protected void swap(int i, int j) {
        super.swap(i, j); // perform the swap
        ((AdaptablePQEntry<K, V>) heap.get(i)).setIndex(i); // reset entry's index
        ((AdaptablePQEntry<K, V>) heap.get(j)).setIndex(j); // reset entry's index
    }

    // Restores the heap property by moving the entry at index j upward/downward
    protected void bubble(int j) {
        if (j > 0 && compare(heap.get(j), heap.get(parent(j))) < 0) upheap(j);
        else downheap(j);
    }

    // Inserts a key-value pair and returns the entry created
    public Entry<K, V> insert(K key, V value) throws IllegalArgumentException {
        checkKey(key);
        Entry<K, V> newest = new AdaptablePQEntry<>(key, value, heap.size());
        heap.add(newest); // add to the end of list
        upheap(heap.size() - 1); // upheap newly added entry
        return newest;
    }

    // Removes the given entry from the priority queue
    public void remove(Entry<K, V> entry) throws IllegalArgumentException {
        AdaptablePQEntry<K, V> locator = validate(entry);
        int j = locator.getIndex();
        if (j == heap.size() - 1) // entry is at last position
        heap.remove(heap.size() - 1); // so just remove it
        else {
            swap(j, heap.size() - 1); // swap entry to last position
            heap.remove(heap.size() - 1); // then remove it
            bubble(j); // and fix entry displayed by the swap
        }
    }

    // Replaces the key of an entry
    public void replaceKey(Entry<K, V> entry, K key) throws IllegalArgumentException {
        AdaptablePQEntry<K, V> locator = validate(entry);
        checkKey(key);
        locator.setKey(key); // method inherited from PQEntry
        bubble(locator.getIndex()); // with new key, may need to move entry
    }
}
