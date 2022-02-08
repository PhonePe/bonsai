package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;

import java.util.List;


public interface BonsaiIdGenerator {
    String newEdgeId();

    default int newEdgeNumber(List<EdgeIdentifier> edges) {
        if (edges == null || edges.isEmpty()) {
            return 1;
        }
        return edges.stream().map(EdgeIdentifier::getNumber).max(Integer::compareTo).orElse(1) + 1;
    }

    String newKnotId();
}
