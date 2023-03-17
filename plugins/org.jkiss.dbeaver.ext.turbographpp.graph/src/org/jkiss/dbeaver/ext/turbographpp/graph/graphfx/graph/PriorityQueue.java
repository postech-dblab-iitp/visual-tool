package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph;

// Interface for the priority queue ADT
public interface PriorityQueue<K, V> {
    int size();

    boolean isEmpty();

    Entry<K, V> insert(K key, V value) throws IllegalArgumentException;

    Entry<K, V> min();

    Entry<K, V> removeMin();
}
