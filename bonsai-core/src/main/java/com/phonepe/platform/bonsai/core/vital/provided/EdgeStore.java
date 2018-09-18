package com.phonepe.platform.bonsai.core.vital.provided;


import com.phonepe.platform.bonsai.core.structures.OrderedList;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:26 AM
 */
public interface EdgeStore<I, Edge extends Comparable<Edge>> {
    Edge mapEdge(I i, Edge knot);

    Edge get(I i);

    Edge delete(I i);

    OrderedList<Edge> getAll(List<I> i);
}
