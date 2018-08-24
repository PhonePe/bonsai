package com.phonepe.platform.bonsai.core.vital.provided;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 1:24 AM
 */
public interface MappingStore<K, I> {
    boolean containsKey(K k);
    boolean map(K k, I i);
    K get(I i);
}
