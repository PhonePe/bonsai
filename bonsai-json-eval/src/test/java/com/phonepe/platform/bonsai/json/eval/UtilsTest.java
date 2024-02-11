package com.phonepe.platform.bonsai.json.eval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UtilsTest {
    @Test
    void testLeftPad() {
        Assertions.assertEquals("iliketoiliketomoveit", Utils.leftPad("moveit", 20, "iliketo"));
        Assertions.assertEquals("iiiimoveit", Utils.leftPad("moveit", 10, 'i'));
    }
}