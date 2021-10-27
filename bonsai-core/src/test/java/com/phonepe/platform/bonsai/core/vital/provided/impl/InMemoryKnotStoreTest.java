package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class InMemoryKnotStoreTest {

    private InMemoryKnotStore inMemoryKnotStore;

    @Before
    public void setUp() throws Exception {
        inMemoryKnotStore = new InMemoryKnotStore();
    }

    @After
    public void tearDown() throws Exception {
        inMemoryKnotStore = null;
    }

    @Test
    public void given_inMemoryKnotStore_when_checkingMappingBetweenKnotIdToKnot_then_returnKnot() {
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
    public void given_inMemoryKnotStore_when_mappingKnotWithValidKnotId_then_returnKnot() {
        final Knot createKnot = getKnot("K1");
        final Knot returnedKnot = inMemoryKnotStore.mapKnot(createKnot.getId(), createKnot);
        final Knot fetchedKnot = inMemoryKnotStore.getKnot("K1");

        assertNull(returnedKnot);
        assertNotNull(fetchedKnot);
        assertEquals("K1", fetchedKnot.getId());
    }

    @Test
    public void given_inMemoryKnotStore_when_mappingKnotWithNullKnotId_then_returnNullObject() {
        final Knot createKnot = getKnot("K1");
        final Knot returnedKnot = inMemoryKnotStore.mapKnot(null, createKnot);
        final Knot fetchedKnot = inMemoryKnotStore.getKnot(null);

        assertNull(returnedKnot);
        assertNull(fetchedKnot);
    }

    @Test
    public void given_inMemoryKnotStore_when_deletingKnotIdToKnotMapping_then_deleteMapping() {
        final Knot createKnot = getKnot("K1");
        final Knot returnedKnot = inMemoryKnotStore.mapKnot(createKnot.getId(), createKnot);
        final Knot deletedKnot = inMemoryKnotStore.deleteKnot(createKnot.getId());
        final Knot fetchedKnot = inMemoryKnotStore.getKnot(createKnot.getId());

        assertNull(returnedKnot);
        assertNotNull(deletedKnot);
        assertNull(fetchedKnot);
    }

    @Test
    public void given_inMemoryKnotStore_when_deletingNonExistingKnotIdToKnotMapping_then_returnNull() {
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