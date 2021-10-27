package com.phonepe.platform.bonsai.models.model;

public interface FlatNodeVisitor<T> {
    T visit(ValueFlatNode valueFlatNode);

    T visit(ListFlatNode listFlatNode);

    T visit(MapFlatNode mapFlatNode);
}
