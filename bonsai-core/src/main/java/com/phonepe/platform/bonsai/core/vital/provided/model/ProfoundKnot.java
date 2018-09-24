package com.phonepe.platform.bonsai.core.vital.provided.model;

import com.phonepe.platform.bonsai.core.data.KnotData;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:38 PM
 */
@Data
public class ProfoundKnot {
    private String id;
    private long version;
    private OrderedList<ProfoundEdge> profoundEdges;
    private KnotData knotData;

    @Builder
    public ProfoundKnot(String id, long version,
                        OrderedList<ProfoundEdge> profoundEdges, KnotData knotData) {
        this.id = id;
        this.version = version;
        this.profoundEdges = profoundEdges;
        this.knotData = knotData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfoundKnot profoundKnot = (ProfoundKnot) o;
        return Objects.equals(id, profoundKnot.id);
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
                .append("edges", profoundEdges.stream().map(ProfoundEdge::getId).reduce((s1, s2) -> s1 + ":" + s2))
                .append("knotData", knotData)
                .toString();
    }
}
