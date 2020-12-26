package com.phonepe.platform.bonsai.models.blocks.model;

import com.phonepe.platform.bonsai.models.data.KnotData;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
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
    private Map<String, Object> properties;

    @Builder
    public TreeKnot(final String id,
                    final long version,
                    final List<TreeEdge> treeEdges,
                    final KnotData knotData,
                    final Map<String, Object> properties) {
        this.id = id;
        this.version = version;
        this.treeEdges = treeEdges;
        this.knotData = knotData;
        this.properties = properties;
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
