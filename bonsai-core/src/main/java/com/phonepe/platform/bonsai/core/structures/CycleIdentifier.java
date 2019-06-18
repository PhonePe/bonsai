package com.phonepe.platform.bonsai.core.structures;

import com.google.common.collect.Sets;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;

import java.util.Set;

/**
 * A structure used to identify if the same item has already been visited (in a graph / tree)
 *
 * @author tushar.naik
 * @version 1.0  27/07/18 - 11:13 AM
 */
public class CycleIdentifier<T> {
    private final Set<T> items;

    public CycleIdentifier() {
        items = Sets.newHashSet();
    }

    public void add(T item) {
        if (items.contains(item)) {
            throw new BonsaiError(BonsaiErrorCode.CYCLE_DETECTED, "Cycle identified at item:" + item);
        }
        items.add(item);
    }
}
