package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.structures.ConflictResolver;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class BonsaiBuilderTest {

    @Test
    public void given_bonsaiBuilder_when_buildingBonsaiWithLeastRequiredParameters_then_returnBonsaiTree() {
        final Bonsai bonsaiTree = BonsaiBuilder.builder()
                                               .withBonsaiProperties(BonsaiProperties.builder().build()).build();
        assertNotNull(bonsaiTree);
    }

    @Test
    public void given_bonsaiBuilder_when_buildingBonsaiWithAllRequiredParameters_then_returnBonsaiTree() {
        final KeyTreeStore<String, String> keyTreeStore = new InMemoryKeyTreeStore();
        final KnotStore<String, Knot> knotStore = new InMemoryKnotStore();
        final EdgeStore<String, Edge> edgeStore = new InMemoryEdgeStore();
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
        final ConflictResolver<Knot> knotConflictResolver = new KnotMergingConflictResolver();
        final Bonsai bonsaiTree = BonsaiBuilder.builder()
                                               .withKeyTreeStore(keyTreeStore)
                                               .withKnotStore(knotStore)
                                               .withEdgeStore(edgeStore)
                                               .withVariationSelectorEngine(variationSelectorEngine)
                                               .withBonsaiProperties(bonsaiProperties)
                                               .withBonsaiIdGenerator(bonsaiIdGenerator)
                                               .withKnotConflictResolver(knotConflictResolver)
                                               .withBonsaiProperties(BonsaiProperties.builder().build())
                                               .build();

        assertNotNull(bonsaiTree);
    }

    @Test(expected = NullPointerException.class)
    public void given_bonsaiBuilder_when_buildingBonsaiWithNoBonsaiProperties_then_throwNullPointerException() {
        final Bonsai bonsaiTree = BonsaiBuilder.builder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_bonsaiBuilder_when_buildingBonsaiWithZeroConditionsPerEdge_then_throwIllegalArgumentException() {
        final Bonsai bonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(0)
                        .build())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_bonsaiBuilder_when_buildingBonsaiWithZeroVariationsPerKnot_then_throwIllegalArgumentException() {
        final Bonsai bonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedVariationsPerKnot(0)
                        .build())
                .build();
    }
}