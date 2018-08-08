package com.phonepe.platform.bonsai.core.core;

import lombok.Data;
import lombok.Getter;

/**
 * @author tushar.naik
 * @version 1.0  12/07/18 - 3:41 PM
 */
@Data
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
