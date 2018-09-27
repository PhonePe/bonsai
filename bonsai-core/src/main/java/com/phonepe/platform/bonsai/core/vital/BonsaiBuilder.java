package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Preconditions;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.provided.*;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKeyTreeStore;

import java.util.UUID;

/**
 * Use this builder to build the Bonsai Tree
 *
 * @author tushar.naik
 * @version 1.0  19/09/18 - 12:10 PM
 */
public class BonsaiBuilder<C extends Context> {
    private KeyTreeStore<String, String> keyTreeStore;
    private KnotStore<String, Knot> knotStore;
    private EdgeStore<String, Edge> edgeStore;
    private VariationSelectorEngine<C> variationSelectorEngine;
    private BonsaiProperties bonsaiProperties;
    private BonsaiIdGenerator bonsaiIdGenerator;

    public static <C extends Context> BonsaiBuilder<C> builder() {
        return new BonsaiBuilder<>();
    }

    public BonsaiBuilder<C> withMappingStore(KeyTreeStore<String, String> keyTreeStore) {
        this.keyTreeStore = keyTreeStore;
        return this;
    }

    public BonsaiBuilder<C> withKnotStore(KnotStore<String, Knot> knotStore) {
        this.knotStore = knotStore;
        return this;
    }

    public BonsaiBuilder<C> withEdgeStore(EdgeStore<String, Edge> edgeStore) {
        this.edgeStore = edgeStore;
        return this;
    }

    public BonsaiBuilder<C> withVariationSelectorEngine(VariationSelectorEngine<C> variationSelectorEngine) {
        this.variationSelectorEngine = variationSelectorEngine;
        return this;
    }

    public BonsaiBuilder<C> withBonsaiProperties(BonsaiProperties bonsaiProperties) {
        this.bonsaiProperties = bonsaiProperties;
        return this;
    }

    public BonsaiBuilder<C> withBonsaiIdGenerator(BonsaiIdGenerator bonsaiIdGenerator) {
        this.bonsaiIdGenerator = bonsaiIdGenerator;
        return this;
    }

    public Bonsai<C> build() {
        Preconditions.checkNotNull(bonsaiProperties, "bonsaiProperties cannot be null");
        keyTreeStore = keyTreeStore == null ? new InMemoryKeyTreeStore() : keyTreeStore;
        knotStore = knotStore == null ? new InMemoryKnotStore() : knotStore;
        edgeStore = edgeStore == null ? new InMemoryEdgeStore() : edgeStore;
        variationSelectorEngine = variationSelectorEngine == null ?
                new VariationSelectorEngine() :
                variationSelectorEngine;
        bonsaiIdGenerator = bonsaiIdGenerator == null ? new BonsaiIdGenerator() {
            @Override
            public String newEdgeId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String newKnotId() {
                return UUID.randomUUID().toString();
            }
        } : bonsaiIdGenerator;
        return new BonsaiTree<>(keyTreeStore, knotStore, edgeStore, variationSelectorEngine, new ComponentValidator(bonsaiProperties), bonsaiProperties, bonsaiIdGenerator);
    }
}
