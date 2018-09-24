package com.phonepe.platform.bonsai.core;

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
}