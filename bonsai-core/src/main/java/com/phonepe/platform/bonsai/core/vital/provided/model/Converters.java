package com.phonepe.platform.bonsai.core.vital.provided.model;

import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.vital.Edge;
import com.phonepe.platform.bonsai.core.vital.Knot;

import java.util.stream.Collectors;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:03 PM
 */
public interface Converters {

    static Knot toKnot(AtomicKnot atomicKnot) {
        return Knot.builder()
                   .id(atomicKnot.getId())
                   .knotData(atomicKnot.getKnotData())
                   .version(atomicKnot.getVersion())
                   .build();
    }

    static AtomicKnot toAtomicKnot(Knot iKnot) {
        return AtomicKnot.builder()
                         .id(iKnot.getId())
                         .knotData(iKnot.getKnotData())
                         .version(iKnot.getVersion())
                         .edges(iKnot.getEdges() == null
                                        ? null
                                        : iKnot.getEdges()
                                               .stream()
                                               .map(Edge::getId)
                                               .collect(Collectors.toCollection(OrderedList::new)))
                         .build();
    }

    static AtomicEdge toAtomicEdge(Edge edge) {
        return AtomicEdge.builder()
                         .id(edge.getId())
                         .priority(edge.getPriority())
                         .conditions(edge.getConditions())
                         .pivot(edge.getPivot())
                         .knotId(edge.getKnot().getId())
                         .version(edge.getVersion())
                         .build();
    }
}
