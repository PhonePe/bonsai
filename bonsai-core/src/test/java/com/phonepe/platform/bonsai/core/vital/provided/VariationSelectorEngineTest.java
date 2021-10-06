package com.phonepe.platform.bonsai.core.vital.provided;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import org.junit.Test;

import java.util.Collections;

public class VariationSelectorEngineTest {
    @Test
    public void testWhenNoContextThenVariationSelectionReturnsFalse() {
        new VariationSelectorEngine<>().match(new Context(null, Maps.newHashMap()),
                                              Collections.singletonList(Edge.builder().edgeIdentifier(new EdgeIdentifier()).build()));

    }
}