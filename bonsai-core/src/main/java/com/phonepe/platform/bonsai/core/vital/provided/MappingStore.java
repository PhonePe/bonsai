package com.phonepe.platform.bonsai.core.vital.provided;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:24 AM
 */
public interface MappingStore<K, I> {
    /* checks if k is present */
    boolean containsKey(K k);

    /* returns older mapped value if it exists, else returns null */
    I map(K k, I i);

    /* current mapping */
    K get(I i);

    /* return mapped value */
    I remove(K k);
}
