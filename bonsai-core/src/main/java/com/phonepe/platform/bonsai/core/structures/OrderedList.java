package com.phonepe.platform.bonsai.core.structures;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * An ordered list of elements, where ordering is maintained based on the comparable element
 * Adding elements at a position isn't allowed
 * This will ensure that all elements that are added are in a specific order
 *
 * @author tushar.naik
 * @version 1.0  20/07/18 - 5:15 PM
 */
public class OrderedList<T extends Comparable<T>> extends LinkedList<T> {

    @Override
    public boolean add(T item) {
        ListIterator<T> iterator = listIterator();
        while (true) {
            if (!iterator.hasNext()) {
                iterator.add(item);
                return true;
            }
            T listItem = iterator.next();
            if (listItem.compareTo(item) > 0) {
                iterator.previous();
                iterator.add(item);
                return true;
            }
        }
    }

    @Override
    public void addFirst(T t) {
        throw new UnsupportedOperationException("addFirst not supported");
    }

    @Override
    public void addLast(T t) {
        throw new UnsupportedOperationException("AddLast not supported");
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) {
        return elements.stream().map(this::add).reduce((a, b) -> a && b).orElse(false);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Adding at position not supported");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Adding at position not supported");
    }
}