package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableList;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.logical.AndFilter;
import com.phonepe.platform.query.dsl.logical.NotFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.platform.query.dsl.numeric.LessEqualFilter;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  19/09/18 - 3:20 PM
 */
public class ComponentBonsaiTreeValidatorTest {

    private ComponentBonsaiTreeValidator componentValidator
            = new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                                                               .mutualExclusivitySettingTurnedOn(true)
                                                               .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                                                               .build());

    @Test(expected = BonsaiError.class)
    public void validateEdge() {
        componentValidator.validate(Edge.builder().build());
    }

    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorOnNegativeVersion() {
        componentValidator.validate(Edge.builder().edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                                        .version(-1).build());
    }

    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorOnNegativePriority() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1, -1))
                                        .version(1).build());
    }

    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorWhenFiltersContainMultipleFields() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new GreaterEqualFilter("field1", 123))
                                        .filter(new LessEqualFilter("field2", 123))
                                        .build());
    }

    @Test()
    public void validateEdgeNoErrorWhenFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new GreaterEqualFilter("field1", 123))
                                        .filter(new LessEqualFilter("field1", 300))
                                        .build());
    }


    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorOnSingleConditionEdgeSettingOnAndMultipleFiltersSet() {

        new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                                                         .mutualExclusivitySettingTurnedOn(true)
                                                         .build())
                .validate(Edge.builder()
                              .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                              .version(1)
                              .knotId("knotId1")
                              .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                     new GreaterEqualFilter("field1", 121))))
                              .build());
    }

    @Test()
    public void validateEdgeNoErrorOnInnerFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new OrFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                              new GreaterEqualFilter("field1", 121))))
                                        .build());
    }

    @Test()
    public void validateEdgeErrorOnInnerFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                               new GreaterEqualFilter("field1", 121))))
                                        .build());
    }

    @Test()
    public void validateEdgeErrorOnInnerFiltersContainSameField2() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1, 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new EqualsFilter("field1", 100))
                                        .filter(new NotFilter(new GreaterEqualFilter("field1", 123)))
                                        .build());
    }

    @Test(expected = BonsaiError.class)
    public void validateVariationMutuality() {
        componentValidator.validate(Variation.builder()
                                             .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                                    new GreaterEqualFilter("field2", 121))))

                                             .knotId("knotId1")
                                             .priority(1)
                                             .build());
    }

    @Test(expected = BonsaiError.class)
    public void validateVariationSingleCondition() {
        new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                                                         .mutualExclusivitySettingTurnedOn(true)
                                                         .build())
                .validate(Variation.builder()
                                   .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                          new GreaterEqualFilter("field1", 123))))
                                   .knotId("knotId1")
                                   .priority(1)
                                   .build());
    }

    @Test(expected = BonsaiError.class)
    public void validateKnotError() {
        new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                                                         .mutualExclusivitySettingTurnedOn(true)
                                                         .build())
                .validate(Knot.builder()
                              .id("k1")
                              .version(1)
                              .build());
    }

    @Test
    public void validateKnotValid() {
        new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                                                         .mutualExclusivitySettingTurnedOn(true)
                                                         .build())
                .validate(Knot.builder()
                              .id("k1")
                              .version(1)
                              .knotData(new ValuedKnotData())
                              .build());
    }

    @Test(expected = BonsaiError.class)
    public void validateKnotInValid() {
        new ComponentBonsaiTreeValidator(BonsaiProperties.builder()
                                                         .mutualExclusivitySettingTurnedOn(true)
                                                         .build())
                .validate(Knot.builder()
                              .id("k1")
                              .version(1)
                              .knotData(new MapKnotData())
                              .build());
    }
}