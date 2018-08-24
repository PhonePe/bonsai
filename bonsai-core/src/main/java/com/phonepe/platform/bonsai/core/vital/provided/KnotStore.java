package com.phonepe.platform.bonsai.core.vital.provided;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:25 AM
 */
public interface KnotStore<I, K, E> {
    boolean containsKey(I i);
    boolean createMapping(I i, K knot);
    boolean update(K knot);
    K get(I i);
}
