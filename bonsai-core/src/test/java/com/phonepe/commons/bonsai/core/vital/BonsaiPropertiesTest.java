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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BonsaiPropertiesTest {

    private BonsaiProperties bonsaiProperties;

    @BeforeEach
    public void setUp() {
        bonsaiProperties = BonsaiProperties.builder().build();
    }

    @AfterEach
    public void tearDown() {
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