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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BonsaiProperties {
    /* only 1 field can be used in filters for edges along a Knot, at a given level */
    private boolean mutualExclusivitySettingTurnedOn;

    /* max number of variations allowed on knot */
    @Builder.Default
    private long maxAllowedVariationsPerKnot = 1;

    /* max number of filters that may be set on edges */
    @Builder.Default
    private long maxAllowedConditionsPerEdge = 1;
}
