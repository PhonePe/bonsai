package com.phonepe.platform.bonsai.models.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUE", value = ValueFlatNode.class),
        @JsonSubTypes.Type(name = "LIST", value = ListFlatNode.class),
        @JsonSubTypes.Type(name = "MAP", value = MapFlatNode.class)
})
public abstract class FlatNode {
    private FlatNodeType type;

    public FlatNode(FlatNodeType type) {
        this.type = type;
    }

    public abstract <T> T accept(FlatNodeVisitor<T> visitor);

    public enum FlatNodeType {
        VALUE,
        LIST,
        MAP
    }
}
