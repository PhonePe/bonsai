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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.core.ObjectExtractor;
import com.phonepe.commons.bonsai.json.eval.Parsers;
import com.phonepe.commons.bonsai.core.exception.BonsaiError;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.blocks.Variation;
import com.phonepe.commons.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.commons.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.commons.bonsai.models.data.KnotDataVisitor;
import com.phonepe.commons.bonsai.models.data.MapKnotData;
import com.phonepe.commons.bonsai.models.data.MultiKnotData;
import com.phonepe.commons.bonsai.models.data.ValuedKnotData;
import com.phonepe.commons.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.GenericFilter;
import com.phonepe.commons.query.dsl.logical.OrFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BonsaiTreeOperationTest {

    private Bonsai<Context> bonsai;

    @BeforeEach
    void setUp() {
        this.bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties
                        .builder()
                        .maxAllowedConditionsPerEdge(1)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();

    }

    @AfterEach
    void tearDown() {
        this.bonsai = null;
    }

    @Test
    void testCreateMapping() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .build(), null);

        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Knot icon3 = bonsai.createKnot(ValuedKnotData.stringValue("This is some coool icon"), null);
        /* there is no older mapping, hence it will return null */
        Knot iconThree = bonsai.createMapping("icon_3", icon3.getId());
        Assertions.assertNull(iconThree);

        /* now mappings exist, hence it will return the older knot */
        Knot iconThreeOne = bonsai.createMapping("icon_3", icon3.getId());
        Assertions.assertNotNull(iconThreeOne);
        Assertions.assertEquals(icon3, iconThreeOne);

        /* now mappings exist, hence it will return the older knot */
        iconThreeOne = bonsai.createMapping("icon_3", widgetKnot1.getId());
        Assertions.assertNotNull(iconThreeOne);
        Assertions.assertEquals(icon3, iconThreeOne);


        /* now mappings exist, hence it will return the older knot */
        iconThreeOne = bonsai.createMapping("icon_3", icon3.getId());
        Assertions.assertNotNull(iconThreeOne);
        Assertions.assertEquals(widgetKnot1, iconThreeOne);

        String knotId = bonsai.getMapping("icon_3");
        Assertions.assertEquals(icon3.getId(), knotId);

        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        bonsai.createMapping("widget_4", ValuedKnotData.stringValue("widget_4"), null);
        Knot homePageKnot = bonsai.createKnot(MapKnotData.builder()
                .mapKeys(ImmutableMap.of("w1", "widget_1",
                        "w2", "widget_2",
                        "w3", "widget_3",
                        "w4", "widget_4"))
                .build(), null);
        Assertions.assertNull(bonsai.createMapping("home_page_1", homePageKnot.getId()));
    }

    @Test
    void testCreateMappingError() {
        assertThrows(BonsaiError.class, () -> bonsai.createMapping("widget_1", "randomUnknownId"));
    }

    @Test
    void testUpdateKnotData() {
        bonsai.createMapping("widget_1", ValuedKnotData.stringValue("widget_1"), null);
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        Knot homePageKnot = bonsai.createKnot(MapKnotData.builder()
                .mapKeys(ImmutableMap.of("w1", "widget_1",
                        "w2", "widget_2"))
                .build(), null);
        MapKnotData newMapKnotData = MapKnotData.builder()
                .mapKeys(ImmutableMap.of("w3", "widget_3"))
                .build();
        Knot previousKnot = bonsai.updateKnotData(homePageKnot.getId(), newMapKnotData, new HashMap<>());
        Assertions.assertEquals(homePageKnot, previousKnot);
        Knot updatedKnot = bonsai.getKnot(homePageKnot.getId());
        Assertions.assertEquals(newMapKnotData, updatedKnot.getKnotData());
    }


    @Test
    void testAddVariationNoEdge() {
        assertThrows(BonsaiError.class, () -> bonsai.addVariation("someInvalidKnotId", Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId("asdf")
                .build()));
    }


    @Test
    void testInvalidInput() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
            bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
            bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
            bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
            Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                    .key("icon_1")
                    .key("icon_4")
                    .key("icon_2")
                    .key("icon_3")
                    .build(), null);
            bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
            bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
            bonsai.createMapping("home_page_1", MultiKnotData.builder()
                    .key("widget_1")
                    .key("widget_2")
                    .key("widget_3")
                    .build(), null);
            Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                    .key("icon_3")
                    .key("icon_1")
                    .key("icon_4")
                    .build(), null);
            Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                    .knotId(femaleConditionKnot.getId())
                    .build()));
        });
    }

    @Test
    void testInvalidInputNegativePriority() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
            bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
            bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
            bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
            Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                    .key("icon_1")
                    .key("icon_4")
                    .key("icon_2")
                    .key("icon_3")
                    .build(), null);
            bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
            bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
            bonsai.createMapping("home_page_1", MultiKnotData.builder()
                    .key("widget_1")
                    .key("widget_2")
                    .key("widget_3")
                    .build(), null);
            Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                    .key("icon_3")
                    .key("icon_1")
                    .key("icon_4")
                    .build(), null);
            Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                    .knotId(femaleConditionKnot.getId())
                    .filter(new EqualsFilter("$.gender", "female"))
                    .priority(-2)
                    .build()));
        });
    }

    @Test
    void testPivotCheck() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
            bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
            bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
            bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
            Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                    .key("icon_1")
                    .key("icon_4")
                    .key("icon_2")
                    .key("icon_3")
                    .build(), null);
            bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
            bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
            bonsai.createMapping("home_page_1", MultiKnotData.builder()
                    .key("widget_1")
                    .key("widget_2")
                    .key("widget_3")
                    .build(), null);
            Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                    .key("icon_3")
                    .key("icon_1")
                    .key("icon_4")
                    .build(), null);
            bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                    .filter(new EqualsFilter("$.gender", "female"))
                    .knotId(femaleConditionKnot.getId())
                    .build());
            bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                    .filter(new EqualsFilter("$.someOtherPivot", "female"))
                    .knotId(femaleConditionKnot.getId())
                    .build());
        });
    }

    @Test
    void testPivotCheck2() {
        Bonsai<Context> newBonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties
                        .builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();
        newBonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        newBonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        newBonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        newBonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        newBonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        newBonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        Knot femaleConditionKnot = newBonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_4")
                .build(), null);
        Knot widgetKnot1 = newBonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);

        newBonsai.createMapping("home_page_1", MultiKnotData.builder()
                .key("widget_1")
                .key("widget_2")
                .key("widget_3")
                .build(), null);
        Edge female = newBonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(femaleConditionKnot.getId())
                .build());
        Edge edge = newBonsai.updateVariation(widgetKnot1.getId(), female.getEdgeIdentifier().getId(),
                Variation.builder().filters(Lists.newArrayList(OrFilter.builder()
                        .filter(new EqualsFilter("$.gender", "female"))
                        .filter(GenericFilter.builder()
                                .field("$.gender")
                                .value("SDf")
                                .build()).build())).build());
        Assertions.assertNotNull(edge);
    }

    @Test
    void testCycleDependencyCheck() {
        assertThrows(BonsaiError.class, () -> {
            new ObjectExtractor().getObject("userData1.json", Map.class);
            bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
            bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
            bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
            bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
            Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                    .key("icon_1")
                    .key("icon_4")
                    .key("icon_2")
                    .key("icon_3")
                    .build(), null);
            bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
            bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
            Knot homePageKnot = bonsai.createMapping("home_page_1", MultiKnotData.builder()
                    .key("widget_1")
                    .key("widget_2")
                    .key("widget_3")
                    .build(), null);
            Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                    .key("icon_3")
                    .key("icon_1")
                    .key("icon_4")
                    .build(), null);
            Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                    .filter(new EqualsFilter("$.gender", "female"))
                    .knotId(femaleConditionKnot.getId())
                    .build()));
            Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                    .filter(new EqualsFilter("$.gender", "male"))
                    .knotId(homePageKnot.getId())
                    .build()));
        });
    }

    @Test
    void testFlatEval() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        bonsai.createMapping("home_page_1", ValuedKnotData.stringValue("4"), null);
        MultiKnotData build = MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("home_page_1")
                .build();
        build.setKeys(Lists.newArrayList("ads"));
        FlatTreeRepresentation widget = bonsai.evaluateFlat("widget_1", Context
                .builder()
                .documentContext(Parsers.parse(Maps.newHashMap()))
                .build());
        Assertions.assertEquals("widget_1", widget.getRoot());
    }

    @Test
    void testGetCompleteTree() throws IOException {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        Knot hpKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("widget_1")
                .key("widget_2")
                .key("widget_3")
                .build(), null);

        bonsai.createMapping("home_page_1", hpKnot.getId());

        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_4")
                .build(), null);

        Knot innerKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_4")
                .build(), null);

        bonsai.addVariation(femaleConditionKnot.getId(), Variation.builder().knotId(innerKnot.getId())
                .filter(new EqualsFilter("$.gender", "female"))
                .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assertions.assertNotNull(femaleConditionKnot);
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(femaleConditionKnot.getId())
                .build());
        TreeKnot widgetOne = bonsai.getCompleteTree("widget_1");

        System.out.println(Parsers.MAPPER.writeValueAsString(widgetOne));
        Assertions.assertEquals(widgetOne.getId(), widgetKnot1.getId());
        Assertions.assertEquals(widgetOne.getTreeEdges().get(0).getTreeKnot().getId(), femaleConditionKnot.getId());
        Assertions.assertEquals(widgetOne.getTreeEdges().get(0).getTreeKnot().getTreeEdges().get(0)
                .getTreeKnot().getId(), innerKnot.getId());
    }

    @Test
    void testBonsaiDeleteVariationRecursive() throws IOException, BonsaiError {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        bonsai.createMapping("icon_5", ValuedKnotData.stringValue("4"), null);
        bonsai.createMapping("female2", ValuedKnotData.stringValue("female2"), null);
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_5")
                .build(), null);

        Knot leafKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("female2")
                .build(), null);


        Knot leafKnot2 = bonsai.createKnot(MultiKnotData.builder()
                .key("female2")
                .build(), null);

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assertions.assertNotNull(femaleConditionKnot);

        /* checking multiple additions */
        Edge femaleVariationEdge = bonsai.addVariation(widgetKnot1.getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender", "female"))
                        .knotId(femaleConditionKnot.getId())
                        .build());

        Edge leafKnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender2", "female"))
                        .knotId(leafKnot.getId())
                        .build());

        Edge leaf2KnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender2", "male"))
                        .knotId(leafKnot2.getId())
                        .build());


        /* Tree should have both knots and 1 edge */
        TreeKnot widgetOne = bonsai.getCompleteTree("widget_1");
        Assertions.assertFalse(widgetOne.getTreeEdges().isEmpty());
        widgetOne.getTreeEdges().get(0).getTreeKnot().getKnotData().accept(new KnotDataVisitor<>() {
            @Override
            public Object visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Object visit(MultiKnotData multiKnotData) {
                Assertions.assertTrue(multiKnotData.getKeys().contains("icon_5"));
                return null;
            }

            @Override
            public Object visit(MapKnotData mapKnotData) {
                return null;
            }
        });
        Assertions.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assertions.assertNotNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));
        Assertions.assertNotNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));

        System.out.println(Parsers.MAPPER.writeValueAsString(widgetOne));
        TreeEdge treeEdge = bonsai.deleteVariation(widgetKnot1.getId(), femaleVariationEdge.getEdgeIdentifier()
                .getId(), true);
        System.out.println(Parsers.MAPPER.writeValueAsString(treeEdge));
        widgetOne = bonsai.getCompleteTree("widget_1");
        System.out.println(Parsers.MAPPER.writeValueAsString(widgetOne));
        Assertions.assertNotNull(widgetOne.getKnotData());
        Assertions.assertTrue(widgetOne.getTreeEdges().isEmpty());
        Assertions.assertNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assertions.assertNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));
        Assertions.assertNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));
        Assertions.assertNull(bonsai.getEdge(leaf2KnotEdge.getEdgeIdentifier().getId()));
    }

    @Test
    void testBonsaiDeleteVariationNonRecursive() throws IOException, BonsaiError {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        bonsai.createMapping("icon_5", ValuedKnotData.stringValue("5"), null);
        bonsai.createMapping("female2", ValuedKnotData.stringValue("female 2"), null);
        bonsai.createMapping("female3", ValuedKnotData.stringValue("female 3"), null);
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_5")
                .build(), null);

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);


        Knot leafKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("female2")
                .build(), null);


        Knot leafKnot2 = bonsai.createKnot(MultiKnotData.builder()
                .key("female3")
                .build(), null);

        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assertions.assertNotNull(femaleConditionKnot);

        /* checking multiple additions */
        Edge femaleVariationEdge = bonsai.addVariation(widgetKnot1.getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender", "female"))
                        .knotId(femaleConditionKnot.getId())
                        .build());

        Edge leafKnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender2", "female"))
                        .knotId(leafKnot.getId())
                        .build());

        Edge leaf2KnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender2", "male"))
                        .knotId(leafKnot2.getId())
                        .build());

        /* Tree should have both knots and 1 edge */
        TreeKnot widgetOne = bonsai.getCompleteTree("widget_1");
        Assertions.assertFalse(widgetOne.getTreeEdges().isEmpty());
        widgetOne.getTreeEdges().get(0).getTreeKnot().getKnotData().accept(new KnotDataVisitor<>() {
            @Override
            public Object visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Object visit(MultiKnotData multiKnotData) {
                Assertions.assertTrue(multiKnotData.getKeys().contains("icon_5"));
                return null;
            }

            @Override
            public Object visit(MapKnotData mapKnotData) {
                return null;
            }
        });
        Assertions.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assertions.assertNotNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));

        System.out.println(Parsers.MAPPER.writeValueAsString(widgetOne));

        /* deleting variation non recursively */
        TreeEdge treeEdge = bonsai.deleteVariation(widgetKnot1.getId(), femaleVariationEdge.getEdgeIdentifier()
                .getId(), false);


        System.out.println(Parsers.MAPPER.writeValueAsString(treeEdge));
        widgetOne = bonsai.getCompleteTree("widget_1");
        System.out.println(Parsers.MAPPER.writeValueAsString(widgetOne));
        Assertions.assertNotNull(widgetOne.getKnotData());
        /* edge must be deleted */
        Assertions.assertTrue(widgetOne.getTreeEdges().isEmpty());
        Assertions.assertNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));

        /* other edges must exist */
        Assertions.assertNotNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));
        Assertions.assertNotNull(bonsai.getEdge(leaf2KnotEdge.getEdgeIdentifier().getId()));

        /* knot should still exist */
        Assertions.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
    }
}