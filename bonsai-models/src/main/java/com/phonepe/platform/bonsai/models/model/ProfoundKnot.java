package com.phonepe.platform.bonsai.models.model;

import com.phonepe.platform.bonsai.models.KeyNode;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:38 PM
 */
@Data
public class ProfoundKnot {
    private String id;
    private long version;
    private List<ProfoundEdge> profoundEdges;
    private KeyNode knotData;

    @Builder
    public ProfoundKnot(String id, long version,
                        List<ProfoundEdge> profoundEdges, KeyNode knotData) {
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
}
