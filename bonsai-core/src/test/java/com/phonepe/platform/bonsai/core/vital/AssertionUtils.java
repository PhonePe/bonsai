package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Assertions;

@UtilityClass
public class AssertionUtils {

    public void assertSame(TreeKnot expected, TreeKnot actual, boolean fullyIdentical) {
        Assertions.assertEquals(expected.getKnotData(), actual.getKnotData());
        if (fullyIdentical) {
            Assertions.assertEquals(expected.getId(), actual.getId());
            Assertions.assertEquals(expected.getVersion(), actual.getVersion());
        }
        assertEqualIfNotNull(expected.getProperties(), actual.getProperties());
        if (expected.getTreeEdges() != null && actual.getTreeEdges() != null) {
            Assertions.assertEquals(expected.getTreeEdges().size(), actual.getTreeEdges().size());
            for (int i = 0; i < expected.getTreeEdges().size(); i++) {
                var expectedEdge = expected.getTreeEdges().get(i);
                var actualEdge = actual.getTreeEdges().get(i);
                if (fullyIdentical) {
                    Assertions.assertEquals(expectedEdge.getEdgeIdentifier(), actualEdge.getEdgeIdentifier());
                    Assertions.assertEquals(expectedEdge.getVersion(), actualEdge.getVersion());
                }
                assertEqualIfNotNull(expectedEdge.getProperties(), actualEdge.getProperties());
                assertEqualIfNotNull(expectedEdge.getPercentage(), actualEdge.getPercentage());
                Assertions.assertEquals(expectedEdge.getFilters(), actualEdge.getFilters());

                assertSame(expectedEdge.getTreeKnot(), actualEdge.getTreeKnot(), fullyIdentical);
            }
        } else {
            Assertions.assertEquals(expected.getTreeEdges(), actual.getTreeEdges());
        }
    }

    public <T> void assertEqualIfNotNull(T expected, T actual) {
        if (expected != null) {
            Assertions.assertEquals(expected, actual);
        } else {
            Assertions.assertNull(actual);
        }
    }
}
