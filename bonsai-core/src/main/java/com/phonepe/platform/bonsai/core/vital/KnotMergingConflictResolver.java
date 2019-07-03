package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.phonepe.platform.bonsai.models.data.KnotDataVisitor;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.structures.ConflictResolver;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is used to resolve conflicts when choosing between preferenceMap Knot and the Knot from the KnotStore
 *
 * @author tushar.naik
 * @version 1.0  2019-06-15 - 20:37
 */
public class KnotMergingConflictResolver implements ConflictResolver<Knot> {

    @Override
    public Knot resolveConflict(Knot preferredKnot, Knot defaultKnot) {
        if (preferredKnot == null) {
            return defaultKnot;
        }
        if (defaultKnot == null) {
            return preferredKnot;
        }
        if (!TreeUtils.isKnotDataOfSimilarType(preferredKnot, defaultKnot)) {
            throw new BonsaiError(BonsaiErrorCode.KNOT_RESOLUTION_ERROR,
                                  String.format("class mismatch preferredKnot:%s defaultKnot%s",
                                                preferredKnot.getClass(), defaultKnot.getClass()));
        }
        return preferredKnot.getKnotData().accept(new KnotDataVisitor<Knot>() {
            @Override
            public Knot visit(ValuedKnotData valuedKnotData) {
                return Knot.builder()
                           .knotData(preferredKnot.getKnotData())
                           .id(preferredKnot.getId())
                           .version(preferredKnot.getVersion()).build();
            }

            @Override
            public Knot visit(MultiKnotData multiKnotData) {
                List<String> mergedMultiKnotKey = Lists.newArrayList(multiKnotData.getKeys());
                Set<String> dedup = Sets.newHashSet(mergedMultiKnotKey);
                ((MultiKnotData) defaultKnot.getKnotData())
                        .getKeys()
                        .stream()
                        .filter(k -> !dedup.contains(k))
                        .forEach(mergedMultiKnotKey::add);
                return Knot.builder()
                           .knotData(new MultiKnotData(mergedMultiKnotKey))
                           .id(preferredKnot.getId())
                           .version(preferredKnot.getVersion()).build();
            }

            @Override
            public Knot visit(MapKnotData mapKnotData) {
                Map<String, String> mergedMapKey = Maps.newHashMap(mapKnotData.getMapKeys());
                mergedMapKey.putAll(((MapKnotData) defaultKnot.getKnotData()).getMapKeys());
                return Knot.builder()
                           .knotData(new MapKnotData(mergedMapKey))
                           .id(preferredKnot.getId())
                           .version(preferredKnot.getVersion()).build();
            }
        });
    }
}
