package com.phonepe.platform.bonsai.core.core;

import com.phonepe.platform.bonsai.core.structures.OrderedList;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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
}
