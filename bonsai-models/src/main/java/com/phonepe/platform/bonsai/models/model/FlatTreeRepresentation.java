package com.phonepe.platform.bonsai.models.model;

import lombok.*;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-11 - 01:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FlatTreeRepresentation {
    private String root;
    private Map<String, FlatNodeDetail> flatNodeMapping;
}
