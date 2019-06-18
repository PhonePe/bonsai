package com.phonepe.platform.bonsai.json.eval;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-17 - 17:39
 */
public class UtilsTest {
    @Test
    public void testLeftPad() {
        Assert.assertEquals("iliketoiliketomoveit", Utils.leftPad("moveit", 20, "iliketo"));
        Assert.assertEquals("iiiimoveit", Utils.leftPad("moveit", 10, 'i'));
    }
}