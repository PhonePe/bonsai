package com.phonepe.platform.bonsai.models.model;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-11 - 00:55
 */
public interface FlatNodeVisitor<T> {
    T visit(ValueFlatNode valueFlatNode);

    T visit(ListFlatNode listFlatNode);

    T visit(MapFlatNode mapFlatNode);
}
