/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.platform.bonsai.models.structures;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * An ordered list of elements, where ordering is maintained based on the comparable element
 * Adding elements at a position isn't allowed
 * This will ensure that all elements that are added are in a specific order
 */
public class OrderedList<T extends Comparable<T>> extends LinkedList<T> {

    @Override
    public boolean add(T item) {
        ListIterator<T> iterator = listIterator();
        while (true) {
            boolean toBreak = false;
            if (!iterator.hasNext()) {
                iterator.add(item);
                toBreak = true;
            } else if (iterator.next().compareTo(item) > 0) {
                iterator.previous();
                iterator.add(item);
                toBreak = true;
            }
            if (toBreak) {
                break;
            }
        }
        return true;
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

    @SafeVarargs
    public static <T extends Comparable<T>> OrderedList<T> of(T... elements) {
        OrderedList<T> orderedList = new OrderedList<>();
        Collections.addAll(orderedList, elements);
        return orderedList;
    }
}