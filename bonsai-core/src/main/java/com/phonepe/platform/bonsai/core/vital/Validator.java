package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.data.KnotData;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:05 PM
 */
public interface Validator {
    void validate(KnotData knotData);

    void validate(Edge edge);

    void validate(Context context);

    void validate(Variation variation);
}
