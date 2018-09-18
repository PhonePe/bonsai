package com.phonepe.platform.bonsai.core.vital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tushar.naik
 * @version 1.0  28/08/18 - 4:31 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BonsaiProperties {
    /* edge can have only 1 condition at a given point in time */
    private boolean singleConditionEdgeSettingTurnedOn;

    /* only 1 field can be used in filters for edges along a Knot, at a given level */
    private boolean mutualExclusivitySettingTurnedOn;
}
