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

package com.phonepe.commons.bonsai.models.blocks;

import com.phonepe.commons.bonsai.models.BonsaiConstants;
import com.phonepe.commons.bonsai.models.data.KnotData;
import com.phonepe.commons.bonsai.models.structures.OrderedList;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

@Data
@ToString
@NoArgsConstructor
public class Knot {
    private String id;
    private long version;
    private KnotData knotData;
    private OrderedList<EdgeIdentifier> edges;
    private Map<String, Object> properties;

    @Builder
    public Knot(final String id,
                final long version,
                final OrderedList<EdgeIdentifier> edges,
                final KnotData knotData,
                final Map<String, Object> properties) {
        this.id = id;
        this.version = version;
        this.edges = edges;
        this.knotData = knotData;
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Knot knot = (Knot) o;
        return Objects.equals(id, knot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Knot updateVersion() {
        this.version = System.currentTimeMillis();
        return this;
    }

    public Knot deepClone(long version) {
        return Knot.builder()
                .id(this.id)
                .version(version)
                .edges(this.edges)
                .knotData(this.knotData)
                .properties(this.properties)
                .build();
    }

    /**
     * Get mutual exclusivity setting from properties map.
     * @return Boolean value if set, null if not specified
     */
    @javax.annotation.Nullable
    public Boolean getMutualExclusivityEnabled() {
        if (properties == null) {
            return null;
        }
        Object value = properties.get(BonsaiConstants.MUTUAL_EXCLUSIVITY_PROPERTY);
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return null;
    }

    /**
     * Set mutual exclusivity setting in properties map.
     * @param enabled true to enable mutual exclusivity, false to disable, null to remove the setting
     */
    public void setMutualExclusivityEnabled(Boolean enabled) {
        if (properties == null) {
            if (enabled != null) {
                this.properties = new java.util.HashMap<>();
                properties.put(BonsaiConstants.MUTUAL_EXCLUSIVITY_PROPERTY, enabled);
            }
        } else {
            if (enabled != null) {
                properties.put(BonsaiConstants.MUTUAL_EXCLUSIVITY_PROPERTY, enabled);
            } else {
                properties.remove(BonsaiConstants.MUTUAL_EXCLUSIVITY_PROPERTY);
            }
        }
    }

}
