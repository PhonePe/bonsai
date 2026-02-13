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

import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.json.eval.hope.HopeFactory;
import com.phonepe.commons.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.commons.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.commons.bonsai.core.vital.provided.impl.InMemoryKeyTreeStore;
import com.phonepe.commons.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonsaiBuilderTest {

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithLeastRequiredParameters_then_returnBonsaiTree() {
        final var bonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build()).build();
        assertNotNull(bonsaiTree);
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithAllRequiredParameters_then_returnBonsaiTree() {
        final var keyTreeStore = new InMemoryKeyTreeStore();
        final var knotStore = new InMemoryKnotStore();
        final var edgeStore = new InMemoryEdgeStore();
        final var hopeLangEngine = HopeFactory.gethopeLangEngine();
        final VariationSelectorEngine<Context> variationSelectorEngine = new VariationSelectorEngine<>(hopeLangEngine);
        final BonsaiProperties bonsaiProperties = BonsaiProperties.builder().build();
        final BonsaiIdGenerator bonsaiIdGenerator = new BonsaiIdGenerator() {
            @Override
            public String newEdgeId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String newKnotId() {
                return UUID.randomUUID().toString();
            }
        };
        final Bonsai bonsaiTree = BonsaiBuilder.builder()
                .withKeyTreeStore(keyTreeStore)
                .withKnotStore(knotStore)
                .withEdgeStore(edgeStore)
                .withVariationSelectorEngine(variationSelectorEngine)
                .withBonsaiProperties(bonsaiProperties)
                .withBonsaiIdGenerator(bonsaiIdGenerator)
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .withHopeLangEngine(hopeLangEngine)
                .build();

        assertNotNull(bonsaiTree);
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithNoBonsaiProperties_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> BonsaiBuilder.builder().build());
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithZeroConditionsPerEdge_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(0)
                        .build())
                .build());
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithZeroVariationsPerKnot_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedVariationsPerKnot(0)
                        .build())
                .build());
    }
}