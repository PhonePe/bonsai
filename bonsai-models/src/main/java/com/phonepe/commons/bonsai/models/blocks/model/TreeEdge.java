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

package com.phonepe.commons.bonsai.models.blocks.model;

import com.phonepe.commons.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.commons.query.dsl.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link TreeKnot}
 * It is part of a {@link TreeKnot}
 */
@Data
@NoArgsConstructor
public class TreeEdge {
    private EdgeIdentifier edgeIdentifier;
    private TreeKnot treeKnot;
    private long version;
    private List<Filter> filters;
    private Map<String, Object> properties;
    private boolean live;
    private float percentage;

    @Builder
    public TreeEdge(final EdgeIdentifier edgeIdentifier,
                    final TreeKnot treeKnot,
                    final long version,
                    final List<Filter> filters,
                    final Map<String, Object> properties,
                    final boolean live,
                    final float percentage) {
        this.edgeIdentifier = edgeIdentifier;
        this.treeKnot = treeKnot;
        this.version = version;
        this.filters = filters;
        this.properties = properties;
        this.live = live;
        this.percentage = percentage;
    }
}
