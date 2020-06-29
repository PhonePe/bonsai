package com.phonepe.platform.bonsai.models.blocks.model;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.structures.OrderedList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Static function to convert top instance of TreeKnot object into its corresponding Knot object.
     *
     * @param treeKnot - {@link TreeKnot} object.
     * @return - converted Knot object.
     */
    static Knot toKnot(final TreeKnot treeKnot) {
        if (treeKnot == null) {
            return null;
        }

        final List<TreeEdge> treeEdges = treeKnot.getTreeEdges() == null ? new ArrayList<>() : treeKnot.getTreeEdges();
        final OrderedList<EdgeIdentifier> edges = treeEdges
                .stream()
                .map(TreeEdge::getEdgeIdentifier)
                .collect(Collectors.toCollection(OrderedList::new));

        return Knot.builder()
                .id(treeKnot.getId())
                .version(treeKnot.getVersion())
                .edges(edges)
                .knotData(treeKnot.getKnotData())
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

    /**
     * Static function to convert top instance of TreeEdge object into its corresponding Edge object.
     *
     * @param treeEdge - {@link TreeEdge} object.
     * @return - converted Edge object.
     */
    static Edge toEdge(final TreeEdge treeEdge) {
        if (treeEdge == null) {
            return null;
        }

        return Edge.builder()
                .edgeIdentifier(treeEdge.getEdgeIdentifier())
                .version(treeEdge.getVersion())
                .filters(treeEdge.getFilters())
                .knotId(treeEdge.getTreeKnot().getId())
                .percentage(treeEdge.getPercentage())
                .live(treeEdge.isLive())
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
