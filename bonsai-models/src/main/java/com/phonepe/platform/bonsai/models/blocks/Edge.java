package com.phonepe.platform.bonsai.models.blocks;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.query.dsl.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link Knot} id
 * It is part of a {@link Knot}
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
    public Edge(final Boolean live,
                final Float percentage,
                final EdgeIdentifier edgeIdentifier,
                final String knotId,
                final long version,
                final @Singular List<Filter> filters,
                final Map<String, Object> properties) {
        super(
                live == null ? true : live,
                percentage == null ? 100f : percentage,
                properties == null ? new HashMap<>() : properties
        );
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
