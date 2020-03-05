package com.phonepe.platform.bonsai.core.visitor.delta.impl;

import com.google.common.base.Preconditions;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;

import java.util.List;

/**
 * The default class responsible for storing the key:knotId mapping, knot's data and edge's data
 * into respective data-store.
 *
 * @author - suraj.s
 * @date - 2019-11-25
 */
public class SaveDataOperationIntoStoreVisitor implements DeltaOperationVoidVisitor {
    private final KeyTreeStore<String, String> keyTreeStore;
    private final KnotStore<String, Knot> knotStore;
    private final EdgeStore<String, Edge> edgeStore;

    public SaveDataOperationIntoStoreVisitor(final KeyTreeStore<String, String> keyTreeStore,
                                             final KnotStore<String, Knot> knotStore,
                                             final EdgeStore<String, Edge> edgeStore) {
        Preconditions.checkNotNull(keyTreeStore, "KeyTreeStore should not be null.");
        Preconditions.checkNotNull(knotStore, "KnotStore should not be null.");
        Preconditions.checkNotNull(edgeStore, "EdgeStore should not be null.");
        this.keyTreeStore = keyTreeStore;
        this.knotStore = knotStore;
        this.edgeStore = edgeStore;
    }

    /**
     * {@inheritDoc}
     * @param keyMappingDeltaOperation - {@link KeyMappingDeltaOperation} object; it contains key and knotId mapping.
     */
    @Override
    public void visit(KeyMappingDeltaOperation keyMappingDeltaOperation) {
        final String keyId  = keyMappingDeltaOperation.getKeyId();
        final String knotId = keyMappingDeltaOperation.getKnotId();
        keyTreeStore.createKeyTree(keyId, knotId);
    }

    /**
     * {@inheritDoc}
     * @param knotDeltaOperation -  {@link KnotDeltaOperation} object; it contains all the data to represent a single knot and
     */
    @Override
    public void visit(KnotDeltaOperation knotDeltaOperation) {
        final Knot knot = knotDeltaOperation.getKnot();
        knot.setVersion(System.currentTimeMillis());
        knotStore.mapKnot(knot.getId(), knot);

        final List<EdgeIdentifier> edgeIdentifiers = knot.getEdges();
        if (edgeIdentifiers != null && !edgeIdentifiers.isEmpty()) {
            for(EdgeIdentifier edgeIdentifier : edgeIdentifiers) {
                final Edge fetchedEdge = edgeStore.getEdge(edgeIdentifier.getId());
                if (fetchedEdge == null) {
                    final Edge edge = Edge.builder()
                            .edgeIdentifier(edgeIdentifier)
                            .build();
                    edgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param edgeDeltaOperation - {@link EdgeDeltaOperation} object; it contains all the data to represent a single edge and
     */
    @Override
    public void visit(EdgeDeltaOperation edgeDeltaOperation) {
        final Edge edge = edgeDeltaOperation.getEdge();
        edge.setVersion(System.currentTimeMillis());
        edgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);

        final String childKnotId = edge.getKnotId();
        if (childKnotId != null && !childKnotId.isEmpty()) {
            final Knot fetchedKnot = knotStore.getKnot(childKnotId);
            if (fetchedKnot == null) {
                final Knot childKnot = Knot.builder()
                        .id(childKnotId)
                        .build();
                knotStore.mapKnot(childKnot.getId(), childKnot);
            }
        }
    }
}
