package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableList;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.query.dsl.logical.AndFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.platform.query.dsl.numeric.LessEqualFilter;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  19/09/18 - 3:20 PM
 */
public class ComponentValidatorTest {

    private ComponentValidator componentValidator
            = new ComponentValidator(BonsaiProperties.builder()
                                                     .mutualExclusivitySettingTurnedOn(true)
                                                     .singleConditionEdgeSettingTurnedOn(false)
                                                     .build());

    @Test(expected = BonsaiError.class)
    public void validateEdge() {
        componentValidator.validate(Edge.builder().build());
    }

    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorOnNegativeVersion() {
        componentValidator.validate(Edge.builder().edgeIdentifier(new EdgeIdentifier("id1", 1))
                                        .version(-1).build());
    }

    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorOnNegativePriority() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", -1))
                                        .version(1).build());
    }

    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorWhenFiltersContainMultipleFields() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new GreaterEqualFilter("field1", 123))
                                        .filter(new LessEqualFilter("field2", 123))
                                        .build());
    }

    @Test()
    public void validateEdgeNoErrorWhenFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new GreaterEqualFilter("field1", 123))
                                        .filter(new LessEqualFilter("field1", 300))
                                        .build());
    }


    @Test(expected = BonsaiError.class)
    public void validateEdgeErrorOnSingleConditionEdgeSettingOnAndMultipleFiltersSet() {

        new ComponentValidator(BonsaiProperties.builder()
                                               .mutualExclusivitySettingTurnedOn(true)
                                               .singleConditionEdgeSettingTurnedOn(true)
                                               .build())
                .validate(Edge.builder()
                              .edgeIdentifier(new EdgeIdentifier("id1", 1))
                              .version(1)
                              .knotId("knotId1")
                              .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                     new GreaterEqualFilter("field1", 121))))
                              .build());
    }

    @Test()
    public void validateEdgeNoErrorOnInnerFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new OrFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                              new GreaterEqualFilter("field1", 121))))
                                        .build());
    }

    @Test()
    public void validateEdgeErrorOnInnerFiltersContainSameField() {
        componentValidator.validate(Edge.builder()
                                        .edgeIdentifier(new EdgeIdentifier("id1", 1))
                                        .version(1)
                                        .knotId("knotId1")
                                        .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                               new GreaterEqualFilter("field1", 121))))
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
        new ComponentValidator(BonsaiProperties.builder()
                                               .mutualExclusivitySettingTurnedOn(true)
                                               .singleConditionEdgeSettingTurnedOn(true)
                                               .build())
                .validate(Variation.builder()
                                   .filter(new AndFilter(ImmutableList.of(new GreaterEqualFilter("field1", 123),
                                                                          new GreaterEqualFilter("field1", 123))))
                                   .knotId("knotId1")
                                   .priority(1)
                                   .build());
    }
}