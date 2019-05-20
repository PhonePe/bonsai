package com.phonepe.platform.bonsai.core.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:34 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MapKnotData extends KnotData {
    private Map<String, String> mapKeys;

    public MapKnotData() {
        super(KnotDataType.MAP_KNOT);
    }

    @Builder
    public MapKnotData(Map<String, String> mapKeys) {
        super(KnotDataType.MAP_KNOT);
        this.mapKeys = mapKeys;
    }

    @Override
    public <T> T accept(KnotDataVisitor<T> knotDataVisitor) {
        return knotDataVisitor.visit(this);
    }
}
