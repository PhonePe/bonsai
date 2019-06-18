package com.phonepe.platform.bonsai.core.vital.blocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

/**
 * @author tushar.naik
 * @version 1.0  18/09/18 - 6:49 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EdgeIdentifier implements Comparable<EdgeIdentifier> {
    private String id;
    private int number;
    private int priority;

    @Override
    public int compareTo(@Nullable EdgeIdentifier edgeIdentifier) {
        if (edgeIdentifier == null) {
            return 1;
        }
        return Integer.compare(priority, edgeIdentifier.priority);
    }
}
