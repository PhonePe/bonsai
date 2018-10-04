package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.KnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.models.KeyNode;
import lombok.AllArgsConstructor;

import java.util.List;

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
    public Knot getKnot(String knotId) {
        return bonsai.getKnot(knotId);
    }

    @Override
    public KeyNode evaluate(String key, C context) {
        return bonsai.evaluate(key, context);
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
    public List<Knot> deleteKnot(String knotId, boolean recursive) {
        throw unsupportedOperationError();
    }

    @Override
    public Edge createEdge(Edge edge) {
        throw unsupportedOperationError();
    }

    @Override
    public String addVariation(String knotId, Variation variation) {
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
    public List<Knot> deleteVariation(String knotId, String edgeId, boolean recursive) {
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

    private BonsaiError unsupportedOperationError() {
        return new BonsaiError(BonsaiErrorCode.UNSUPPORTED_OPERATION, "ImmutableBonsaiTree cannot be modified");
    }
}
