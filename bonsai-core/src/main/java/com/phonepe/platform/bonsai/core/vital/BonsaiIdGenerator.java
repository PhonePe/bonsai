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

import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;

import java.util.List;

public interface BonsaiIdGenerator {
    String newEdgeId();

    default int newEdgeNumber(List<EdgeIdentifier> edges) {
        if (edges == null || edges.isEmpty()) {
            return 1;
        }
        return edges.stream().map(EdgeIdentifier::getNumber).max(Integer::compareTo).orElse(1) + 1;
    }

    String newKnotId();
}
