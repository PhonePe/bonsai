package com.phonepe.platform.bonsai.core.data;

/**
 * @author tushar.naik
 * @version 1.0  12/07/18 - 3:44 PM
 */
public interface KnotDataVisitor<T> {
    T visit(ValuedKnotData valuedKnotData);

    T visit(MultiKnotData multiKnotData);

    T visit(MapKnotData mapKnotData);
}
