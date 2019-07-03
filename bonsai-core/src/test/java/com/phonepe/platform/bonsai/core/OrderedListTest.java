package com.phonepe.platform.bonsai.core;

import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author tushar.naik
 * @version 1.0  30/07/18 - 2:45 PM
 */
public class OrderedListTest {

    @Test
    public void testOrderedListAdd() {
        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("2", 1, 2));
        edges.add(new EdgeIdentifier("1", 1, 1));
        edges.add(new EdgeIdentifier("4", 1, 4));
        edges.add(new EdgeIdentifier("5", 1, 5));
        edges.add(new EdgeIdentifier("123", 1, 123));
        edges.add(new EdgeIdentifier("11", 1, 11));
        edges.add(new EdgeIdentifier("-1", 1, -1));
        edges.add(new EdgeIdentifier("0", 1, 0));
        edges.add(new EdgeIdentifier("2.1", 1, 2));
        edges.add(new EdgeIdentifier("2.3", 1, 2));
        edges.add(new EdgeIdentifier("2.2", 1, 2));

        assertEquals(-1, edges.get(0).getPriority());
        assertEquals("-1", edges.get(0).getId());
        assertEquals(0, edges.get(1).getPriority());
        assertEquals("0", edges.get(1).getId());
        assertEquals(2, edges.get(6).getPriority());
        assertEquals("2.2", edges.get(6).getId());
        assertEquals(123, edges.get(edges.size() - 1).getPriority());
        assertEquals("123", edges.get(edges.size() - 1).getId());
    }

    @Test
    public void testOrderedListAddAll() {
        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("2", 1, 2));
        edges.add(new EdgeIdentifier("1", 1, 1));
        edges.add(new EdgeIdentifier("4", 1, 4));
        edges.add(new EdgeIdentifier("5", 1, 5));
        edges.add(new EdgeIdentifier("123", 1, 123));
        edges.add(new EdgeIdentifier("11", 1, 11));
        edges.add(new EdgeIdentifier("-1", 1, -1));

        edges.addAll(Lists.newArrayList(new EdgeIdentifier("A4", 1, 4),
                                        new EdgeIdentifier("A1", 1, 1),
                                        new EdgeIdentifier("A3", 1, 3),
                                        new EdgeIdentifier("A2", 1, 2),
                                        new EdgeIdentifier("A5", 1, 5),
                                        new EdgeIdentifier("A6", 1, 6)));

        assertEquals(-1, edges.get(0).getPriority());
        assertEquals("-1", edges.get(0).getId());
        assertEquals(1, edges.get(2).getPriority());
        assertEquals("A1", edges.get(2).getId());
        assertEquals(4, edges.get(7).getPriority());
        assertEquals("A4", edges.get(7).getId());
        assertEquals(123, edges.get(edges.size() - 1).getPriority());
        assertEquals("123", edges.get(edges.size() - 1).getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOrderedListAddFirstException() {
        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.addFirst(new EdgeIdentifier("1", 1, 1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOrderedListAddLastException() {
        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.addLast(new EdgeIdentifier("1", 1, 1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOrderedListAddAllException() {
        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.addAll(2, Collections.emptyList());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOrderedListAddException() {
        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(2, new EdgeIdentifier("1", 1, 1));
    }
}