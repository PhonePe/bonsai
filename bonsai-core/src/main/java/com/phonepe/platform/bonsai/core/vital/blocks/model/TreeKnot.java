package com.phonepe.platform.bonsai.core.vital.blocks.model;

import com.phonepe.platform.bonsai.core.data.KnotData;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * This is a recursive representation of the tree, without references
 *
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:38 PM
 */
@Data
@NoArgsConstructor
public class TreeKnot {
    private String id;
    private long version;
    private List<TreeEdge> treeEdges;
    private KnotData knotData;

    @Builder
    public TreeKnot(String id, long version,
                    List<TreeEdge> treeEdges, KnotData knotData) {
        this.id = id;
        this.version = version;
        this.treeEdges = treeEdges;
        this.knotData = knotData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeKnot treeKnot = (TreeKnot) o;
        return Objects.equals(id, treeKnot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
