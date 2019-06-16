package com.phonepe.platform.bonsai.models.model;

import lombok.*;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-11 - 01:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class FlatNodeDetail {
    private List<Integer> path;
    private FlatNode flatNode;
}
