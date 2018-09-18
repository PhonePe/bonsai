package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.query.filter.general.EqualsFilter;
import com.phonepe.platform.bonsai.core.vital.Knot;
import com.phonepe.platform.bonsai.core.vital.KnotData;
import com.phonepe.platform.bonsai.core.vital.KnotDataVisitor;
import com.phonepe.platform.bonsai.core.vital.Variation;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicKnot;
import com.phonepe.platform.bonsai.models.value.DataValue;
import com.phonepe.platform.bonsai.models.value.ReferenceValue;
import com.phonepe.platform.bonsai.models.value.Value;
import com.phonepe.platform.bonsai.models.value.ValueVisitor;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author tushar.naik
 * @version 1.0  05/09/18 - 1:56 PM
 */
public class TreeUtils {
    private static Random random = new Random();

    public static void generateEdges(Knot knot, Bonsai bonsai, int numOfEdges, int levels) {
        IntStream.range(0, levels)
                 .forEach(level -> {
                     generateEdges(knot, bonsai, numOfEdges);
                 });

    }

    public static void generateEdges(Knot knot, Bonsai bonsai, int numOfEdges) {
        IntStream.range(0, numOfEdges)
                 .forEach(i -> {
                     KnotData accept = knot.getKnotData().accept(new KnotDataVisitor<KnotData>() {
                         @Override
                         public KnotData visit(ValuedKnotData valuedKnotData) {
                             return getKnotData(valuedKnotData, i);
                         }

                         @Override
                         public KnotData visit(MultiKnotData multiKnotData) {
                             return null;
                         }

                         @Override
                         public KnotData visit(MapKnotData mapKnotData) {
                             return null;
                         }
                     });
                     AtomicKnot newlyCreatedKnot = bonsai.createKnot(accept);
                     bonsai.addVariation(knot.getId(), Variation.builder()
//                                                                .id("E" + i + ":ED" + random.nextInt(99999))
                                                                .filter(new EqualsFilter("E", i))
                                                                .knotId(newlyCreatedKnot.getId())
                                                                .build());
                 });

    }

    private static ValuedKnotData getKnotData(ValuedKnotData valuedKnotData, int i) {
        return ValuedKnotData.builder()
                             .value(valuedKnotData.getValue()
                                                  .accept(new ValueVisitor<Value>() {
                                                      @Override
                                                      public Value visit(DataValue dataValue) {
                                                          return DataValue.builder()
                                                                          .data(dataValue.getData().toString() + i)
                                                                          .build();
                                                      }

                                                      @Override
                                                      public Value visit(ReferenceValue referenceValue) {
                                                          return null;
                                                      }
                                                  }))
                             .build();
    }
}
