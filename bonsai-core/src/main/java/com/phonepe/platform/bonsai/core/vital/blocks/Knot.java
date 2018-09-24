package com.phonepe.platform.bonsai.core.vital.blocks;

import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.data.KnotData;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @author tushar.naik
 * @version 1.0  16/08/18 - 11:37 PM
 */
@Data
@ToString
public class Knot {
    private String id;
    private long version;
    private KnotData knotData;
    private OrderedList<EdgeIdentifier> edges;

    @Builder
    public Knot(String id, long version,
                OrderedList<EdgeIdentifier> edges, KnotData knotData) {
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

    public Knot updateVersion() {
        this.version = System.currentTimeMillis();
        return this;
    }
}
