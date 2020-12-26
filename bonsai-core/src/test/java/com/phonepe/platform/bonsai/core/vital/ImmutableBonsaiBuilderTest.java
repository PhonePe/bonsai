package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author - suraj.s
 * @date - 2019-11-21
 */
public class ImmutableBonsaiBuilderTest {

    private Bonsai<Context> mutableBonsaiTree;
    private ImmutableBonsaiBuilder<Context> immutableBonsaiBuilder;

    @Before
    public void setUp() {
        mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();
        immutableBonsaiBuilder = ImmutableBonsaiBuilder.builder(mutableBonsaiTree);
    }

    @After
    public void tearDown() throws Exception {
        mutableBonsaiTree = null;
        immutableBonsaiBuilder = null;
    }

    @Test
    public void given_immutableBonsaiBuilder_when_buildingImmutableBonsaiTree_then_buildImmutableBonsaiTree() {
        final Bonsai<Context> mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .mutualExclusivitySettingTurnedOn(false)
                        .build())
                .build();
        final Bonsai<Context> immutableBonsaiTree = ImmutableBonsaiBuilder
                .builder(mutableBonsaiTree)
                .build();

        assertNotNull(immutableBonsaiTree);
    }

    @Test
    public void given_immutableBonsaiBuilder_when_addingKnotsIntoImmutableBonsaiBuilder_then_doThoseOperations() {
        final Bonsai<Context> mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();

        ImmutableBonsaiBuilder<Context> immutableBonsaiTreeBuilder = ImmutableBonsaiBuilder
                .builder(mutableBonsaiTree);

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createKnot(
                Knot.builder()
                        .id("K1")
                        .knotData(ValuedKnotData.stringValue("K1 Data"))
                        .version(123)
                        .build());
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createMapping("Key1", "K1");
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.updateKnotData(
                "K1", ValuedKnotData.stringValue("New K1 Data"), new HashMap<>());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createKnot(
                Knot.builder()
                        .id("K2")
                        .knotData(ValuedKnotData.stringValue("K2 Data"))
                        .version(234)
                        .build());
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createMapping("Key2", "K2");
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.updateKnotData(
                "K2", ValuedKnotData.stringValue("New K2 Data"), new HashMap<>());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.addVariation("K1",
                Variation.builder()
                        .knotId("K2")
                        .priority(1)
                        .filter(EqualsFilter.builder().field("Field").value("value").build())
                        .build());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.deleteKnot("K1", false);

        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiTreeBuilder.build();

        assertNotNull(immutableBonsaiTree);
        assertNotNull(immutableBonsaiTree.getKnot("K2"));
        assertNull(immutableBonsaiTree.getKnot("K1"));
    }

    @Test
    public void given_immutableBonsaiBuilder_when_addingEdgeIntoImmutableBonsaiBuilder_then_doThoseOperations() {
        final Bonsai<Context> mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();

        ImmutableBonsaiBuilder<Context> immutableBonsaiTreeBuilder = ImmutableBonsaiBuilder
                .builder(mutableBonsaiTree);

        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        Edge e1Edge = Edge.builder()
                         .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                         .filter(EqualsFilter.builder().field("fieldOne").value("valueOne").build())
                         .version(1234)
                         .knotId("K2")
                         .build();
        edges.add(e1Edge.getEdgeIdentifier());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createKnot(
                Knot.builder()
                        .id("K1")
                        .knotData(ValuedKnotData.stringValue("K1 Data"))
                        .edges(edges)
                        .version(123)
                        .build());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createEdge(e1Edge);

        final List<Filter> edgeFilters = Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build(),
                                                       NotEqualsFilter.builder().field("fieldOne").value("valueTwo").build());
        final Variation updateVariation = Variation.builder().filters(edgeFilters).build();
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.updateVariation("K1", "E1", updateVariation);

        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiTreeBuilder.build();

        assertNotNull(immutableBonsaiTree);
        assertNotNull(immutableBonsaiTree.getKnot("K1"));
        assertNull(immutableBonsaiTree.getKnot("K2"));
        assertNotNull(immutableBonsaiTree.getEdge("E1"));
        assertEquals(2, immutableBonsaiTree.getEdge("E1").getFilters().size());
    }

    @Test
    public void given_immutableBonsaiTree_when_creatingKnotAndCapture_then_saveKnotAndReturnPreviousValueCorrespondingToKnotId() {
        final Knot knot = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("K1 Data"))
                .version(123)
                .build();

        final Knot capturedPreviousKnot = immutableBonsaiBuilder.createKnotAndCapture(knot);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Knot fetchedKnot = immutableBonsaiTree.getKnot("K1");

        assertNull(capturedPreviousKnot);
        assertNotNull(fetchedKnot);
    }

    @Test
    public void given_immutableBonsaiTree_when_creatingKnotAndCaptureWithKnotData_then_saveKnotAndReturnValueCorrespondingToKnotId() {
        final KnotData knotData = ValuedKnotData.stringValue("K1 Data");
        final Knot capturedPreviousKnot = immutableBonsaiBuilder.createKnotAndCapture(knotData);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Knot fetchedKnot = immutableBonsaiTree.getKnot(capturedPreviousKnot.getId());

        assertNotNull(capturedPreviousKnot);
        assertNotNull(fetchedKnot);
    }

    @Test
    public void given_immutableBonsaiTree_when_creatingMappingAndCaptureWithKnotData_then_saveKnotAndPreviousValueCorrespondingToKnotId() {
        final KnotData knotData = ValuedKnotData.stringValue("K1 Data");
        final Knot capturedPreviousKnot = immutableBonsaiBuilder.createMappingAndCapture("key", knotData);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Knot fetchedKnot = immutableBonsaiTree.getKnot(capturedPreviousKnot.getId());

        assertNotNull(capturedPreviousKnot);
        assertNotNull(fetchedKnot);
    }

    @Test
    public void given_immutableBonsaiTree_when_addingVariationAndCapture_then_saveVariation() {
        final Knot knotOne = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("K1 Data"))
                .version(123)
                .build();
        final Knot knotTwo = Knot.builder()
                .id("K2")
                .knotData(ValuedKnotData.stringValue("K2 Data"))
                .version(234)
                .build();

        immutableBonsaiBuilder.createKnotAndCapture(knotOne);
        immutableBonsaiBuilder.createKnotAndCapture(knotTwo);
        final Variation variation = Variation.builder()
                .priority(1)
                .filter(EqualsFilter.builder().field("fieldOne").value("valueOne").build())
                .knotId("K2")
                .build();
        final Edge capturedEdge = immutableBonsaiBuilder.addVariationAndCapture("K1", variation);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Edge fetchedEdge = immutableBonsaiTree.getEdge(capturedEdge.getEdgeIdentifier().getId());

        assertNotNull(capturedEdge);
        assertNotNull(capturedEdge.getEdgeIdentifier().getId());
        assertEquals("K2", capturedEdge.getKnotId());
        assertNotNull(fetchedEdge);
        assertEquals("K2", fetchedEdge.getKnotId());
    }
}