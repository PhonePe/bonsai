package com.phonepe.platform.bonsai.models.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;

/**
 * @author tushar.naik
 * @version 1.0  12/07/18 - 3:41 PM
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "knotDataType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUED", value = ValuedKnotData.class),
        @JsonSubTypes.Type(name = "MULTI_KNOT", value = MultiKnotData.class),
        @JsonSubTypes.Type(name = "MAP_KNOT", value = MapKnotData.class)
})
public abstract class KnotData {

    public enum KnotDataType {
        VALUED(false),
        MULTI_KNOT(true),
        MAP_KNOT(true);

        @Getter
        private boolean isDeReferenced;

        KnotDataType(boolean isDeReferenced) {
            this.isDeReferenced = isDeReferenced;
        }
    }

    private KnotDataType knotDataType;

    protected KnotData(KnotDataType knotDataType) {
        this.knotDataType = knotDataType;
    }

    public abstract <T> T accept(KnotDataVisitor<T> knotDataVisitor);
}
