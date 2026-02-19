/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.commons.bonsai.core.vital;

import com.google.common.base.Preconditions;
import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.json.eval.BonsaiHopeEngine;
import com.phonepe.commons.bonsai.json.eval.hope.HopeHandler;
import com.phonepe.commons.bonsai.json.eval.hope.impl.BonsaiHopeHandler;
import com.phonepe.commons.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.commons.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.commons.bonsai.core.vital.provided.KnotStore;
import com.phonepe.commons.bonsai.core.vital.provided.Stores;
import com.phonepe.commons.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.commons.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.commons.bonsai.core.vital.provided.impl.InMemoryKeyTreeStore;
import com.phonepe.commons.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.Knot;

import java.util.UUID;

/**
 * Use this builder to build the Bonsai Tree
 */
public class BonsaiBuilder<C extends Context> {
    private KeyTreeStore<String, String> keyTreeStore;
    private KnotStore<String, Knot> knotStore;
    private EdgeStore<String, Edge> edgeStore;
    private VariationSelectorEngine<C> variationSelectorEngine;
    private BonsaiProperties bonsaiProperties;
    private BonsaiIdGenerator bonsaiIdGenerator;
    private HopeHandler hopeHandler;

    public static <C extends Context> BonsaiBuilder<C> builder() {
        return new BonsaiBuilder<>();
    }

    public BonsaiBuilder<C> withKeyTreeStore(KeyTreeStore<String, String> keyTreeStore) {
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

    public BonsaiBuilder<C> withHopeHandler(HopeHandler hopeHandler) {
        this.hopeHandler = hopeHandler;
        return this;
    }

    public Bonsai<C> build() {
        Preconditions.checkNotNull(bonsaiProperties, "bonsaiProperties cannot be null");
        keyTreeStore = keyTreeStore == null ? new InMemoryKeyTreeStore() : keyTreeStore;
        knotStore = knotStore == null ? new InMemoryKnotStore() : knotStore;
        edgeStore = edgeStore == null ? new InMemoryEdgeStore() : edgeStore;
        hopeHandler = hopeHandler == null ? new BonsaiHopeHandler() : hopeHandler;
        final BonsaiHopeEngine hopeEngine = new BonsaiHopeEngine(hopeHandler);
        variationSelectorEngine = variationSelectorEngine == null
                                  ? new VariationSelectorEngine<>(hopeEngine)
                                  : variationSelectorEngine;
        final ComponentBonsaiTreeValidator bonsaiTreeValidator = new ComponentBonsaiTreeValidator(bonsaiProperties, hopeEngine);
        Preconditions.checkArgument(bonsaiProperties.getMaxAllowedConditionsPerEdge() > 0,
                "maxAllowedConditionsPerEdge cannot be < 1");
        Preconditions.checkArgument(bonsaiProperties.getMaxAllowedVariationsPerKnot() > 0,
                "maxAllowedVariationsPerKnot cannot be < 1");
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
        return new BonsaiTree<>(
                new Stores<>(keyTreeStore, knotStore, edgeStore),
                variationSelectorEngine,
                bonsaiTreeValidator,
                bonsaiProperties,
                bonsaiIdGenerator);
    }
}
