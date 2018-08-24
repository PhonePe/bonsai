package com.phonepe.platform.bonsai.core.vital;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:05 PM
 */
public interface Validator {
    void validate(KnotData knotData);

    void validate(Knot knot);

    void validate(Edge edge);

    void validate(Context context);
}
