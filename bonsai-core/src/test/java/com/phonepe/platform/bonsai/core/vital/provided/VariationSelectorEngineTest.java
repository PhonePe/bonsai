package com.phonepe.platform.bonsai.core.vital.provided;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

public class VariationSelectorEngineTest {
    @Test
    public void testWhenNoContextThenVariationSelectionReturnsFalse() {
        Optional<Edge> match = new VariationSelectorEngine<>()
                .match(new Context(null, Maps.newHashMap()),
                        Collections.singletonList(Edge.builder()
                                .edgeIdentifier(new EdgeIdentifier())
                                .build()));
        Assert.assertFalse(match.isPresent());

    }
}