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

package com.phonepe.platform.bonsai.core.vital.provided.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryKeyTreeStoreTest {

    private final static String KEY_ONE = "keyOne";
    private final static String KEY_TWO = "keyTwo";
    private final static String KNOT_ID_ONE = "knotIdOne";
    private final static String KNOT_ID_TWO = "knotIdTwo";

    private InMemoryKeyTreeStore inMemoryKeyTreeStore;

    @BeforeEach
    public void setUp() {
        inMemoryKeyTreeStore = new InMemoryKeyTreeStore();
    }

    @AfterEach
    public void tearDown() {
        inMemoryKeyTreeStore = null;
    }

    @Test
    void given_inMemoryKeyTreeStore_when_checkingMappingBetweenKeyToKnotId_then_returnKnotId() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(KEY_TWO, KNOT_ID_TWO);
        final boolean isKnotTwoIdPresent = inMemoryKeyTreeStore.containsKey(KEY_TWO);
        final boolean isKnotOneIdPresent = inMemoryKeyTreeStore.containsKey(KEY_ONE);
        final boolean isKnotThreePresent = inMemoryKeyTreeStore.containsKey(null);

        assertNull(returnedKnotId, "Previous copy of Returned KnotId should be null.");
        assertFalse(isKnotOneIdPresent, "keyTwo:knotIdTwo should not be present in the map.");
        assertTrue(isKnotTwoIdPresent, "keyOne:knotIdOne should be present in the map.");
        assertFalse(isKnotThreePresent, "Nothing should not be present in the map for null key.");
    }

    @Test
    void given_inMemoryKeyTreeStore_when_mappingKnotIdWithValidKey_then_returnKnotId() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(KEY_ONE, KNOT_ID_ONE);
        final String getKnotId = inMemoryKeyTreeStore.getKeyTree(KEY_ONE);

        assertNull(returnedKnotId, "Previous copy of Returned KnotId should be null.");
        assertEquals(KNOT_ID_ONE, getKnotId, "Fetched KnotId should be : knotIdOne");
    }

    @Test
    void given_inMemoryKeyTreeStore_when_mappingKnotWithNullKey_then_returnNullObject() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(null, KNOT_ID_ONE);
        final String getKnotId = inMemoryKeyTreeStore.getKeyTree(null);

        assertNull(returnedKnotId, "Previous copy of Returned KnotId should be null.");
        assertNull(getKnotId, "Fetched copy of Returned KnotId should be null.");
    }

    @Test
    void given_inMemoryKeyTreeStore_when_deletingKeyToKnotIdMapping_then_deleteMapping() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(KEY_ONE, KNOT_ID_ONE);
        final String deletedKnotId = inMemoryKeyTreeStore.removeKeyTree(KEY_ONE);
        final String getKnotId = inMemoryKeyTreeStore.getKeyTree(KEY_ONE);

        assertNull(returnedKnotId, "Previous copy of Returned KnotId should be null.");
        assertEquals(KNOT_ID_ONE, deletedKnotId, "Previous copy of deleted KnotId should be : knotIdOne");
        assertNull(getKnotId, "Fetched copy of deleted KnotId should be null.");
    }

    @Test
    void given_inMemoryKeyTreeStore_when_deletingNonExistingKeyToKnotIdMapping_then_returnNull() {
        final String deletedKnotId = inMemoryKeyTreeStore.removeKeyTree(null);
        assertNull(deletedKnotId, "Copy of deletedKnotId KnotId should be null.");
    }
}