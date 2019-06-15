package com.phonepe.platform.bonsai.core.structures;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-15 - 20:35
 */
public interface ConflictResolver<T> {
    T resolveConflict(T t1, T t2);
}
