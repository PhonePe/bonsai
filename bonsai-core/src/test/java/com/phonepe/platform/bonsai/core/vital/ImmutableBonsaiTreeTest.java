package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  2018-10-04 - 16:21
 */
public class ImmutableBonsaiTreeTest {

    private final Bonsai<Context> mutableBonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(BonsaiProperties.builder()
                    .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                    .mutualExclusivitySettingTurnedOn(false)
                    .build())
            .build();
    private final Bonsai<Context> bonsai = ImmutableBonsaiBuilder
            .builder(mutableBonsai)
            .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
            .createMapping("key1", "k1")
            .createKnot(Knot.builder()
                            .id("k2")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
            .createMapping("key2", "k2")
            .createMapping("key3", ValuedKnotData.stringValue("d2"))
            .removeMapping("key3")
            .build();

    @Test
    public void given_immutableBonsaiTree_when_checkingItems_then_returnValues() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build())
                .createMapping("key1", "k1");
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k2").build());
        final Bonsai<Context> immutable = bonsaiBuilder.build();

        final boolean isKeyPresent = immutable.containsKey("key1");
        final boolean isKnotOnePresent = immutable.containsKnot("k1");
        final boolean isEdgeOnePresent = immutable.containsEdge("e1");
        final boolean isKnotThreePresent = immutable.containsKnot("k3");
        final boolean isEdgeThreePresent = immutable.containsEdge("e3");

