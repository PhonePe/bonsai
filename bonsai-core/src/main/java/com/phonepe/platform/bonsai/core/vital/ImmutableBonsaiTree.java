package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.data.KnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  2018-10-03 - 11:55
 */
public class ImmutableBonsaiTree<C extends Context> extends BonsaiTree<C> {

    public ImmutableBonsaiTree(KeyTreeStore<String, String> keyTreeStore,
                               KnotStore<String, Knot> knotStore,
                               EdgeStore<String, Edge> edgeStore,
                               VariationSelectorEngine<C> variationSelectorEngine,
                               ComponentValidator componentValidator, BonsaiProperties bonsaiProperties,
                               BonsaiIdGenerator bonsaiIdGenerator) {
        super(keyTreeStore, knotStore, edgeStore, variationSelectorEngine, componentValidator, bonsaiProperties, bonsaiIdGenerator);
    }

    @Override
    public Knot createKnot(KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public boolean updateKnotData(String knotId, KnotData knotData) {
        throw unsupportedOperationError();
    }

    @Override
    public List<Knot> deleteKnot(String id, boolean recursive) {
        throw unsupportedOperationError();
    }

    @Override
    public String addVariation(String knotId, Variation variation) {
        throw unsupportedOperationError();
    }

    @Override
    public boolean updateEdgeFilters(String knotId, String edgeId, List<Filter> filters) {
        throw unsupportedOperationError();
    }

    @Override
    public boolean addEdgeFilters(String edgeId, List<Filter> filters) {
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
        return new BonsaiError(BonsaiErrorCode.UNSUPPORTED_OPERATION, "EvaluationOnlyBonsaiTree cannot modify the Tree");
    }
}
