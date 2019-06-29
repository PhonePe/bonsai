package com.phonepe.platform.bonsai.core.vital.provided;

import java.util.List;
import java.util.Map;

/**
 * A store for edges
 * All operations related to edges will reside here
 *
 * @param <I>    id
 * @param <E> edge (could be a reference too
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:26 AM
 */
public interface EdgeStore<I, E> {
    /**
     * associate an edge with the id
     *
     * @param i    id
     * @param edge edge to be associated
     * @return the previous value associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    E mapEdge(I i, E edge);

    /**
     * @param i id
     * @return edge that is mapped to the id
     */
    E getEdge(I i);

    /**
     * delete the edge that is mapped to the id
     *
     * @param i id
     * @return the previous edge associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    E deleteEdge(I i);

    /**
     * given a list of ids, fetch all the edges
     *
     * @param i ids
     * @return map of ids to corresponding edges (it returns a linkedHashMap, to maintain the order)
     */
    Map<I, E> getAllEdges(List<I> i);
}
