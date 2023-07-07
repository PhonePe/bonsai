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
