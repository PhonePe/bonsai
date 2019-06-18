package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:05 PM
 */
public interface BonsaiTreeValidator {

    void validate(Knot knot);

    void validate(Knot knot, Knot knot2);

    void validate(Edge edge);

    void validate(Context context);

    void validate(Variation variation);
}
