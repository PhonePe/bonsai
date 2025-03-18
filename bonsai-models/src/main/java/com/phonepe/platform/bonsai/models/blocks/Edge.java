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

import com.phonepe.platform.bonsai.conditions.Condition;
import com.phonepe.commons.query.dsl.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link Knot} id
 * It is part of a {@link Knot}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Edge extends Condition {
    private String knotId;
    private EdgeIdentifier edgeIdentifier;
    private long version;
    private List<Filter> filters;

    @Builder
    public Edge(final Boolean live,
                final Float percentage,
                final EdgeIdentifier edgeIdentifier,
                final String knotId,
                final long version,
                final @Singular List<Filter> filters,
                final Map<String, Object> properties) {
        super(
                live == null || live,
                percentage == null ? 100f : percentage,
                properties == null ? new HashMap<>() : properties
        );
        this.edgeIdentifier = edgeIdentifier;
        this.knotId = knotId;
        this.version = version;
        this.filters = filters;
    }

    public Edge updateVersion() {
        this.version = System.currentTimeMillis();
        return this;
    }

    public Edge deepClone(long version) {
        return Edge.builder()
                .knotId(this.knotId)
                .edgeIdentifier(this.edgeIdentifier)
                .version(version)
                .filters(this.filters)
                .properties(this.getProperties())
                .build();
    }
}
