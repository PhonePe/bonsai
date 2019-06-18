package com.phonepe.platform.bonsai.core.vital.blocks.model;

import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.bonsai.core.vital.blocks.EdgeIdentifier;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link TreeKnot}
 * It is part of a {@link TreeKnot}
 *
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:41 PM
 */
@Data
@NoArgsConstructor
public class TreeEdge {
    private EdgeIdentifier edgeIdentifier;
    private TreeKnot treeKnot;
    private long version;
    private List<Filter> filters;

    @Builder
    public TreeEdge(EdgeIdentifier edgeIdentifier, TreeKnot treeKnot, long version,
                    List<Filter> filters) {
        this.edgeIdentifier = edgeIdentifier;
        this.treeKnot = treeKnot;
        this.version = version;
        this.filters = filters;
    }
}
