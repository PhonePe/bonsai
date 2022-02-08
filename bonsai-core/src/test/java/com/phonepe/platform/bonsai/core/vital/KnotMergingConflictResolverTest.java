package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableMap;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KnotMergingConflictResolverTest {
    private KnotMergingConflictResolver knotMergingConflictResolver;

    @Before
    public void setUp() {
        knotMergingConflictResolver = new KnotMergingConflictResolver();
    }

    @After
    public void destroy() {
        knotMergingConflictResolver = null;
    }

    @Test
    public void Given_SimilarValueTypePreferredAndDefaultKnot_When_ResolvingConflict_Then_ReturnKnot() {
        final Knot preferredKnot = Knot.builder()
                .knotData(ValuedKnotData.stringValue("P1"))
                .build();
        final Knot defaultKnot = Knot.builder()
                .knotData(ValuedKnotData.stringValue("D1"))
                .build();

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(ValuedKnotData.stringValue("P1"), knot.getKnotData());
    }

    @Test
    public void given_similarMultiTypePreferredAndDefaultKnot_when_resolvingConflict_then_returnKnot() {
        final Knot preferredKnot = Knot.builder()
                .knotData(MultiKnotData.builder()
                        .key("pk1")
                        .key("pk2").build())
                .build();
        final Knot defaultKnot = Knot.builder()
                .knotData(MultiKnotData.builder()
                        .key("dk1").build())
                .build();

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(MultiKnotData.builder().key("pk1").key("pk2").key("dk1").build(), knot.getKnotData());
    }

    @Test
    public void given_similarMapTypePreferredAndDefaultKnot_when_resolvingConflict_then_returnKnot() {
        final Knot preferredKnot = Knot.builder()
                .knotData(MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("k1", "pk1", "k2", "pk2")).build())
                .build();
        final Knot defaultKnot = Knot.builder()
                .knotData(MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("k3", "d1")).build())
                .build();

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(MapKnotData.builder()
                .mapKeys(ImmutableMap.of("k1", "pk1", "k2", "pk2", "k3", "d1"))
                .build(), knot.getKnotData());
    }

    @Test
    public void given_nullPreferredAndNonNullDefaultKnot_when_resolvingConflict_then_returnDefaultKnot() {
        final Knot preferredKnot = null;
        final Knot defaultKnot = Knot.builder()
                .knotData(MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("k3", "d1")).build())
                .build();

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(defaultKnot, knot);
    }

    @Test
    public void given_nonNullPreferredAndNullDefaultKnot_when_resolvingConflict_then_returnPreferredKnot() {
        final Knot preferredKnot = Knot.builder()
                .knotData(MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("k1", "pk1", "k2", "pk2")).build())
                .build();
        final Knot defaultKnot = null;

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(preferredKnot, knot);
    }

    @Test(expected = BonsaiError.class)
    public void given_dissimilarPreferredAndDefaultKnotTypeOne_when_resolvingConflict_then_throwBonsaiError() {
        final Knot preferredKnot = Knot.builder()
                .knotData(ValuedKnotData.stringValue("P1"))
                .build();
        final Knot defaultKnot = Knot.builder()
                .knotData(MultiKnotData.builder().build())
                .build();

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(ValuedKnotData.stringValue("P1"), knot.getKnotData());
    }

    @Test(expected = BonsaiError.class)
    public void given_dissimilarPreferredAndDefaultKnotTypeTwo_when_resolvingConflict_then_throwBonsaiError() {
        final Knot preferredKnot = Knot.builder()
                .knotData(MapKnotData.builder().build())
                .build();
        final Knot defaultKnot = Knot.builder()
                .knotData(MultiKnotData.builder().build())
                .build();

        final Knot knot = knotMergingConflictResolver.resolveConflict(preferredKnot, defaultKnot);

        Assert.assertEquals(ValuedKnotData.stringValue("P1"), knot.getKnotData());
    }
}