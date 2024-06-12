package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph;

// Interface for AdaptablePriorityQueue
public interface AdaptablePriorityQueue<K, V> {
    void remove(Entry<K, V> entry); // Removes the given entry from the priority queue

    void replaceKey(Entry<K, V> entry, K key); // Replaces the key of an entry
}
