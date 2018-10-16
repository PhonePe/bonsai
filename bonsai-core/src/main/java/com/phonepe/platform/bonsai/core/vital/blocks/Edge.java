package com.phonepe.platform.bonsai.core.vital.blocks;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import lombok.*;

import java.util.List;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link Knot} id
 * It is part of a {@link Knot}
 *
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:41 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Edge extends Condition {
    private String knotId;
    private EdgeIdentifier edgeIdentifier;
    private long version;
    private List<Filter> filters;

    @Builder
    public Edge(EdgeIdentifier edgeIdentifier, String knotId, long version, @Singular List<Filter> filters) {
        this.edgeIdentifier = edgeIdentifier;
        this.knotId = knotId;
        this.version = version;
        this.filters = filters;
    }

    public Edge updateVersion() {
        this.version = System.currentTimeMillis();
        return this;
    }
}
