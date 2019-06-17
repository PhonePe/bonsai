package com.phonepe.platform.bonsai.json.eval;

import com.phonepe.platform.query.dsl.general.InFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-17 - 17:38
 */
public class JsonPathFilterBuilderTest {
    @Test
    public void testBuilder() {
        Assert.assertNotNull(new JsonPathFilterBuilder().visit(new InFilter()));
    }
}