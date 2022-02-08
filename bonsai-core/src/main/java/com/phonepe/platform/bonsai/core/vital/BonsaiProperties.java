package com.phonepe.platform.bonsai.core.vital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BonsaiProperties {
    /* only 1 field can be used in filters for edges along a Knot, at a given level */
    private boolean mutualExclusivitySettingTurnedOn;

    /* max number of variations allowed on knot */
    @Builder.Default
    private long maxAllowedVariationsPerKnot = 1;

    /* max number of filters that may be set on edges */
    @Builder.Default
    private long maxAllowedConditionsPerEdge = 1;
}
