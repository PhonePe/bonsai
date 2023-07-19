package com.phonepe.platform.bonsai.models.model;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class FlatNodeDetail {
    private List<Integer> path;
    private List<Edge> edges;
    private FlatNode flatNode;
    private long version;
}
