package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKnotStore;
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
        final VariationSelectorEngine<Context> variationSelectorEngine = new VariationSelectorEngine<>();
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
                .build();

        assertNotNull(bonsaiTree);
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithNoBonsaiProperties_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            final Bonsai bonsaiTree = BonsaiBuilder.builder().build();
        });
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithZeroConditionsPerEdge_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            final Bonsai bonsaiTree = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder()
                            .maxAllowedConditionsPerEdge(0)
                            .build())
                    .build();
        });
    }

    @Test
    void given_bonsaiBuilder_when_buildingBonsaiWithZeroVariationsPerKnot_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            final Bonsai bonsaiTree = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder()
                            .maxAllowedVariationsPerKnot(0)
                            .build())
                    .build();
        });
    }
}