package com.phonepe.platform.bonsai.models.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

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
