package com.phonepe.platform.bonsai.core.vital.provided;


import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:26 AM
 */
public interface EdgeStore<I, Edge> {
    Edge mapEdge(I i, Edge knot);

    Edge get(I i);

    Edge delete(I i);

    List<Edge> getAll(List<I> i);
}
