package com.phonepe.platform.bonsai.models.blocks;

import com.phonepe.platform.query.dsl.Filter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This depicts a variation of a knot, given certain filter criteria
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

    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();

    /* these is a nullable field, hence we aren't using primitive type */
    @Builder.Default
    private boolean live = true;

    /* these is a nullable field, hence we aren't using primitive type */
    @Builder.Default
    private float percentage = 100.0f;
}
