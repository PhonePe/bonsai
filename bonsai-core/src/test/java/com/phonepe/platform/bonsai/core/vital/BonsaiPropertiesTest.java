package com.phonepe.platform.bonsai.core.vital;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author - suraj.s
 * @date - 2019-11-21
 */
public class BonsaiPropertiesTest {

    private BonsaiProperties bonsaiProperties;

    @Before
    public void setUp() throws Exception {
        bonsaiProperties = BonsaiProperties.builder().build();
    }

    @After
    public void tearDown() throws Exception {
        bonsaiProperties = null;
    }

    @Test
    public void given_bonsaiProperties_when_settingDefaultProperties_then_returnTheseProperties() {
        assertEquals(1, bonsaiProperties.getMaxAllowedConditionsPerEdge());
        assertEquals(1, bonsaiProperties.getMaxAllowedVariationsPerKnot());
        assertFalse(bonsaiProperties.isMutualExclusivitySettingTurnedOn());
    }

    @Test
    public void given_bonsaiProperties_when_settingTheseProperties_then_returnTheseProperties() {
        final BonsaiProperties bonsaiProperties = BonsaiProperties.builder()
                .mutualExclusivitySettingTurnedOn(true)
                .maxAllowedVariationsPerKnot(5)
                .maxAllowedConditionsPerEdge(10)
                .build();

        assertEquals(10, bonsaiProperties.getMaxAllowedConditionsPerEdge());
        assertEquals(5, bonsaiProperties.getMaxAllowedVariationsPerKnot());
        assertTrue(bonsaiProperties.isMutualExclusivitySettingTurnedOn());
    }
}