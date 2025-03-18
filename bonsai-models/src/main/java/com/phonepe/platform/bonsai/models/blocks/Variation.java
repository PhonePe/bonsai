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

package com.phonepe.platform.bonsai.models.blocks;

import com.phonepe.commons.query.dsl.Filter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This depicts a variation of a knot, given certain filter criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variation {

    private int priority;

    @Singular
    private List<Filter> filters;

    private String knotId;

    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();

    /* these is a nullable field, hence we aren't using primitive type */
    @Builder.Default
    private boolean live = true;

    /* these is a nullable field, hence we aren't using primitive type */
    @Builder.Default
    private float percentage = 100.0f;
}
