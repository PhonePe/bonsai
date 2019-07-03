package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableMap;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-15 - 20:42
 */
public class KnotMergingConflictResolverTest {
    @Test
    public void testConflictResolution() {
        KnotMergingConflictResolver knotMergingConflictResolver = new KnotMergingConflictResolver();
        Knot preferredKnot = Knot.builder().knotData(ValuedKnotData.stringValue("P1")).build();
        Knot defaultKnot = Knot.builder().knotData(ValuedKnotData.stringValue("D1")).build();
        Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);
        Assert.assertEquals(ValuedKnotData.stringValue("P1"), knot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void testConflictResolutionError() {
        KnotMergingConflictResolver knotMergingConflictResolver = new KnotMergingConflictResolver();
        Knot preferredKnot = Knot.builder().knotData(ValuedKnotData.stringValue("P1")).build();
        Knot defaultKnot = Knot.builder().knotData(MultiKnotData.builder().build()).build();
        Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);
        Assert.assertEquals(ValuedKnotData.stringValue("P1"), knot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void testConflictResolutionError2() {
        KnotMergingConflictResolver knotMergingConflictResolver = new KnotMergingConflictResolver();
        Knot preferredKnot = Knot.builder().knotData(MapKnotData.builder().build()).build();
        Knot defaultKnot = Knot.builder().knotData(MultiKnotData.builder().build()).build();
        Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);
        Assert.assertEquals(ValuedKnotData.stringValue("P1"), knot.getKnotData());
    }

    @Test
    public void testConflictResolutionMultiKnot() {
        KnotMergingConflictResolver knotMergingConflictResolver = new KnotMergingConflictResolver();
        Knot preferredKnot = Knot.builder().knotData(MultiKnotData.builder().key("pk1").key("pk2").build()).build();
        Knot defaultKnot = Knot.builder().knotData(MultiKnotData.builder().key("dk1").build()).build();
        Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);
        Assert.assertEquals(MultiKnotData.builder().key("pk1").key("pk2").key("dk1").build(), knot.getKnotData());
    }

    @Test
    public void testConflictResolutionMapKnot() {
        KnotMergingConflictResolver knotMergingConflictResolver = new KnotMergingConflictResolver();
        Knot preferredKnot = Knot.builder()
                                 .knotData(MapKnotData.builder()
                                                      .mapKeys(ImmutableMap.of("k1", "pk1", "k2", "pk2"))
                                                      .build())
                                 .build();
        Knot defaultKnot = Knot.builder()
                               .knotData(MapKnotData.builder().mapKeys(ImmutableMap.of("k3", "d1")).build())
                               .build();
        Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);
        Assert.assertEquals(MapKnotData.builder()
                                       .mapKeys(ImmutableMap.of("k1", "pk1", "k2", "pk2", "k3", "d1"))
                                       .build(), knot.getKnotData());
    }
}