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

package com.phonepe.platform.bonsai.models.blocks.model;

import com.phonepe.platform.bonsai.models.data.KnotData;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This is a recursive representation of the tree, without references
 */
@Data
@NoArgsConstructor
public class TreeKnot {
    private String id;
    private long version;
    private List<TreeEdge> treeEdges;
    private KnotData knotData;
    private Map<String, Object> properties;

    @Builder
    public TreeKnot(final String id,
                    final long version,
                    final List<TreeEdge> treeEdges,
                    final KnotData knotData,
                    final Map<String, Object> properties) {
        this.id = id;
        this.version = version;
        this.treeEdges = treeEdges;
        this.knotData = knotData;
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeKnot treeKnot = (TreeKnot) o;
        return Objects.equals(id, treeKnot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
