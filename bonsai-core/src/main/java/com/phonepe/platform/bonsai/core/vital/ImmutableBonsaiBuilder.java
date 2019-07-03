package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.query.dsl.Filter;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  2018-10-03 - 11:55
 */
public class ImmutableBonsaiBuilder<C extends Context> {
    private Bonsai<C> bonsai;

    public ImmutableBonsaiBuilder(Bonsai<C> bonsai) {
        this.bonsai = bonsai;
    }

    public static <C extends Context> ImmutableBonsaiBuilder<C> builder(Bonsai<C> bonsai) {
        return new ImmutableBonsaiBuilder<>(bonsai);
    }

    public ImmutableBonsaiBuilder<C> createKnot(KnotData knotData) {
        bonsai.createKnot(knotData);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createKnot(Knot knot) {
        bonsai.createKnot(knot);
        return this;
    }

    public ImmutableBonsaiBuilder<C> updateKnotData(String id, KnotData knotData) {
        bonsai.updateKnotData(id, knotData);
        return this;
    }

    public ImmutableBonsaiBuilder<C> deleteKnot(String id, boolean recursive) {
        bonsai.deleteKnot(id, recursive);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createEdge(Edge edge) {
        bonsai.createEdge(edge);
        return this;
    }

    public ImmutableBonsaiBuilder<C> addVariation(String knotId, Variation variation) {
        bonsai.addVariation(knotId, variation);
        return this;
    }

    public ImmutableBonsaiBuilder<C> updateEdgeFilters(String knotId, String edgeId,
                                                       List<Filter> filters) {
        bonsai.updateEdgeFilters(knotId, edgeId, filters);
        return this;
    }

    public ImmutableBonsaiBuilder<C> addEdgeFilters(String edgeId,
                                                    List<Filter> filters) {
        bonsai.addEdgeFilters(edgeId, filters);
        return this;
    }

    public ImmutableBonsaiBuilder<C> unlinkVariation(String knotId, String edgeId) {
        bonsai.unlinkVariation(knotId, edgeId);
        return this;
    }

    public ImmutableBonsaiBuilder<C> deleteVariation(String knotId,
                                                     String edgeId,
                                                     boolean recursive) {
        bonsai.deleteVariation(knotId, edgeId, recursive);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createMapping(String key, String knotId) {
        bonsai.createMapping(key, knotId);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createMapping(String key,
                                                   KnotData knotData) {
        bonsai.createMapping(key, knotData);
        return this;
    }

    public ImmutableBonsaiBuilder<C> removeMapping(String key) {
        bonsai.removeMapping(key);
        return this;
    }

    public Bonsai<C> build() {
        return new ImmutableBonsaiTree<>(bonsai);
    }

}
