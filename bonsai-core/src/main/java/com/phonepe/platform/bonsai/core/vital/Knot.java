package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.structures.OrderedList;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:38 PM
 */
@Data
@ToString
public class Knot {
    private String id;
    private long version;
    private OrderedList<Edge> edges;
    private KnotData knotData;

    @Builder
    public Knot(String id, long version,
                OrderedList<Edge> edges, KnotData knotData) {
        this.id = id;
        this.version = version;
        this.edges = edges;
        this.knotData = knotData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Knot knot = (Knot) o;
        return Objects.equals(id, knot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void updateVersion() {
        version = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("version", version)
                .append("edges", edges.stream().map(Edge::getId).reduce((s1, s2) -> s1 + ":" + s2))
                .append("knotData", knotData)
                .toString();
    }
}
