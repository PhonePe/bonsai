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

package com.phonepe.platform.bonsai.core.vital;

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
