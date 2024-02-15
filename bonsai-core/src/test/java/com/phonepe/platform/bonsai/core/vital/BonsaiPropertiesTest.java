package com.phonepe.platform.bonsai.core.vital;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BonsaiPropertiesTest {

    private BonsaiProperties bonsaiProperties;

    @BeforeEach
    public void setUp() throws Exception {
        bonsaiProperties = BonsaiProperties.builder().build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        bonsaiProperties = null;
    }

    @Test
    void given_bonsaiProperties_when_settingDefaultProperties_then_returnTheseProperties() {
        assertEquals(1, bonsaiProperties.getMaxAllowedConditionsPerEdge());
        assertEquals(1, bonsaiProperties.getMaxAllowedVariationsPerKnot());
        assertFalse(bonsaiProperties.isMutualExclusivitySettingTurnedOn());
    }

    @Test
    void given_bonsaiProperties_when_settingTheseProperties_then_returnTheseProperties() {
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