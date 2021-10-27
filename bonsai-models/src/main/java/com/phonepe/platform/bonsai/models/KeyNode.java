package com.phonepe.platform.bonsai.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    public KeyNode(String key, Node node) {
        this.key = key;
        this.node = node;
    }

    public static KeyNode empty(String key) {
        return new KeyNode(key, null, null);
    }

    public static KeyNode empty(String key, List<Integer> path) {
        return new KeyNode(key, null, path);
    }
}
