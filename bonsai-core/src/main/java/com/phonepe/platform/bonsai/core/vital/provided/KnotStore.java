package com.phonepe.platform.bonsai.core.vital.provided;

import com.phonepe.platform.bonsai.core.vital.blocks.Knot;

/**
 * A store for knots
 *
 * @param <I> id
 * @param <K> knot
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:25 AM
 */
public interface KnotStore<I, K> {
    /**
     * check if id is mapped to a knot
     *
     * @param id id
     * @return true if knot exists
     */
    boolean containsKnot(I id);

    /**
     * map an id to a knot
     *
     * @param id   id to be mapped to the knot
     * @param knot knot being mapped
     * @return the previous value associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    Knot mapKnot(I id, K knot);

    /**
     * get the mapped knot
     *
     * @param id id to be fetched
     * @return knot mapped to it
     */
    K getKnot(I id);

    /**
     * remove the knot associated with id
     *
     * @param i id
     * @return the previous value associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    K deleteKnot(I i);
}
