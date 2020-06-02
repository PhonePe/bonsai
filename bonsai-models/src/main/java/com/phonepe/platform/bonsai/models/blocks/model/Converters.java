package com.phonepe.platform.bonsai.models.blocks.model;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;

/**
 * @author tushar.naik
 * @version 1.0  2019-05-19 - 12:41
 */
public interface Converters {
    static TreeKnot toTreeKnot(Knot knot) {
        return TreeKnot.builder()
                .id(knot.getId())
                .knotData(knot.getKnotData())
                .version(knot.getVersion())
                .build();
    }

    static TreeEdge toTreeEdge(Edge edge) {
        return TreeEdge.builder()
                .edgeIdentifier(edge.getEdgeIdentifier())
                .filters(edge.getFilters())
                .version(edge.getVersion())
                .live(edge.isLive())
                .percentage(edge.getPercentage())
                .build();
    }

    static Variation toVariation(String childKnotId, TreeEdge treeEdge) {
        return Variation.builder()
                        .knotId(childKnotId)
                        .filters(treeEdge.getFilters())
                        .live(treeEdge.isLive())
                        .percentage(treeEdge.getPercentage())
                        .priority(treeEdge.getEdgeIdentifier().getPriority())
                        .build();
    }
}
