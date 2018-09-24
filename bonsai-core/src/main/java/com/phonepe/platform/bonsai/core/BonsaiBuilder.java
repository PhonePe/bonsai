package com.phonepe.platform.bonsai.core;

import com.google.common.base.Preconditions;
import com.phonepe.platform.bonsai.core.vital.BonsaiIdGenerator;
import com.phonepe.platform.bonsai.core.vital.BonsaiProperties;
import com.phonepe.platform.bonsai.core.vital.BonsaiTree;
import com.phonepe.platform.bonsai.core.vital.ComponentValidator;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.provided.*;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryMappingStore;

import java.util.UUID;

/**
 * Use this builder to build the Bonsai Tree
 *
 * @author tushar.naik
 * @version 1.0  19/09/18 - 12:10 PM
 */
public class BonsaiBuilder {
    private MappingStore<String, String> mappingStore;
    private KnotStore<String, Knot> knotStore;
    private EdgeStore<String, Edge> edgeStore;
    private VariationSelectorEngine variationSelectorEngine;
    private BonsaiProperties bonsaiProperties;
    private BonsaiIdGenerator bonsaiIdGenerator;

    public static BonsaiBuilder builder() {
        return new BonsaiBuilder();
    }

    public BonsaiBuilder withMappingStore(MappingStore<String, String> mappingStore) {
        this.mappingStore = mappingStore;
        return this;
    }

    public BonsaiBuilder withKnotStore(KnotStore<String, Knot> knotStore) {
        this.knotStore = knotStore;
        return this;
    }

    public BonsaiBuilder withEdgeStore(EdgeStore<String, Edge> edgeStore) {
        this.edgeStore = edgeStore;
        return this;
    }

    public BonsaiBuilder withVariationSelectorEngine(VariationSelectorEngine variationSelectorEngine) {
        this.variationSelectorEngine = variationSelectorEngine;
        return this;
    }

    public BonsaiBuilder withBonsaiProperties(BonsaiProperties bonsaiProperties) {
        this.bonsaiProperties = bonsaiProperties;
        return this;
    }

    public BonsaiBuilder withBonsaiIdGenerator(BonsaiIdGenerator bonsaiIdGenerator) {
        this.bonsaiIdGenerator = bonsaiIdGenerator;
        return this;
    }

    public Bonsai build() {
        Preconditions.checkNotNull(bonsaiProperties, "bonsaiProperties cannot be null");
        mappingStore = mappingStore == null ? new InMemoryMappingStore() : mappingStore;
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
        return new BonsaiTree(mappingStore, knotStore, edgeStore, variationSelectorEngine, new ComponentValidator(bonsaiProperties), bonsaiProperties, bonsaiIdGenerator);
    }
}
