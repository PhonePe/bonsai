package com.phonepe.platform.bonsai.models.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ListFlatNode extends FlatNode {

    private List<String> nodes;

    public ListFlatNode() {
        super(FlatNodeType.LIST);
    }

    public ListFlatNode(List<String> nodes) {
        super(FlatNodeType.LIST);
        this.nodes = nodes;
    }

    @Override
    public <T> T accept(FlatNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
