package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.platform.query.dsl.Filter;
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
    public Edge getEdge(String edgeId) {
        return bonsai.getEdge(edgeId);
    }

    @Override
    public Map<String, Edge> getAllEdges(List<String> edgeIds) {
        return bonsai.getAllEdges(edgeIds);
    }

    @Override
    public Knot getKnot(String knotId) {
        return bonsai.getKnot(knotId);
    }

    @Override
    public KeyNode evaluate(String key, C context) {
        return bonsai.evaluate(key, context);
    }

    @Override
    public FlatTreeRepresentation evaluateFlat(String key, C context) {
        return bonsai.evaluateFlat(key, context);
    }

    @Override
    public String getMapping(String key) {
        return bonsai.getMapping(key);
    }

    @Override
    public Knot createKnot(KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot createKnot(Knot knot) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot updateKnotData(String knotId, KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeKnot deleteKnot(String knotId, boolean recursive) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge createEdge(Edge edge) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge addVariation(String knotId, Variation variation) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge updateEdgeFilters(String knotId, String edgeId,
                                  List<Filter> filters) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge addEdgeFilters(String edgeId,
                               List<Filter> filters) {
        throw unsupportedOperationError();
    }

    @Override
    public boolean unlinkVariation(String knotId, String edgeId) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeEdge deleteVariation(String knotId, String edgeId, boolean recursive) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot createMapping(String key, String knotId) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot createMapping(String key, KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public Knot removeMapping(String key) {
        throw unsupportedOperationError();
    }

    @Override
    public TreeKnot getCompleteTree(String key) {
        return bonsai.getCompleteTree(key);
    }

    private BonsaiError unsupportedOperationError() {
        return new BonsaiError(BonsaiErrorCode.UNSUPPORTED_OPERATION, "ImmutableBonsaiTree cannot be modified");
    }
}
