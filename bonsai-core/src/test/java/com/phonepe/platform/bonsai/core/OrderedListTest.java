package com.phonepe.platform.bonsai.core;

import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.vital.Edge;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author tushar.naik
 * @version 1.0  30/07/18 - 2:45 PM
 */
public class OrderedListTest {

    @Test
    public void testOrderedListAdd() {
        OrderedList<Edge> edges = new OrderedList<>();
        edges.add(Edge.builder().id("2").priority(2).build());
        edges.add(Edge.builder().id("1").priority(1).build());
        edges.add(Edge.builder().id("4").priority(4).build());
        edges.add(Edge.builder().id("5").priority(5).build());
        edges.add(Edge.builder().id("123").priority(123).build());
        edges.add(Edge.builder().id("11").priority(11).build());
        edges.add(Edge.builder().id("-1").priority(-1).build());
        edges.add(Edge.builder().id("0").priority(0).build());
        edges.add(Edge.builder().id("2.1").priority(2).build());
        edges.add(Edge.builder().id("2.3").priority(2).build());
        edges.add(Edge.builder().id("2.2").priority(2).build());

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
        OrderedList<Edge> edges = new OrderedList<>();
        edges.add(Edge.builder().id("2").priority(2).build());
        edges.add(Edge.builder().id("1").priority(1).build());
        edges.add(Edge.builder().id("4").priority(4).build());
        edges.add(Edge.builder().id("5").priority(5).build());
        edges.add(Edge.builder().id("123").priority(123).build());
        edges.add(Edge.builder().id("11").priority(11).build());
        edges.add(Edge.builder().id("-1").priority(-1).build());

        edges.addAll(Lists.newArrayList(Edge.builder().id("A4").priority(4).build(),
                                        Edge.builder().id("A1").priority(1).build(),
                                        Edge.builder().id("A3").priority(3).build(),
                                        Edge.builder().id("A2").priority(2).build(),
                                        Edge.builder().id("A5").priority(5).build(),
                                        Edge.builder().id("A6").priority(6).build()));

        assertEquals(-1, edges.get(0).getPriority());
        assertEquals("-1", edges.get(0).getId());
        assertEquals(1, edges.get(2).getPriority());
        assertEquals("A1", edges.get(2).getId());
        assertEquals(4, edges.get(7).getPriority());
        assertEquals("A4", edges.get(7).getId());
        assertEquals(123, edges.get(edges.size() - 1).getPriority());
        assertEquals("123", edges.get(edges.size() - 1).getId());
    }

    @Test(expected = RuntimeException.class)
    public void testOrderedListAddException() {
        OrderedList<Edge> edges = new OrderedList<>();
        edges.addFirst(Edge.builder().id("1").priority(1).build());
    }
}