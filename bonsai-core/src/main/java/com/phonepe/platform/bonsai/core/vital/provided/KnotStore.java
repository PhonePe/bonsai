package com.phonepe.platform.bonsai.core.vital.provided;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:25 AM
 */
public interface KnotStore<I, K> {
    boolean containsKey(I id);

    boolean mapKnot(I id, K knot);

    K get(I id);

    K delete(I i);
}
