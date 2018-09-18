package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.bonsai.core.query.filter.Filter;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link Knot}
 * It is part of a {@link Knot}
 *
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:41 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Edge extends Condition implements Comparable<Edge> {
    private String id;
    private Knot knot;
    private int priority;
    private int version;
    private List<Filter> filters;

    @Builder
    public Edge(String id, Knot knot, int priority, int version, @Singular List<Filter> filters) {
        this.id = id;
        this.knot = knot;
        this.priority = priority;
        this.version = version;
        this.filters = filters;
    }

    @Override
    public int compareTo(@Nullable Edge edge) {
        if (edge == null) {
            return 1;
        }
        return Integer.compare(priority, edge.priority);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("knot", knot.getId())
                .append("priority", priority)
                .append("version", version)
                .append("filters", filters)
                .toString();
    }
}
