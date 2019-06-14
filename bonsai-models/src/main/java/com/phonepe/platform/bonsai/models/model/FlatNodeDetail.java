package com.phonepe.platform.bonsai.models.model;

import lombok.*;

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
    private int[] path;
    private FlatNode flatNode;
}
