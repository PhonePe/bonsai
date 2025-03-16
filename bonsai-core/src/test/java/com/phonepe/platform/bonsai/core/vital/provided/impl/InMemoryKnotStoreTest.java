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

import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryKnotStoreTest {

    private InMemoryKnotStore inMemoryKnotStore;

    @BeforeEach
    public void setUp() throws Exception {
        inMemoryKnotStore = new InMemoryKnotStore();
    }

    @AfterEach
    public void tearDown() throws Exception {
        inMemoryKnotStore = null;
    }

    @Test
    void given_inMemoryKnotStore_when_checkingMappingBetweenKnotIdToKnot_then_returnKnot() {
        final Knot createKnotOne = getKnot("K1");
        final Knot createKnotTwo = getKnot("K2");
        final Knot returnedKnotOne = inMemoryKnotStore.mapKnot(createKnotOne.getId(), createKnotOne);
        final boolean isKnotOnePresent = inMemoryKnotStore.containsKnot(createKnotOne.getId());
        final boolean isKnotTwoPresent = inMemoryKnotStore.containsKnot(createKnotTwo.getId());
        final boolean isKnotThreePresent = inMemoryKnotStore.containsKnot(null);


        assertNull(returnedKnotOne);
        assertTrue(isKnotOnePresent);
        assertFalse(isKnotTwoPresent);
        assertFalse(isKnotThreePresent);
    }

    @Test
    void given_inMemoryKnotStore_when_mappingKnotWithValidKnotId_then_returnKnot() {
        final Knot createKnot = getKnot("K1");
        final Knot returnedKnot = inMemoryKnotStore.mapKnot(createKnot.getId(), createKnot);
        final Knot fetchedKnot = inMemoryKnotStore.getKnot("K1");

        assertNull(returnedKnot);
        assertNotNull(fetchedKnot);
        assertEquals("K1", fetchedKnot.getId());
    }

    @Test
    void given_inMemoryKnotStore_when_mappingKnotWithNullKnotId_then_returnNullObject() {
        final Knot createKnot = getKnot("K1");
        final Knot returnedKnot = inMemoryKnotStore.mapKnot(null, createKnot);
        final Knot fetchedKnot = inMemoryKnotStore.getKnot(null);

        assertNull(returnedKnot);
        assertNull(fetchedKnot);
    }

    @Test
    void given_inMemoryKnotStore_when_deletingKnotIdToKnotMapping_then_deleteMapping() {
        final Knot createKnot = getKnot("K1");
        final Knot returnedKnot = inMemoryKnotStore.mapKnot(createKnot.getId(), createKnot);
        final Knot deletedKnot = inMemoryKnotStore.deleteKnot(createKnot.getId());
        final Knot fetchedKnot = inMemoryKnotStore.getKnot(createKnot.getId());

        assertNull(returnedKnot);
        assertNotNull(deletedKnot);
        assertNull(fetchedKnot);
    }

    @Test
    void given_inMemoryKnotStore_when_deletingNonExistingKnotIdToKnotMapping_then_returnNull() {
        final Knot deletedKnot = inMemoryKnotStore.deleteKnot(null);
        assertNull(deletedKnot);
    }

    private Knot getKnot(final String knotId) {
        return Knot.builder()
                .id(knotId)
                .version(123)
                .knotData(ValuedKnotData.stringValue("KnotData"))
                .edges(null)
                .build();
    }
}