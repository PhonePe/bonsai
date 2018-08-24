package com.phonepe.platform.bonsai.core.vital.provided.model;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.vital.Knot;
import lombok.*;

import javax.annotation.Nullable;
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
public class AtomicEdge extends Condition implements Comparable<AtomicEdge>{
    private String id;
    private String knotId;
    private String pivot;
    private int priority;
    private int version;
    private List<Filter> conditions;

    @Builder
    public AtomicEdge(String id, String knotId, String pivot, int priority, int version, @Singular List<Filter> conditions) {
        this.id = id;
        this.knotId = knotId;
        this.pivot = pivot;
        this.priority = priority;
        this.version = version;
        this.conditions = conditions;
    }

    @Override
    public int compareTo(@Nullable AtomicEdge edge) {
        if (edge == null) {
            return 1;
        }
        return Integer.compare(priority, edge.priority);
    }
}
