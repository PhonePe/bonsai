package com.phonepe.platform.bonsai.core.vital;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import lombok.Data;
import lombok.Getter;

/**
 * @author tushar.naik
 * @version 1.0  12/07/18 - 3:41 PM
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "dataType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUED", value = ValuedKnotData.class),
        @JsonSubTypes.Type(name = "MULTI_KNOT", value = MultiKnotData.class),
        @JsonSubTypes.Type(name = "MAP_KNOT", value = MapKnotData.class)
})
public abstract class KnotData {

    public enum DataType {
        VALUED(false),
        MULTI_KNOT(true),
        MAP_KNOT(true);

        @Getter
        private boolean isDeReferenced;

        DataType(boolean isDeReferenced) {
            this.isDeReferenced = isDeReferenced;
        }
    }

    private DataType dataType;

    protected KnotData(DataType dataType) {
        this.dataType = dataType;
    }

    public abstract <T> T accept(KnotDataVisitor<T> knotDataVisitor);
}
