package com.phonepe.platform.bonsai.models.blocks;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.query.dsl.Filter;
import lombok.*;

import java.util.Collections;
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
    public Edge(final EdgeIdentifier edgeIdentifier,
                final String knotId,
                final @Singular List<Filter> filters,
                final long version,
                final Boolean live,
                final Float percentage) {
        super(live == null ? true : live, percentage == null ? 100f : percentage, Collections.emptyMap());
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
