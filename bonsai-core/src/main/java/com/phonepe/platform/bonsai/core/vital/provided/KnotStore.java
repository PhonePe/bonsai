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
     * @return true if successfully mapped
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
     * @return knot that was mapped to it
     */
    K deleteKnot(I i);
}
