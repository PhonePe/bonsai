package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.models.TreeKnotState;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  2018-10-03 - 11:55
 */
@AllArgsConstructor
public class ImmutableBonsaiTree<C extends Context> implements Bonsai<C> {
    private Bonsai<C> bonsai;

    @Override
    public Edge getEdge(final String edgeId) {
        return bonsai.getEdge(edgeId);
    }

    @Override
    public Map<String, Edge> getAllEdges(final List<String> edgeIds) {
        return bonsai.getAllEdges(edgeIds);
    }

    @Override
    public boolean containsKey(final String key) {
        return bonsai.containsKey(key);
    }

    @Override
    public Knot getKnot(final String knotId) {
        return bonsai.getKnot(knotId);
    }

    @Override
    public KeyNode evaluate(final String key, final C context) {
        return bonsai.evaluate(key, context);
    }

    @Override
    public FlatTreeRepresentation evaluateFlat(final String key, final C context) {
        return bonsai.evaluateFlat(key, context);
    }

    @Override
    public String getMapping(final String key) {
        return bonsai.getMapping(key);
    }

    @Override
    public boolean containsKnot(String knotId) {
        return bonsai.containsKnot(knotId);
    }

    @Override
    public Knot createKnot(final KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot createKnot(final Knot knot) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot updateKnotData(final String knotId, final KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeKnot deleteKnot(final String knotId, final boolean recursive) {
        throw unsupportedOperationError();
    }

    @Override
    public boolean containsEdge(String edgeId) {
        return bonsai.containsEdge(edgeId);
    }

    @Override
    public Edge createEdge(final Edge edge) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge addVariation(final String knotId, final Variation variation) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge updateVariation(final String knotId, final String edgeId, final Variation updateVariation) {
        throw unsupportedOperationError();
    }

    @Override
    public boolean unlinkVariation(final String knotId, final String edgeId) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeEdge deleteVariation(final String knotId, final String edgeId, final boolean recursive) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot createMapping(final String key, final String knotId) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot createMapping(final String key, final KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot removeMapping(final String key) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeKnot getCompleteTree(final String key) {
        return bonsai.getCompleteTree(key);
    }

    @Override
    public Knot createCompleteTree(TreeKnot treeKnot) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeKnotState getCompleteTreeWithDeltaOperations(final String key,
                                                            final List<DeltaOperation> deltaOperationList) {
        return bonsai.getCompleteTreeWithDeltaOperations(key, deltaOperationList);
    }

    @Override
    public TreeKnotState applyDeltaOperations(final String key,
                                              final List<DeltaOperation> deltaOperationList) {
        throw unsupportedOperationError();
    }

    private BonsaiError unsupportedOperationError() {
        return new BonsaiError(BonsaiErrorCode.UNSUPPORTED_OPERATION, "ImmutableBonsaiTree cannot be modified");
    }
}
