package com.phonepe.platform.bonsai.models.blocks;

import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

@Data
@ToString
@NoArgsConstructor
public class Knot {
    private String id;
    private long version;
    private KnotData knotData;
    private OrderedList<EdgeIdentifier> edges;
    private Map<String, Object> properties;

    @Builder
    public Knot(final String id,
                final long version,
                final OrderedList<EdgeIdentifier> edges,
                final KnotData knotData,
                final Map<String, Object> properties) {
        this.id = id;
        this.version = version;
        this.edges = edges;
        this.knotData = knotData;
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Knot knot = (Knot) o;
        return Objects.equals(id, knot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Knot updateVersion() {
        this.version = System.currentTimeMillis();
        return this;
    }

    public Knot deepClone(long version) {
        return Knot.builder()
                .id(this.id)
                .version(version)
                .edges(this.edges)
                .knotData(this.knotData)
                .properties(this.properties)
                .build();
    }

}
