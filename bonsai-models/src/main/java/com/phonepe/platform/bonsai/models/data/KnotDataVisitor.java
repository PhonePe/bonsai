package com.phonepe.platform.bonsai.models.data;

public interface KnotDataVisitor<T> {
    T visit(ValuedKnotData valuedKnotData);

    T visit(MultiKnotData multiKnotData);

    T visit(MapKnotData mapKnotData);
}
