package com.phonepe.platform.bonsai.core.structures;

public interface ConflictResolver<T> {
    T resolveConflict(T t1, T t2);
}
