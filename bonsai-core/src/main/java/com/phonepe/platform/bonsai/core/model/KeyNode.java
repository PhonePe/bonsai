package com.phonepe.platform.bonsai.core.model;

import lombok.*;

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

    public static KeyNode empty(String key) {
        return new KeyNode(key, null);
    }
}
