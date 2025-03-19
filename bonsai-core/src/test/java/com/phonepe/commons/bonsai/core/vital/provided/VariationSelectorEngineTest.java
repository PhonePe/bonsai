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

package com.phonepe.commons.bonsai.core.vital.provided;

import com.google.common.collect.Maps;
import com.phonepe.commons.bonsai.core.vital.Context;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.EdgeIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

class VariationSelectorEngineTest {
    @Test
    void testWhenNoContextThenVariationSelectionReturnsFalse() {
        Optional<Edge> match = new VariationSelectorEngine<>()
                .match(new Context(null, Maps.newHashMap()),
                        Collections.singletonList(Edge.builder()
                                .edgeIdentifier(new EdgeIdentifier())
                                .build()));
        Assertions.assertFalse(match.isPresent());

    }
}