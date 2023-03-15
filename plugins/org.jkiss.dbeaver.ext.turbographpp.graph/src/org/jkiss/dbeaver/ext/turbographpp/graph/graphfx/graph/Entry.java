package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph;

// Interface for a key-value pair
public interface Entry<K, V> {

    K getKey(); // return the key stored in this entry

    V getValue(); // return the value stored in this entry
}
