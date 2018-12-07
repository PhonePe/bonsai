package com.phonepe.platform.bonsai.core.vital.blocks;

import com.phonepe.platform.query.dsl.Filter;
import lombok.*;

import java.util.List;

/**
 * This depicts a variation of a knot, given certain filter criteria
 *
 * @author tushar.naik
 * @version 1.0  17/09/18 - 7:00 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variation {

    private int priority;

    @Singular
    private List<Filter> filters;

    private String knotId;
}
