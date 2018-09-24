package com.phonepe.platform.bonsai.core.vital.provided.model;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link ProfoundKnot}
 * It is part of a {@link ProfoundKnot}
 *
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:41 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfoundEdge extends Condition implements Comparable<ProfoundEdge> {
    private String id;
    private ProfoundKnot profoundKnot;
    private int priority;
    private int version;
    private List<Filter> filters;

    @Builder
    public ProfoundEdge(String id, ProfoundKnot profoundKnot, int priority, int version, @Singular List<Filter> filters) {
        this.id = id;
        this.profoundKnot = profoundKnot;
        this.priority = priority;
        this.version = version;
        this.filters = filters;
    }

    @Override
    public int compareTo(@Nullable ProfoundEdge profoundEdge) {
        if (profoundEdge == null) {
            return 1;
        }
        return Integer.compare(priority, profoundEdge.priority);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("knot", profoundKnot.getId())
                .append("priority", priority)
                .append("version", version)
                .append("filters", filters)
                .toString();
    }
}
