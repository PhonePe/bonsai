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

package com.phonepe.platform.bonsai.models;

import com.google.common.annotations.VisibleForTesting;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class KeyNode {
    private String key;
    private Node node;
    private List<Integer> edgePath;
    private List<Edge> edges;

    public KeyNode(String key, Node node) {
        this.key = key;
        this.node = node;
        this.edgePath = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public static KeyNode empty(String key) {
        return new KeyNode(key, null, new ArrayList<>(), new ArrayList<>());
    }

    public static KeyNode empty(String key, List<Integer> path) {
        return new KeyNode(key, null, path, new ArrayList<>());
    }

    public static KeyNode empty(String key, List<Integer> path, List<Edge> edges) {
        return new KeyNode(key, null, path, edges);
    }

    @VisibleForTesting
    public static KeyNode of(Node node) {
        return new KeyNode(null, node, null, null);
    }
}
