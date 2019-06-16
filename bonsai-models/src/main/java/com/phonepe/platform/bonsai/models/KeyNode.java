package com.phonepe.platform.bonsai.models;

import lombok.*;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  01/08/18 - 11:11 AM
 */
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
