package com.phonepe.platform.bonsai.models.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MapFlatNode extends FlatNode {
    private Map<String, String> nodeMap;

    public MapFlatNode() {
        super(FlatNodeType.MAP);
    }

    public MapFlatNode(Map<String, String> nodeMap) {
        super(FlatNodeType.MAP);
        this.nodeMap = nodeMap;
    }

    @Override
    public <T> T accept(FlatNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