        Assert.assertTrue("key1 should be present.", isKeyPresent);
        Assert.assertTrue("k1 should be present.", isKnotOnePresent);
        Assert.assertTrue("e1 should be present.", isEdgeOnePresent);
        Assert.assertFalse("k3 should be present.", isKnotThreePresent);
        Assert.assertFalse("e3 should be present.", isEdgeThreePresent);
    }

    @Test
    public void given_immutableBonsaiTree_when_getEdge_then_returnEdge() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build());
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k2").build());
        final Bonsai<Context> immutable = bonsaiBuilder.build();
        final Edge edge = immutable.getEdge("e1");

        Assert.assertNotNull("Edge should not be null", edge);
        Assert.assertEquals("KnotId should not be null",  "k2", edge.getKnotId());
        Assert.assertEquals("EdgeId should be : e1", "e1", edge.getEdgeIdentifier().getId());
        Assert.assertEquals("Version should be : 123", 1, edge.getVersion());
        Assert.assertEquals("Field of Edge Filter should be : $.data","$.data", edge.getFilters().get(0).getField());
    }

    @Test
    public void given_immutableBonsaiTree_when_getAllEdges_then_returnAllEdges() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k3")
                        .knotData(ValuedKnotData.stringValue("d3"))
                        .version(123)
                        .build())
                .createKnot(ValuedKnotData.stringValue("2"));
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k3").build());
        bonsaiBuilder.addVariation("k1", Variation.builder()
                .knotId("k3")
                .filter(new NotEqualsFilter("$.data", "male"))
                .priority(1)
                .build());
        final Bonsai<Context> immutable = bonsaiBuilder.build();
        final List<String> edgeIds = new ArrayList<>();
        edgeIds.add("e1"); edgeIds.add("e2"); edgeIds.add("e3");
        final Map<String, Edge> edgeMap = immutable.getAllEdges(edgeIds);

        Assert.assertEquals("The size of map should be three", 3, edgeMap.size());
        Assert.assertTrue("The edgeId and edgeId in EdgeIdentifier should match.",
                "e1".equals(edgeMap.get("e1").getEdgeIdentifier().getId()));
        Assert.assertNull("e2 edge should not exist.", edgeMap.get("e2"));
        Assert.assertNull("e3 edge should not exist.", edgeMap.get("e3"));
    }

    @Test
    public void given_immutableAndMutableBonsaiTree_when_evaluate_then_returnNonNullKnot() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        Bonsai<Context> immutableBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key2", "k2")
                .build();
        KeyNode k2 = immutableBuilder.evaluate("key1", Context.builder()
                .documentContext(JsonPath.parse(Maps.newHashMap()))
                .build());
        Assert.assertNotNull(k2);

        bonsai.createKnot(Knot.builder()
                .id("k3")
                .knotData(ValuedKnotData.stringValue("1"))
                .version(123)
                .build());
        bonsai.createMapping("key3", "k3");
        KeyNode k3 = immutableBuilder.evaluate("key3", Context.builder()
                .documentContext(JsonPath.parse(Maps.newHashMap()))
                .build());
        Assert.assertNotNull(k3);
    }

    @Test
    public void given_immutableBonsaiTree_when_evaluateFlat_then_returnNonNullKnot() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final Bonsai<Context> immutableBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key2", "k2")
                .build();
        final FlatTreeRepresentation flatTree = immutableBuilder.evaluateFlat("key1", Context.builder()
                .documentContext(JsonPath.parse(Maps.newHashMap()))
                .build());

        Assert.assertNotNull("FlatTreeRepresentation should not be null for : key1 ",flatTree);
    }

    @Test
    public void given_immutableBonsaiTreeWithNonExistingKey_when_evaluateFlat_then_returnNonNullKnot() {
        final FlatTreeRepresentation flatTree = bonsai.evaluateFlat("key3", Context.builder()
                .documentContext(JsonPath.parse(Maps.newHashMap()))
                .build());

        Assert.assertNotNull("FlatTreeRepresentation should not be null for : key3 ",flatTree);
    }

    @Test
    public void given_immutableBonsaiTree_when_getMapping_then_returnKnotId() {
        final String knotId = bonsai.getMapping("key1");
        Assert.assertEquals("Returned knotId should be : k1", "k1", knotId);
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_createKnot_then_throwBonsaiError() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> build = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.numberValue(1))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.numberValue(1))
                                .version(123)
                                .build())
                .build();

        build.createKnot(Knot.builder()
                             .id("k2")
                             .knotData(ValuedKnotData.numberValue(1))
                             .version(123)
                             .build());
    }

    @Test
    public void given_mutableBonsaiTree_when_createKnot_then_returnNonNullKnot() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .build();

        Knot k2 = bonsai.createKnot(Knot.builder()
                                        .id("k1")
                                        .knotData(ValuedKnotData.stringValue("2"))
                                        .version(123)
                                        .build());
        Assert.assertNotNull(k2);
    }

    @Test(expected = BonsaiError.class)
    public void given_mutableBonsaiTree_when_createKnotWithKnotDataOnly_then_throwBonsaiError() {
        final KnotData knotData = ValuedKnotData.stringValue("knotValue");
        bonsai.createKnot(knotData);
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_updateKnotData_then_throwBonsaiError() {
        final String knotId = "knotOne";
        final KnotData knotData = ValuedKnotData.stringValue("knotValue");
        bonsai.updateKnotData(knotId, knotData);
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_deleteKnot_then_throwBonsaiError() {
        bonsai.deleteKnot("e1", false);
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_createMapping_then_throwBonsaiError() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .build();

        immutable.createMapping("k2", "asdf");
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_createEdge_then_throwBonsaiError() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .build();

        immutable.createEdge(null);
    }

    @Test
    public void given_immutableBonsaiBuilder_when_performingCRUDOperationOnKnotAndEdge_then_returnMeaningfulTree() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k2")
                                .knotData(ValuedKnotData.stringValue("d1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k3")
                                .knotData(ValuedKnotData.stringValue("d3"))
                                .version(123)
                                .build())
                .createKnot(ValuedKnotData.stringValue("2"));
        bonsaiBuilder.updateKnotData("k1", ValuedKnotData.stringValue("3"));
        bonsaiBuilder.deleteKnot("k2", false);
        bonsaiBuilder.createEdge(Edge.builder()
                                     .version(1)
                                     .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                                     .filter(new NotEqualsFilter("$.data", "male"))
                                     .knotId("k3").build());
        bonsaiBuilder.addVariation("k1", Variation.builder()
                                                  .knotId("k3")
                                                  .filter(new NotEqualsFilter("$.data", "male"))
                                                  .priority(1)
                                                  .build());
        Bonsai<Context> immutable = bonsaiBuilder.build();


        Assert.assertNotNull(immutable.getKnot("k1"));
        Assert.assertEquals("3", ((StringValue) ((ValuedKnotData) immutable.getKnot("k1")
                                                                           .getKnotData()).getValue()).getValue());
        Assert.assertNull(immutable.getKnot("k2"));
        Assert.assertNotNull(immutable.getEdge("e1"));

    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_addVariation_then_throwBonsaiError() throws BonsaiError {
        Edge edge1 = bonsai.addVariation("k1",
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId("k2")
                                                  .build());
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_updateEdgeFilters_then_throwBonsaiError() throws BonsaiError {
        Edge edge = bonsai.updateVariation("k1", "e1",
                                           Variation.builder().filters(Lists.newArrayList(new EqualsFilter("$.gender2", "female"))).build());
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_unlinkVariation_then_throwBonsaiError() {
        bonsai.unlinkVariation("knotId", "edgeId");
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_deleteVariation_then_throwBonsaiError() throws BonsaiError {
        TreeEdge treeEdge = bonsai.deleteVariation("k1", "e1", false);
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_createMappingWithKeyE1_then_throwBonsaiError() {
        bonsai.createMapping("e1", ValuedKnotData.stringValue("asdf"));
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_removeMapping_then_throwBonsaiError() {
        bonsai.removeMapping("e1");
    }

    @Test
    public void given_immutableBonsaiTree_when_getCompleteTree_then_returnCompleteTree() {
        final TreeKnot treeKnot = bonsai.getCompleteTree("key1");
        Assert.assertNotNull("TreeKnot should not be null for key1", treeKnot);
        Assert.assertEquals("Treeknot id should be : k1", "k1", treeKnot.getId());
        Assert.assertEquals(123, treeKnot.getVersion());
        Assert.assertNull("TreeKnot has zero TreeEdge for key1", treeKnot.getTreeEdges());
        Assert.assertNotNull("TreeKnot's KnotData should not be null", treeKnot.getKnotData());
    }

    @Test
    public void given_immutableBonsaiTree_when_getCompleteTreeWithDeltaOperations_then_returnCompleteTree() {
        final OrderedList<EdgeIdentifier> orderedList = new OrderedList<>();
        orderedList.add(new EdgeIdentifier("E1", 1, 1));
        orderedList.add(new EdgeIdentifier("E2", 2, 2));

        final List<DeltaOperation> deltaOperationList = Arrays.asList(
                new KnotDeltaOperation(
                    Knot.builder()
                            .edges(orderedList)
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("Value Changed"))
                            .build()
                )
        );
        final TreeKnot treeKnot = bonsai.getCompleteTreeWithDeltaOperations("key1", deltaOperationList);
        Assert.assertNotNull("TreeKnot should not be null for key1", treeKnot);
        Assert.assertEquals("Treeknot id should be : k1", "k1", treeKnot.getId());
        Assert.assertEquals(0, treeKnot.getVersion());
        Assert.assertEquals("TreeKnot has 2 TreeEdge for key1", 2, treeKnot.getTreeEdges().size());
        Assert.assertNotNull("TreeKnot's KnotData should not be null", treeKnot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void given_immutableBonsaiTree_when_applyPendingUpdatesOnCompleteTree_then_throwBonsaiError() {
        bonsai.applyDeltaOperations("key1", new ArrayList<>());
    }
}