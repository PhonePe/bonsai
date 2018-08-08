package com.phonepe.platform.bonsai.core.core;

import com.phonepe.folios.condition.engine.Condition;
import com.phonepe.platform.bonsai.core.query.filter.Filter;
import lombok.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:41 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Edge extends Condition implements Comparable<Edge> {
    private String id;
    private Knot knot;
    private String pivot;
    private int priority;
    private int version;
    private List<Filter> conditions;

    @Builder
    public Edge(String id, Knot knot, String pivot, int priority, int version,
                @Singular List<Filter> conditions) {
        this.id = id;
        this.knot = knot;
        this.pivot = pivot;
        this.priority = priority;
        this.version = version;
        this.conditions = conditions;
    }

    @Override
    public int compareTo(@Nullable Edge edge) {
        if (edge == null) {
            return 1;
        }
        return Integer.compare(priority, edge.priority);
    }
}
