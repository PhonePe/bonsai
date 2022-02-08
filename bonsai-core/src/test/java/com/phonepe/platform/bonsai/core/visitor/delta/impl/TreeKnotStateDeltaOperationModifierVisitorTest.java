package com.phonepe.platform.bonsai.core.visitor.delta.impl;

import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.vital.BonsaiProperties;
import com.phonepe.platform.bonsai.core.vital.ComponentBonsaiTreeValidator;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import com.phonepe.platform.bonsai.models.TreeKnotState;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TreeKnotStateDeltaOperationModifierVisitorTest {

    private ComponentBonsaiTreeValidator treeComponentValidator;

    private KnotStore<String, Knot> knotStore;

    private EdgeStore<String, Edge> edgeStore;

    private TreeKnotStateDeltaOperationModifierVisitor treeKnotModifierVisitor;

    @Before
    public void setUp() throws Exception {
        final BonsaiProperties bonsaiProperties = BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .maxAllowedConditionsPerEdge(10)
                .maxAllowedVariationsPerKnot(5)
                .build();
        treeComponentValidator = new ComponentBonsaiTreeValidator(bonsaiProperties);
        knotStore = new InMemoryKnotStore();
        edgeStore = new InMemoryEdgeStore();
        treeKnotModifierVisitor = new TreeKnotStateDeltaOperationModifierVisitor(treeComponentValidator, knotStore, edgeStore);
    }

    @After
    public void tearDown() throws Exception {
        treeKnotModifierVisitor = null;
        knotStore = null;
        edgeStore = null;
        treeComponentValidator = null;
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingKeyMappingDeltaOperationIntoTree_thenReturnTreeKnot() {
        final TreeKnot treeKnot = null;
        final TreeKnotState metaData = new TreeKnotState(treeKnot, null);
        final KeyMappingDeltaOperation keyMappingDeltaData = new KeyMappingDeltaOperation("key", "knotId");
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, keyMappingDeltaData).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals("knotId", returnedTreeKnot.getId());
        assertEquals(0, returnedTreeKnot.getVersion());
        assertNull(returnedTreeKnot.getTreeEdges());
        assertNull(returnedTreeKnot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void given_treeKnotModifierVisitorImpl_when_addingKeyMappingDeltaOperationIntoTree_thenThrowBonsaiError() {
        final TreeKnot treeKnot = TreeKnot.builder()
                .id("K0")
                .build();
        final TreeKnotState metaData = new TreeKnotState(treeKnot, null);
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        final KeyMappingDeltaOperation keyMappingDeltaData = new KeyMappingDeltaOperation("key", "knotId");
        treeKnotModifierVisitor.visit(metaData, keyMappingDeltaData);
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingTopLevelKnotDeltaOperationIntoTree_thenReturnTreeKnot() {
        final TreeKnot treeKnot = TreeKnot.builder()
                .id("K0")
                .build();
        final TreeKnotState metaData = new TreeKnotState(treeKnot, null);
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E1", 1, 1));
        edges.add(new EdgeIdentifier("E2", 2, 2));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K0")
                        .knotData(ValuedKnotData.stringValue("Top Level Knot"))
                        .edges(edges)
                        .build()
        );
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, knotDeltaData).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals("K0", returnedTreeKnot.getId());
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals(2, returnedTreeKnot.getTreeEdges().size());
        assertEquals("E1", returnedTreeKnot.getTreeEdges().get(0).getEdgeIdentifier().getId());
        assertEquals("E2", returnedTreeKnot.getTreeEdges().get(1).getEdgeIdentifier().getId());
        assertEquals("VALUED", returnedTreeKnot.getKnotData().getKnotDataType().toString());
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingLeafLevelKnotDeltaOperationIntoTree_thenReturnTreeKnot() {
        /* Create TreeKnot */
        final TreeKnot leafTreeKnot = TreeKnot.builder()
                .id("K3")
                .build();
        final TreeEdge leafTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                .treeKnot(leafTreeKnot)
                .build();
        final TreeKnot middleLeftTreeKnot = TreeKnot.builder()
                .id("K1")
                .treeEdges(Arrays.asList(leafTreeEdge))
                .knotData(ValuedKnotData.stringValue("Middle Level Left Knot : K1"))
                .build();
        final TreeEdge middleLeftTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build()))
                .treeKnot(middleLeftTreeKnot)
                .build();
        final TreeKnot middleRightTreeKnot = TreeKnot.builder()
                .id("K2")
                .treeEdges(null)
                .knotData(ValuedKnotData.stringValue("Middle Level Right Knot : K2"))
                .build();
        final TreeEdge middleRightTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwo").value("valueTwo").build()))
                .treeKnot(middleRightTreeKnot)
                .build();
        final TreeKnot previousTreeKnot = TreeKnot.builder()
                .id("K0")
                .treeEdges(Arrays.asList(middleRightTreeEdge, middleLeftTreeEdge))
                .knotData(ValuedKnotData.stringValue("Root Level Knot : K0"))
                .build();
        final TreeKnotState metaData = new TreeKnotState(previousTreeKnot, null);

        /* Save knot & edge details into database.*/
        final Knot knotK3 = Knot.builder()
                .id("K3")
                .build();
        final Edge edgeE3 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                .knotId("K3")
                .build();
        final OrderedList<EdgeIdentifier> edgesOfK1 = new OrderedList<>();
        edgesOfK1.add(new EdgeIdentifier("E3", 1, 1));
        final Knot knotK1 = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("Middle Level Left Knot : K1"))
                .edges(edgesOfK1)
                .build();
        final Edge edgeE1 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build()))
                .knotId("K1")
                .build();
        final Knot knotK2 = Knot.builder()
                .id("K2")
                .edges(null)
                .knotData(ValuedKnotData.stringValue("Middle Level Right Knot : K2"))
                .build();
        final Edge edgeE2 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwo").value("valueTwo").build()))
                .knotId("K2")
                .build();
        final OrderedList<EdgeIdentifier> edgesOfK0 = new OrderedList<>();
        edgesOfK0.add(new EdgeIdentifier("E2", 1, 1));
        edgesOfK0.add(new EdgeIdentifier("E1", 1, 1));
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .edges(edgesOfK0)
                .knotData(ValuedKnotData.stringValue("Root Level Knot : K0"))
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        knotStore.mapKnot(knotK1.getId(), knotK1);
        knotStore.mapKnot(knotK2.getId(), knotK2);
        knotStore.mapKnot(knotK3.getId(), knotK3);
        edgeStore.mapEdge(edgeE1.getEdgeIdentifier().getId(), edgeE1);
        edgeStore.mapEdge(edgeE2.getEdgeIdentifier().getId(), edgeE2);
        edgeStore.mapEdge(edgeE3.getEdgeIdentifier().getId(), edgeE3);

        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E4", 1, 1));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K3")
                        .knotData(ValuedKnotData.stringValue("Leaf Level Knot : K3"))
                        .edges(edges)
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, knotDeltaData).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals("K0", returnedTreeKnot.getId());
        assertEquals(2, returnedTreeKnot.getTreeEdges().size());
        assertEquals("VALUED", returnedTreeKnot.getKnotData().getKnotDataType().toString());
        final TreeEdge internalTreeEdgeOne = returnedTreeKnot.getTreeEdges().get(0);
        assertNotNull(internalTreeEdgeOne);
        assertEquals("E2", internalTreeEdgeOne.getEdgeIdentifier().getId());
        assertEquals(0, internalTreeEdgeOne.getVersion());
        assertEquals(1, internalTreeEdgeOne.getFilters().size());
        final TreeKnot internalTreeKnotOne = internalTreeEdgeOne.getTreeKnot();
        assertNotNull(internalTreeKnotOne);
        assertEquals(0, internalTreeKnotOne.getVersion());
        assertEquals("K2", internalTreeKnotOne.getId());
        assertNull(internalTreeKnotOne.getTreeEdges());
        assertEquals("VALUED", internalTreeKnotOne.getKnotData().getKnotDataType().toString());
        final TreeEdge internalTreeEdgeTwo = returnedTreeKnot.getTreeEdges().get(1);
        assertNotNull(internalTreeEdgeTwo);
        assertEquals("E1", internalTreeEdgeTwo.getEdgeIdentifier().getId());
        assertEquals(0, internalTreeEdgeTwo.getVersion());
        assertEquals(1, internalTreeEdgeTwo.getFilters().size());
        final TreeKnot internalTreeKnotTwo = internalTreeEdgeTwo.getTreeKnot();
        assertNotNull(internalTreeKnotTwo);
        assertEquals(0, internalTreeKnotTwo.getVersion());
        assertEquals("K1", internalTreeKnotTwo.getId());
        assertEquals(1, internalTreeKnotTwo.getTreeEdges().size());
        assertEquals("VALUED", internalTreeKnotTwo.getKnotData().getKnotDataType().toString());
        final TreeEdge lowestTreeEdge = internalTreeKnotTwo.getTreeEdges().get(0);
        assertNotNull(lowestTreeEdge);
        assertEquals("E3", lowestTreeEdge.getEdgeIdentifier().getId());
        assertEquals(0, lowestTreeEdge.getVersion());
        assertEquals(1, lowestTreeEdge.getFilters().size());
        final TreeKnot lowestTreeKnot = lowestTreeEdge.getTreeKnot();
        assertNotNull(lowestTreeKnot);
        assertEquals(0, lowestTreeKnot.getVersion());
        assertEquals("K3", lowestTreeKnot.getId());
        assertEquals(1, lowestTreeKnot.getTreeEdges().size());
        assertEquals("VALUED", lowestTreeKnot.getKnotData().getKnotDataType().toString());
        final TreeEdge incompleteTreeEdge = lowestTreeKnot.getTreeEdges().get(0);
        assertEquals("E4", incompleteTreeEdge.getEdgeIdentifier().getId());
        assertEquals(0, incompleteTreeEdge.getVersion());
        assertNull(incompleteTreeEdge.getFilters());
        assertNull(incompleteTreeEdge.getTreeKnot());
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_updatingSomeKnotAndSomeEdgeDeltaOperationIntoTree_thenReturnTreeKnot() {
        /* Create TreeKnot */
        final TreeKnot leafTreeKnot = TreeKnot.builder()
                .id("K3")
                .build();
        final TreeEdge leafTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                .treeKnot(leafTreeKnot)
                .build();
        final TreeKnot middleLeftTreeKnot = TreeKnot.builder()
                .id("K1")
                .treeEdges(Arrays.asList(leafTreeEdge))
                .knotData(ValuedKnotData.stringValue("Middle Level Left Knot : K1"))
                .build();
        final TreeEdge middleLeftTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build()))
                .treeKnot(middleLeftTreeKnot)
                .build();
        final TreeKnot middleRightTreeKnot = TreeKnot.builder()
                .id("K2")
                .treeEdges(null)
                .knotData(ValuedKnotData.stringValue("Middle Level Right Knot : K2"))
                .build();
        final TreeEdge middleRightTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwo").value("valueTwo").build()))
                .treeKnot(middleRightTreeKnot)
                .build();
        final TreeKnot previousTreeKnot = TreeKnot.builder()
                .id("K0")
                .treeEdges(Arrays.asList(middleRightTreeEdge, middleLeftTreeEdge))
                .knotData(ValuedKnotData.stringValue("Root Level Knot : K0"))
                .build();
        final TreeKnotState metaData = new TreeKnotState(previousTreeKnot, null);

        /* Save knot & edge details into database.*/
        final Knot knotK3 = Knot.builder()
                .id("K3")
                .build();
        final Edge edgeE3 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                .knotId("K3")
                .build();
        final OrderedList<EdgeIdentifier> edgesOfK1 = new OrderedList<>();
        edgesOfK1.add(new EdgeIdentifier("E3", 1, 1));
        final Knot knotK1 = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("Middle Level Left Knot : K1"))
                .edges(edgesOfK1)
                .build();
        final Edge edgeE1 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build()))
                .knotId("K1")
                .build();
        final Knot knotK2 = Knot.builder()
                .id("K2")
                .edges(null)
                .knotData(ValuedKnotData.stringValue("Middle Level Right Knot : K2"))
                .build();
        final Edge edgeE2 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwo").value("valueTwo").build()))
                .knotId("K2")
                .build();
        final OrderedList<EdgeIdentifier> edgesOfK0 = new OrderedList<>();
        edgesOfK0.add(new EdgeIdentifier("E2", 1, 1));
        edgesOfK0.add(new EdgeIdentifier("E1", 1, 1));
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .edges(edgesOfK0)
                .knotData(ValuedKnotData.stringValue("Root Level Knot : K0"))
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        knotStore.mapKnot(knotK1.getId(), knotK1);
        knotStore.mapKnot(knotK2.getId(), knotK2);
        knotStore.mapKnot(knotK3.getId(), knotK3);
        edgeStore.mapEdge(edgeE1.getEdgeIdentifier().getId(), edgeE1);
        edgeStore.mapEdge(edgeE2.getEdgeIdentifier().getId(), edgeE2);
        edgeStore.mapEdge(edgeE3.getEdgeIdentifier().getId(), edgeE3);

        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E3", 1, 1));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K1")
                        .knotData(ValuedKnotData.stringValue("Data updated of K1 knot."))
                        .edges(edges)
                        .build()
        );
        final EdgeDeltaOperation edgeDeltaOperation = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                        .knotId("K2")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwoChanged").value("valueTwoChanged").build()))
                        .build()
        );

        treeKnotModifierVisitor.visit(metaData, knotDeltaData);
        TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, edgeDeltaOperation).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals("K0", returnedTreeKnot.getId());
        assertEquals(2, returnedTreeKnot.getTreeEdges().size());
        assertEquals("VALUED", returnedTreeKnot.getKnotData().getKnotDataType().toString());
        final TreeEdge internalTreeEdgeOne = returnedTreeKnot.getTreeEdges().get(0);
        assertNotNull(internalTreeEdgeOne);
        assertEquals("E2", internalTreeEdgeOne.getEdgeIdentifier().getId());
        assertEquals(0, internalTreeEdgeOne.getVersion());
        assertEquals(1, internalTreeEdgeOne.getFilters().size());
        assertEquals("fieldTwoChanged", internalTreeEdgeOne.getFilters().get(0).getField());
        final TreeKnot internalTreeKnotOne = internalTreeEdgeOne.getTreeKnot();
        assertNotNull(internalTreeKnotOne);
        assertEquals(0, internalTreeKnotOne.getVersion());
        assertEquals("K2", internalTreeKnotOne.getId());
        assertNull(internalTreeKnotOne.getTreeEdges());
        assertEquals("VALUED", internalTreeKnotOne.getKnotData().getKnotDataType().toString());
        final TreeEdge internalTreeEdgeTwo = returnedTreeKnot.getTreeEdges().get(1);
        assertNotNull(internalTreeEdgeTwo);
        assertEquals("E1", internalTreeEdgeTwo.getEdgeIdentifier().getId());
        assertEquals(0, internalTreeEdgeTwo.getVersion());
        assertEquals(1, internalTreeEdgeTwo.getFilters().size());
        assertEquals("fieldOne", internalTreeEdgeTwo.getFilters().get(0).getField());
        final TreeKnot internalTreeKnotTwo = internalTreeEdgeTwo.getTreeKnot();
        assertNotNull(internalTreeKnotTwo);
        assertEquals(0, internalTreeKnotTwo.getVersion());
        assertEquals("K1", internalTreeKnotTwo.getId());
        assertEquals(1, internalTreeKnotTwo.getTreeEdges().size());
        assertEquals("VALUED", internalTreeKnotTwo.getKnotData().getKnotDataType().toString());
        final TreeEdge lowestTreeEdge = internalTreeKnotTwo.getTreeEdges().get(0);
        assertNotNull(lowestTreeEdge);
        assertEquals("E3", lowestTreeEdge.getEdgeIdentifier().getId());
        assertEquals(0, lowestTreeEdge.getVersion());
        assertEquals(1, lowestTreeEdge.getFilters().size());
        final TreeKnot lowestTreeKnot = lowestTreeEdge.getTreeKnot();
        assertNotNull(lowestTreeKnot);
        assertEquals(0, lowestTreeKnot.getVersion());
        assertEquals("K3", lowestTreeKnot.getId());
        assertNull(lowestTreeKnot.getTreeEdges());
    }


    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingNonExistingKnotDeltaOperationIntoTree_thenLogError() {
        final TreeKnot treeKnot = TreeKnot.builder()
                .id("K0")
                .build();
        final TreeKnotState metaData = new TreeKnotState(treeKnot, null);
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E1", 1, 1));
        edges.add(new EdgeIdentifier("E2", 2, 2));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K1")
                        .knotData(ValuedKnotData.stringValue("Top Level Knot"))
                        .edges(edges)
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, knotDeltaData).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals("K0", returnedTreeKnot.getId());
        assertNull(returnedTreeKnot.getTreeEdges());
        assertNull(returnedTreeKnot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void given_treeKnotModifierVisitorImpl_when_addingKnotDeltaOperationIntoNonExistingTree_thenThrowBonsaiError() {
        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E1", 1, 1));
        edges.add(new EdgeIdentifier("E2", 2, 2));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K0")
                        .knotData(ValuedKnotData.stringValue("Top Level Knot"))
                        .edges(edges)
                        .build()
        );

        try {
            treeKnotModifierVisitor.visit(new TreeKnotState(null, null), knotDeltaData);
        } catch (BonsaiError e) {
            assertEquals(BonsaiErrorCode.TREE_DOES_NOT_EXIST, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingEdgeDeltaOperationIntoTree_thenReturnTreeKnot() {
        /*Create TreeKnot of it.*/
        final TreeEdge leafTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                .build();
        final TreeKnot middleLeftTreeKnot = TreeKnot.builder()
                .id("K1")
                .treeEdges(null)
                .knotData(ValuedKnotData.stringValue("Middle Level Left Knot : K1"))
                .build();
        final TreeEdge middleLeftTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build()))
                .treeKnot(middleLeftTreeKnot)
                .build();
        final TreeKnot middleRightTreeKnot = TreeKnot.builder()
                .id("K2")
                .treeEdges(Arrays.asList(leafTreeEdge))
                .knotData(ValuedKnotData.stringValue("Middle Level Right Knot : K2"))
                .build();
        final TreeEdge middleRightTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwo").value("valueTwo").build()))
                .treeKnot(middleRightTreeKnot)
                .build();
        final TreeKnot previousTreeKnot = TreeKnot.builder()
                .id("K0")
                .treeEdges(Arrays.asList(middleLeftTreeEdge, middleRightTreeEdge))
                .knotData(ValuedKnotData.stringValue("Root Level Knot : K0"))
                .build();
        final TreeKnotState metaData = new TreeKnotState(previousTreeKnot, null);

        /* Save knot & edge details into database.*/
        final Edge edgeE3 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                .build();
        final Knot knotK1 = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("Middle Level Left Knot : K1"))
                .edges(null)
                .build();
        final Edge edgeE1 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build()))
                .knotId("K1")
                .build();
        final OrderedList<EdgeIdentifier> edgesOfK2 = new OrderedList<>();
        edgesOfK2.add(new EdgeIdentifier("E3", 1, 1));
        final Knot knotK2 = Knot.builder()
                .id("K2")
                .edges(edgesOfK2)
                .knotData(ValuedKnotData.stringValue("Middle Level Right Knot : K2"))
                .build();
        final Edge edgeE2 = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E2", 1, 1))
                .filters(Arrays.asList(EqualsFilter.builder().field("fieldTwo").value("valueTwo").build()))
                .knotId("K2")
                .build();
        final OrderedList<EdgeIdentifier> edgesOfK0 = new OrderedList<>();
        edgesOfK0.add(new EdgeIdentifier("E1", 1, 1));
        edgesOfK0.add(new EdgeIdentifier("E2", 1, 1));
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .edges(edgesOfK0)
                .knotData(ValuedKnotData.stringValue("Root Level Knot : K0"))
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        knotStore.mapKnot(knotK1.getId(), knotK1);
        knotStore.mapKnot(knotK2.getId(), knotK2);
        edgeStore.mapEdge(edgeE1.getEdgeIdentifier().getId(), edgeE1);
        edgeStore.mapEdge(edgeE2.getEdgeIdentifier().getId(), edgeE2);

        final EdgeDeltaOperation edgeDeltaData = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                        .knotId("K3")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, edgeDeltaData).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals("K0", returnedTreeKnot.getId());
        assertEquals(2, returnedTreeKnot.getTreeEdges().size());
        assertEquals("VALUED", returnedTreeKnot.getKnotData().getKnotDataType().toString());
        final TreeEdge internalTreeEdgeOne = returnedTreeKnot.getTreeEdges().get(0);
        assertNotNull(internalTreeEdgeOne);
        assertEquals("E1", internalTreeEdgeOne.getEdgeIdentifier().getId());
        assertEquals(0, internalTreeEdgeOne.getVersion());
        assertEquals(1, internalTreeEdgeOne.getFilters().size());
        final TreeKnot internalTreeKnotOne = internalTreeEdgeOne.getTreeKnot();
        assertNotNull(internalTreeKnotOne);
        assertEquals(0, internalTreeKnotOne.getVersion());
        assertEquals("K1", internalTreeKnotOne.getId());
        assertNull(internalTreeKnotOne.getTreeEdges());
        assertEquals("VALUED", internalTreeKnotOne.getKnotData().getKnotDataType().toString());
        final TreeEdge internalTreeEdgeTwo = returnedTreeKnot.getTreeEdges().get(1);
        assertNotNull(internalTreeEdgeTwo);
        assertEquals("E2", internalTreeEdgeTwo.getEdgeIdentifier().getId());
        assertEquals(0, internalTreeEdgeTwo.getVersion());
        assertEquals(1, internalTreeEdgeTwo.getFilters().size());
        final TreeKnot internalTreeKnotTwo = internalTreeEdgeTwo.getTreeKnot();
        assertNotNull(internalTreeKnotTwo);
        assertEquals(0, internalTreeKnotTwo.getVersion());
        assertEquals("K2", internalTreeKnotTwo.getId());
        assertEquals(1, internalTreeKnotTwo.getTreeEdges().size());
        assertEquals("VALUED", internalTreeKnotTwo.getKnotData().getKnotDataType().toString());
        final TreeEdge lowestTreeEdge = internalTreeKnotTwo.getTreeEdges().get(0);
        assertEquals("E3", lowestTreeEdge.getEdgeIdentifier().getId());
        assertEquals(0, lowestTreeEdge.getVersion());
        assertEquals(1, lowestTreeEdge.getFilters().size());
        final TreeKnot lowestTreeKnot = lowestTreeEdge.getTreeKnot();
        assertNotNull(lowestTreeKnot);
        assertEquals(0, lowestTreeKnot.getVersion());
        assertEquals("K3", lowestTreeKnot.getId());
        assertNull(lowestTreeKnot.getTreeEdges());
        assertNull(lowestTreeKnot.getKnotData());
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingNonExistingEdgeDeltaOperationIntoTree_thenLogError() {
        final TreeKnot treeKnot = TreeKnot.builder()
                .id("K0")
                .build();
        final TreeKnotState metaData = new TreeKnotState(treeKnot, null);
        final Knot knotK0 = Knot.builder()
                .id("K0")
                .build();
        knotStore.mapKnot(knotK0.getId(), knotK0);
        final EdgeDeltaOperation edgeDeltaData = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                        .knotId("K1")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(metaData, edgeDeltaData).getTreeKnot();

        assertNotNull(returnedTreeKnot);
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals("K0", returnedTreeKnot.getId());
        assertNull(returnedTreeKnot.getTreeEdges());
        assertNull(returnedTreeKnot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void given_treeKnotModifierVisitorImpl_when_addingEdgeDeltaOperationIntoNonExistingTree_thenThrowBonsaiError() {
        final EdgeDeltaOperation edgeDeltaData = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                        .knotId("K3")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                        .build()
        );

        try {
            treeKnotModifierVisitor.visit(new TreeKnotState(null, null), edgeDeltaData);
        } catch (BonsaiError e) {
            assertEquals(BonsaiErrorCode.TREE_DOES_NOT_EXIST, e.getErrorCode());
            throw e;
        }
    }
}