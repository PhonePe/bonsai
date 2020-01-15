package com.phonepe.platform.bonsai.core.vital.provided.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author - suraj.s
 * @date - 2019-11-19
 */
public class InMemoryKeyTreeStoreTest {

    private final static String KEY_ONE = "keyOne";
    private final static String KEY_TWO = "keyTwo";
    private final static String KNOT_ID_ONE = "knotIdOne";
    private final static String KNOT_ID_TWO = "knotIdTwo";

    private InMemoryKeyTreeStore inMemoryKeyTreeStore;

    @Before
    public void setUp() throws Exception {
        inMemoryKeyTreeStore = new InMemoryKeyTreeStore();
    }

    @After
    public void tearDown() throws Exception {
        inMemoryKeyTreeStore = null;
    }

    @Test
    public void given_inMemoryKeyTreeStore_when_checkingMappingBetweenKeyToKnotId_then_returnKnotId() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(KEY_TWO, KNOT_ID_TWO);
        final boolean isKnotTwoIdPresent = inMemoryKeyTreeStore.containsKey(KEY_TWO);
        final boolean isKnotOneIdPresent = inMemoryKeyTreeStore.containsKey(KEY_ONE);

        assertNull("Previous copy of Returned KnotId should be null.", returnedKnotId);
        assertFalse("keyTwo:knotIdTwo should not be present in the map.", isKnotOneIdPresent);
        assertTrue("keyOne:knotIdOne should be present in the map.", isKnotTwoIdPresent);

    }

    @Test
    public void given_inMemoryKeyTreeStore_when_mappingKnotIdWithValidKey_then_returnKnotId() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(KEY_ONE, KNOT_ID_ONE);
        final String getKnotId = inMemoryKeyTreeStore.getKeyTree(KEY_ONE);

        assertNull("Previous copy of Returned KnotId should be null.", returnedKnotId);
        assertEquals("Fetched KnotId should be : knotIdOne", KNOT_ID_ONE, getKnotId);
    }

    @Test
    public void given_inMemoryKeyTreeStore_when_mappingKnotWithNullKey_then_returnNullObject() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(null, KNOT_ID_ONE);
        final String getKnotId = inMemoryKeyTreeStore.getKeyTree(null);

        assertNull("Previous copy of Returned KnotId should be null.", returnedKnotId);
        assertNull("Fetched copy of Returned KnotId should be null.", getKnotId);
    }

    @Test
    public void given_inMemoryKeyTreeStore_when_deletingKeyToKnotIdMapping_then_deleteMapping() {
        final String returnedKnotId = inMemoryKeyTreeStore.createKeyTree(KEY_ONE, KNOT_ID_ONE);
        final String deletedKnotId = inMemoryKeyTreeStore.removeKeyTree(KEY_ONE);
        final String getKnotId = inMemoryKeyTreeStore.getKeyTree(KEY_ONE);

        assertNull("Previous copy of Returned KnotId should be null.", returnedKnotId);
        assertEquals("Previous copy of deleted KnotId should be : knotIdOne", KNOT_ID_ONE, deletedKnotId);
        assertNull("Fetched copy of deleted KnotId should be null.", getKnotId);
    }

    @Test
    public void given_inMemoryKeyTreeStore_when_deletingNonExistingKeyToKnotIdMapping_then_returnNull() {
        final String deletedKnotId = inMemoryKeyTreeStore.removeKeyTree(null);
        assertNull("Copy of deletedKnotId KnotId should be null.", deletedKnotId);
    }
}