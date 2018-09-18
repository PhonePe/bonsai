package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:05 PM
 */
public interface Validator {
    void validate(KnotData knotData);

    void validate(AtomicEdge edge);

    void validate(Context context);

    void validate(Variation variation);
}
