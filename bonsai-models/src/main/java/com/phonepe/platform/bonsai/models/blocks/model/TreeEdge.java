package com.phonepe.platform.bonsai.models.blocks.model;

import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.query.dsl.Filter;
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
    private boolean live;
    private float percentage;

    @Builder
    public TreeEdge(final EdgeIdentifier edgeIdentifier,
                    final TreeKnot treeKnot,
                    final long version,
                    final List<Filter> filters,
                    final boolean live,
                    final float percentage) {
        this.edgeIdentifier = edgeIdentifier;
        this.treeKnot = treeKnot;
        this.version = version;
        this.filters = filters;
        this.live = live;
        this.percentage = percentage;
    }
}
