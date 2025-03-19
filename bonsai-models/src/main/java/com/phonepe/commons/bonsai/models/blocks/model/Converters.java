/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.commons.bonsai.models.blocks.model;

import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.blocks.Variation;
import com.phonepe.commons.bonsai.models.structures.OrderedList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Converters {

    /**
     * Static function to convert a Knot object into its corresponding TreeKnot object.
     *
     * @param knot - {@link Knot} object.
     * @return - converted TreeKnot object.
     */
    static TreeKnot toTreeKnot(Knot knot) {
        return TreeKnot.builder()
                .id(knot.getId())
                .knotData(knot.getKnotData())
                .version(knot.getVersion())
                .properties(knot.getProperties())
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
                .properties(treeKnot.getProperties())
                .build();
    }

    /**
     * Static function to convert Edge object into its corresponding TreeEdge object.
     *
     * @param edge - {@link Edge} object.
     * @return - converted TreeEdge object.
     */
    static TreeEdge toTreeEdge(Edge edge) {
        return TreeEdge.builder()
                .edgeIdentifier(edge.getEdgeIdentifier())
                .filters(edge.getFilters())
                .version(edge.getVersion())
                .properties(edge.getProperties())
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
                .properties(treeEdge.getProperties())
                .percentage(treeEdge.getPercentage())
                .live(treeEdge.isLive())
                .build();
    }

    /**
     * Static function to convert TreeEdge object into its corresponding Variation object.
     *
     * @param childKnotId - child knotId
     * @param treeEdge    - {@link TreeEdge} object.
     * @return - converted Variation object.
     */
    static Variation toVariation(String childKnotId, TreeEdge treeEdge) {
        return Variation.builder()
                .knotId(childKnotId)
                .filters(treeEdge.getFilters())
                .live(treeEdge.isLive())
                .percentage(treeEdge.getPercentage())
                .properties(treeEdge.getProperties())
                .priority(treeEdge.getEdgeIdentifier().getPriority())
                .build();
    }
}
