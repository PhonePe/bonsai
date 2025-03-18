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

package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableList;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.InFilter;
import com.phonepe.commons.query.dsl.general.NotEqualsFilter;
import com.phonepe.commons.query.dsl.general.NotInFilter;
import com.phonepe.commons.query.dsl.logical.AndFilter;
import com.phonepe.commons.query.dsl.logical.NotFilter;
import com.phonepe.commons.query.dsl.logical.OrFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.commons.query.dsl.numeric.LessEqualFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentBonsaiTreeValidatorTest {

    private final ComponentBonsaiTreeValidator componentValidator
            = new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
            .mutualExclusivitySettingTurnedOn(true)
            .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
            .maxAllowedVariationsPerKnot(Integer.MAX_VALUE)
            .build());

    @Test
    void validateEdge() {
        assertThrows(BonsaiError.class, () -> componentValidator.validate(Edge.builder().build()));
    }

    @Test
    void validateEdgeErrorOnNegativeVersion() {
        assertThrows(BonsaiError.class, () -> componentValidator.validate(Edge.builder().edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                .version(-1).build()));
    }

    @Test
    void validateEdgeErrorOnNegativePriority() {
        assertThrows(BonsaiError.class, () -> componentValidator.validate(Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("id1", 1, -1))
                .version(1).build()));
    }

    @Test
    void validateEdgeErrorWhenFiltersContainMultipleFields() {
        assertThrows(BonsaiError.class, () -> componentValidator.validate(Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                .version(1)
                .knotId("knotId1")
                .filter(new GreaterEqualFilter("field1", 123))
                .filter(new LessEqualFilter("field2", 123))
                .build()));
    }

    @Test
    void given_twoDifferentKnots_when_validating_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            final Knot knotOne = Knot.builder()
                    .id("k1")
                    .version(1)
                    .knotData(ValuedKnotData.stringValue("string one"))
                    .build();
            final Map<String, String> mapKeys = new HashMap<>();
            mapKeys.put("key1", "key1");
            final Knot knotTwo = Knot.builder()
                    .id("k2")
                    .version(3)
                    .knotData(MapKnotData.builder().mapKeys(mapKeys).build())
                    .build();
            componentValidator.validate(knotOne, knotTwo);
        });
    }

    @Test
    void validateEdgeNoErrorWhenFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                .version(1)
                .knotId("knotId1")
                .filter(new GreaterEqualFilter("field1", 123))
                .filter(new LessEqualFilter("field1", 300))
                .build());
    }


    @Test
    void validateEdgeErrorOnSingleConditionEdgeSettingOnAndMultipleFiltersSet() {
        assertThrows(BonsaiError.class, () -> new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .build())
                .validate(Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                        .version(1)
                        .knotId("knotId1")
                        .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                new GreaterEqualFilter("field1", 121))))
                        .build()));
    }

    @Test
    void validateEdgeNoErrorOnInnerFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                .version(1)
                .knotId("knotId1")
                .filter(new OrFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                        new GreaterEqualFilter("field1", 121))))
                .build());
    }

    @Test
    void validateEdgeErrorOnInnerFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                .version(1)
                .knotId("knotId1")
                .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                        new GreaterEqualFilter("field1", 121))))
                .build());
    }

    @Test
    void validateEdgeErrorOnInnerFiltersContainSameField2() {
        componentValidator.validate(Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                .version(1)
                .knotId("knotId1")
                .filter(new EqualsFilter("field1", 100))
                .filter(new NotFilter(new GreaterEqualFilter("field1", 123)))
                .build());
    }

    @Test
    void validateVariationMutuality() {
        assertThrows(BonsaiError.class, () -> componentValidator.validate(Variation.builder()
                .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                        new GreaterEqualFilter("field2", 121))))

                .knotId("knotId1")
                .priority(1)
                .build()));
    }

    @Test
    void validateVariationSingleCondition() {
        assertThrows(BonsaiError.class, () -> new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .build())
                .validate(Variation.builder()
                        .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                new GreaterEqualFilter("field1", 123))))
                        .knotId("knotId1")
                        .priority(1)
                        .build()));
    }

    @Test
    void validateKnotError() {
        assertThrows(BonsaiError.class, () -> new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .build())
                .validate(Knot.builder()
                        .id("k1")
                        .version(1)
                        .build()));
    }

    @Test
    void validateKnotValid() {
        new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .build())
                .validate(Knot.builder()
                        .id("k1")
                        .version(1)
                        .knotData(new ValuedKnotData())
                        .build());
    }

    @Test
    void validateKnotInValid() {
        assertThrows(BonsaiError.class, () -> new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .build())
                .validate(Knot.builder()
                        .id("k1")
                        .version(1)
                        .knotData(new MapKnotData())
                        .build()));
    }


    @Test
    void Given_RootValuedKnotAndInternalMapKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final Map<String, String> mapKeys = new HashMap<>();
        mapKeys.put("key1", "value1");
        mapKeys.put("key2", "value2");
        final MapKnotData mapKnotData = MapKnotData.builder()
                .mapKeys(mapKeys)
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(mapKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();

        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootMapKnotAndInternalValuedKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("valueKnotId")
                .knotData(valuedKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final Map<String, String> mapKeys = new HashMap<>();
        mapKeys.put("key1", "value1");
        mapKeys.put("key2", "value2");
        final MapKnotData mapKnotData = MapKnotData.builder()
                .mapKeys(mapKeys)
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .treeEdges(treeEdges)
                .knotData(mapKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootValuedKnotAndInternalMultiKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");
        final MultiKnotData multiKnotData = MultiKnotData.builder()
                .keys(keys)
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("multiKnotId")
                .knotData(multiKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();

        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootMultiKnotAndInternalValuedKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("valueKnotId")
                .knotData(valuedKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");
        final MultiKnotData multiKnotData = MultiKnotData.builder()
                .keys(keys)
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("multiKnotId")
                .treeEdges(treeEdges)
                .knotData(multiKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootMultiKnotAndInternalMapKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final Map<String, String> mapKeys = new HashMap<>();
        mapKeys.put("key1", "value1");
        mapKeys.put("key2", "value2");
        final MapKnotData mapKnotData = MapKnotData.builder()
                .mapKeys(mapKeys)
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(mapKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");
        final MultiKnotData multiKnotData = MultiKnotData.builder()
                .keys(keys)
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("multiKnotId")
                .treeEdges(treeEdges)
                .knotData(multiKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootMapKnotAndInternalMultiKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");
        final MultiKnotData multiKnotData = MultiKnotData.builder()
                .keys(keys)
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("multiKnotId")
                .knotData(multiKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final Map<String, String> mapKeys = new HashMap<>();
        mapKeys.put("key1", "value1");
        mapKeys.put("key2", "value2");
        final MapKnotData mapKnotData = MapKnotData.builder()
                .mapKeys(mapKeys)
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .treeEdges(treeEdges)
                .knotData(mapKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootStringValuedKnotAndInternalNumberValuedKnot_When_ValidatingTreeKnot_ThenThrowBonsaiError() {
        final ValuedKnotData numberKnotData = ValuedKnotData.builder()
                .value(new NumberValue(9))
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(numberKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("field", "value"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.INVALID_INPUT, e.getErrorCode());
        }
    }

    @Test
    void Given_RootStringValuedKnotAndInternalStringValuedKnot_When_ValidatingTreeKnotWithMutualExclusionOn_ThenThrowBonsaiError() {
        final ValuedKnotData numberKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(numberKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("fieldOne", "valueOne"));
        filters.add(new NotEqualsFilter("fieldTwo", "valueTwo"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR, e.getErrorCode());
        }
    }

    @Test
    void Given_RootStringValuedKnotAndInternalStringValuedKnot_When_ValidatingTreeKnotWithMutualExclusionOff_ThenReturnNothing() {
        final ValuedKnotData numberKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final TreeKnot internalTreeKnot = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(numberKnotData)
                .build();

        final List<Filter> filters = new ArrayList<>();
        filters.add(new EqualsFilter("fieldOne", "valueOne"));
        filters.add(new NotEqualsFilter("fieldTwo", "valueTwo"));
        final TreeEdge internalTreeEdge = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeId", 1, 1))
                .filters(filters)
                .treeKnot(internalTreeKnot)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdge);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        final ComponentBonsaiTreeValidator componentValidator =
                getComponentBonsaiTreeValidator(Integer.MAX_VALUE, Integer.MAX_VALUE, false);

        componentValidator.validate(rootTreeKnot);

        Assertions.assertNotNull(rootTreeKnot);
    }

    @Test
    void Given_TwoInternalStringValuedKnotAndRootStringValuedKnot_When_ValidatingTreeKnotWithMutualExclusionOn_ThenThrowBonsaiError() {
        final ValuedKnotData stringKnotDataOne = ValuedKnotData.builder()
                .value(new StringValue("string value One"))
                .build();
        final TreeKnot internalTreeKnotOne = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(stringKnotDataOne)
                .build();
        final List<Filter> filtersOne = new ArrayList<>();
        filtersOne.add(new EqualsFilter("fieldOne", "valueOne"));
        final TreeEdge internalTreeEdgeOne = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeIdOne", 1, 1))
                .filters(filtersOne)
                .treeKnot(internalTreeKnotOne)
                .build();

        final ValuedKnotData stringKnotDataTwo = ValuedKnotData.builder()
                .value(new StringValue("string value Two"))
                .build();
        final TreeKnot internalTreeKnotTwo = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(stringKnotDataTwo)
                .build();
        final List<Filter> filtersTwo = new ArrayList<>();
        filtersTwo.add(new EqualsFilter("fieldTwo", "valueTwo"));
        final TreeEdge internalTreeEdgeTwo = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeIdTwo", 2, 2))
                .filters(filtersTwo)
                .treeKnot(internalTreeKnotTwo)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdgeOne);
        treeEdges.add(internalTreeEdgeTwo);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        try {
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR, e.getErrorCode());
        }
    }

    @Test
    void Given_TwoInternalStringValuedKnotAndRootStringValuedKnot_When_ValidatingTreeKnotWithMutualExclusionOff_ThenReturnNothing() {
        final ValuedKnotData stringKnotDataOne = ValuedKnotData.builder()
                .value(new StringValue("string value One"))
                .build();
        final TreeKnot internalTreeKnotOne = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(stringKnotDataOne)
                .build();
        final List<Filter> filtersOne = new ArrayList<>();
        filtersOne.add(new EqualsFilter("fieldOne", "valueOne"));
        final TreeEdge internalTreeEdgeOne = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeIdOne", 1, 1))
                .filters(filtersOne)
                .treeKnot(internalTreeKnotOne)
                .build();

        final ValuedKnotData stringKnotDataTwo = ValuedKnotData.builder()
                .value(new StringValue("string value Two"))
                .build();
        final TreeKnot internalTreeKnotTwo = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(stringKnotDataTwo)
                .build();
        final List<Filter> filtersTwo = new ArrayList<>();
        filtersTwo.add(new EqualsFilter("fieldTwo", "valueTwo"));
        final TreeEdge internalTreeEdgeTwo = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeIdTwo", 2, 2))
                .filters(filtersTwo)
                .treeKnot(internalTreeKnotTwo)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdgeOne);
        treeEdges.add(internalTreeEdgeTwo);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        final ComponentBonsaiTreeValidator componentValidator =
                getComponentBonsaiTreeValidator(Integer.MAX_VALUE, Integer.MAX_VALUE, false);

        componentValidator.validate(rootTreeKnot);

        Assertions.assertNotNull(rootTreeKnot);
    }


    /**
     * Adding the documentation since this is little heavy tree.
     * ________
     * | VALUED |
     * | Value1 |
     * --------
     * fieldOne = valueOne  /          \ fieldOne IN [valueTwo, valueThree]
     * /            \
     * --------        --------
     * | VALUED |      | VALUED |
     * | Value2 |      | Value4 |
     * --------        --------
     * fieldTwo = valueTwo |               | fieldThree NOT_IN [valueThree, valueFour]
     * |               |
     * --------        --------
     * | VALUED |      | VALUED |
     * | Value3 |      | Value5 |
     * --------        --------
     * <p>
     * And this test-case should pass even with the mutual exclusion flag is set to true.
     */
    @Test
    void Given_HeavyTreeKnot_When_ValidatingTreeKnotWithMutualExclusionOn_ThenReturnNothing() {
        final ValuedKnotData treeKnotThirdValue = ValuedKnotData.builder()
                .value(new StringValue("Value3"))
                .build();
        final TreeKnot treeKnotThird = TreeKnot.builder()
                .id("treeKnotThirdId")
                .treeEdges(null)
                .knotData(treeKnotThirdValue)
                .build();
        final List<Filter> treeEdgeThirdFilters = new ArrayList<>();
        treeEdgeThirdFilters.add(new EqualsFilter("fieldTwo", "valueTwo"));
        final TreeEdge treeEdgeThird = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("treeEdgeThirdId", 1, 1))
                .filters(treeEdgeThirdFilters)
                .treeKnot(treeKnotThird)
                .build();

        final ValuedKnotData treeKnotFifthValue = ValuedKnotData.builder()
                .value(new StringValue("Value5"))
                .build();
        final TreeKnot treeKnotFifth = TreeKnot.builder()
                .id("treeKnotFifthId")
                .treeEdges(null)
                .knotData(treeKnotFifthValue)
                .build();
        final List<Filter> treeEdgeFifthFilters = new ArrayList<>();
        final List<String> valueList = new ArrayList<>();
        valueList.add("valueThree");
        valueList.add("valueFour");
        treeEdgeFifthFilters.add(new NotInFilter("fieldThree", Collections.singletonList(valueList)));
        final TreeEdge treeEdgeFifth = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("treeEdgeFifthId", 1, 1))
                .filters(treeEdgeFifthFilters)
                .treeKnot(treeKnotFifth)
                .build();

        final ValuedKnotData treeKnotSecondValue = ValuedKnotData.builder()
                .value(new StringValue("Value2"))
                .build();
        final List<TreeEdge> treeEdgeListOfSecondKnot = new ArrayList<>();
        treeEdgeListOfSecondKnot.add(treeEdgeThird);
        final TreeKnot treeKnotSecond = TreeKnot.builder()
                .id("treeKnotSecondId")
                .treeEdges(treeEdgeListOfSecondKnot)
                .knotData(treeKnotSecondValue)
                .build();
        final List<Filter> treeEdgeSecondFilters = new ArrayList<>();
        treeEdgeSecondFilters.add(new EqualsFilter("fieldOne", "valueOne"));
        final TreeEdge treeEdgeSecond = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("treeEdgeSecondId", 1, 1))
                .filters(treeEdgeSecondFilters)
                .treeKnot(treeKnotSecond)
                .build();

        final ValuedKnotData treeKnotFourthValue = ValuedKnotData.builder()
                .value(new StringValue("Value4"))
                .build();
        final List<TreeEdge> treeEdgeListOfFourthKnot = new ArrayList<>();
        treeEdgeListOfFourthKnot.add(treeEdgeFifth);
        final TreeKnot treeKnotFourth = TreeKnot.builder()
                .id("treeKnotFourthId")
                .treeEdges(treeEdgeListOfFourthKnot)
                .knotData(treeKnotFourthValue)
                .build();
        final List<Filter> treeEdgeFourthFilters = new ArrayList<>();
        final List<String> values = new ArrayList<>();
        values.add("valueThree");
        values.add("valueTwo");
        treeEdgeFourthFilters.add(new InFilter("fieldOne", Collections.singletonList(values)));
        final TreeEdge treeEdgeFourth = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("treeEdgeFourthId", 1, 1))
                .filters(treeEdgeFourthFilters)
                .treeKnot(treeKnotFourth)
                .build();

        final ValuedKnotData treeKnotFirstValue = ValuedKnotData.builder()
                .value(new StringValue("Value1"))
                .build();
        final List<TreeEdge> treeEdgeListOfFirstKnot = new ArrayList<>();
        treeEdgeListOfFirstKnot.add(treeEdgeSecond);
        treeEdgeListOfFirstKnot.add(treeEdgeFourth);
        final TreeKnot treeKnotFirst = TreeKnot.builder()
                .id("treeKnotFirstId")
                .treeEdges(treeEdgeListOfFirstKnot)
                .knotData(treeKnotFirstValue)
                .build();

        componentValidator.validate(treeKnotFirst);

        Assertions.assertNotNull(treeKnotFirst);
    }

    @Test
    void Given_TwoInternalStringValuedKnotAndRootStringValuedKnot_When_ValidatingTreeKnotWithMutualExclusionOn_ThenThrowMaximumVariationBonsaiError() {
        final ValuedKnotData stringKnotDataOne = ValuedKnotData.builder()
                .value(new StringValue("string value One"))
                .build();
        final TreeKnot internalTreeKnotOne = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(stringKnotDataOne)
                .build();
        final List<Filter> filtersOne = new ArrayList<>();
        filtersOne.add(new EqualsFilter("fieldOne", "valueOne"));
        final TreeEdge internalTreeEdgeOne = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeIdOne", 1, 1))
                .filters(filtersOne)
                .treeKnot(internalTreeKnotOne)
                .build();

        final ValuedKnotData stringKnotDataTwo = ValuedKnotData.builder()
                .value(new StringValue("string value Two"))
                .build();
        final TreeKnot internalTreeKnotTwo = TreeKnot.builder()
                .id("mapKnotId")
                .knotData(stringKnotDataTwo)
                .build();
        final List<Filter> filtersTwo = new ArrayList<>();
        filtersTwo.add(new EqualsFilter("fieldOne", "valueTwo"));
        final TreeEdge internalTreeEdgeTwo = TreeEdge.builder()
                .edgeIdentifier(new EdgeIdentifier("edgeIdTwo", 2, 2))
                .filters(filtersTwo)
                .treeKnot(internalTreeKnotTwo)
                .build();

        final ValuedKnotData valuedKnotData = ValuedKnotData.builder()
                .value(new StringValue("string value"))
                .build();
        final List<TreeEdge> treeEdges = new ArrayList<>();
        treeEdges.add(internalTreeEdgeOne);
        treeEdges.add(internalTreeEdgeTwo);
        final TreeKnot rootTreeKnot = TreeKnot.builder()
                .id("valuedKnotId")
                .treeEdges(treeEdges)
                .knotData(valuedKnotData)
                .build();

        try {
            final ComponentBonsaiTreeValidator componentValidator =
                    getComponentBonsaiTreeValidator(1, 3, true);
            componentValidator.validate(rootTreeKnot);
        } catch (BonsaiError e) {
            Assertions.assertEquals(BonsaiErrorCode.MAX_VARIATIONS_EXCEEDED, e.getErrorCode());
        }
    }

    private ComponentBonsaiTreeValidator getComponentBonsaiTreeValidator(final long maxAllowedVariationsPerKnot,
                                                                         final long maxAllowedConditionsPerEdge,
                                                                         final boolean isMutualExclusivitySettingTurnedOn) {
        final BonsaiProperties bonsaiProperties = BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(isMutualExclusivitySettingTurnedOn)
                .maxAllowedConditionsPerEdge(maxAllowedConditionsPerEdge)
                .maxAllowedVariationsPerKnot(maxAllowedVariationsPerKnot)
                .build();

        return new ComponentBonsaiTreeValidator(bonsaiProperties);
    }
}