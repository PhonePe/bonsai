package com.phonepe.platform.bonsai.core.visitor.delta.impl;

import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.BonsaiProperties;
import com.phonepe.platform.bonsai.core.vital.ComponentBonsaiTreeValidator;
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

/**
 * @author - suraj.s
 * @date - 2019-11-20
 */
public class TreeKnotDeltaOperationModifierTest {

    private ComponentBonsaiTreeValidator treeComponentValidator;

    private TreeKnotDeltaOperationModifier treeKnotModifierVisitor;

    @Before
    public void setUp() throws Exception {
        final BonsaiProperties bonsaiProperties = BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .maxAllowedConditionsPerEdge(10)
                .maxAllowedVariationsPerKnot(5)
                .build();
        treeComponentValidator = new ComponentBonsaiTreeValidator(bonsaiProperties);
        treeKnotModifierVisitor = new TreeKnotDeltaOperationModifier(treeComponentValidator);
    }

    @After
    public void tearDown() throws Exception {
        treeKnotModifierVisitor = null;
        treeComponentValidator = null;
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingKeyMappingDeltaOperationIntoTree_thenReturnTreeKnot() {
        final TreeKnot treeKnot = null;
        final KeyMappingDeltaOperation keyMappingDeltaData = new KeyMappingDeltaOperation("key", "knotId");
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(treeKnot, keyMappingDeltaData);

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
        final KeyMappingDeltaOperation keyMappingDeltaData = new KeyMappingDeltaOperation("key", "knotId");
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(treeKnot, keyMappingDeltaData);
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingTopLevelKnotDeltaOperationIntoTree_thenReturnTreeKnot() {
        final TreeKnot treeKnot = TreeKnot.builder()
                .id("K0")
                .build();
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
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(treeKnot, knotDeltaData);

        assertNotNull(returnedTreeKnot);
        assertEquals("K0",returnedTreeKnot.getId());
        assertEquals(0, returnedTreeKnot.getVersion());
        assertEquals(2, returnedTreeKnot.getTreeEdges().size());
        assertEquals("E1", returnedTreeKnot.getTreeEdges().get(0).getEdgeIdentifier().getId());
        assertEquals("E2", returnedTreeKnot.getTreeEdges().get(1).getEdgeIdentifier().getId());
        assertEquals("VALUED", returnedTreeKnot.getKnotData().getKnotDataType().toString());
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingLeafLevelKnotDeltaOperationIntoTree_thenReturnTreeKnot() {
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

        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E4", 1, 1));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K3")
                        .knotData(ValuedKnotData.stringValue("Leaf Level Knot : K3"))
                        .edges(edges)
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(previousTreeKnot, knotDeltaData);

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
    public void given_treeKnotModifierVisitorImpl_when_addingNonExistingKnotDeltaOperationIntoTree_thenLogError() {
        final TreeKnot treeKnot = TreeKnot.builder()
                .id("K0")
                .build();
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
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(treeKnot, knotDeltaData);

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
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(null, knotDeltaData);
    }

    @Test
    public void given_treeKnotModifierVisitorImpl_when_addingEdgeDeltaOperationIntoTree_thenReturnTreeKnot() {
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

        final EdgeDeltaOperation edgeDeltaData = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E3", 1, 1))
                        .knotId("K3")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(previousTreeKnot, edgeDeltaData);

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
        final EdgeDeltaOperation edgeDeltaData = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                        .knotId("K1")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                        .build()
        );

        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(treeKnot, edgeDeltaData);

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
        final TreeKnot returnedTreeKnot = treeKnotModifierVisitor.visit(null, edgeDeltaData);
    }
}