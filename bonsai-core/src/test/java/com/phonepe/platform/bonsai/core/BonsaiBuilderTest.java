package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.vital.BonsaiBuilder;
import com.phonepe.platform.bonsai.core.vital.BonsaiProperties;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  19/09/18 - 2:52 PM
 */
public class BonsaiBuilderTest {

    @Test(expected = NullPointerException.class)
    public void testBuildFailure() {
        Bonsai build = BonsaiBuilder.builder().build();
    }

    @Test
    public void testBuildSuccess() {
        Bonsai build = BonsaiBuilder.builder()
                                    .withBonsaiProperties(BonsaiProperties.builder().build()).build();
        Assert.assertNotNull(build);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildFailureMaxCondition() {
        Bonsai build = BonsaiBuilder.builder()
                                    .withBonsaiProperties(BonsaiProperties.builder()
                                                                          .maxAllowedConditionsPerEdge(0)
                                                                          .build())
                                    .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildFailureMaxVariation() {
        Bonsai build = BonsaiBuilder.builder()
                                    .withBonsaiProperties(BonsaiProperties.builder()
                                                                          .maxAllowedVariationsPerKnot(0)
                                                                          .build())
                                    .build();
    }
}