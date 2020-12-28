package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.value.*;
import com.phonepe.platform.bonsai.models.data.*;
import com.phonepe.platform.query.dsl.general.EqualsFilter;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author tushar.naik
 * @version 1.0  05/09/18 - 1:56 PM
 */
public class TreeGenerationHelper {

    private static Random random = new Random();

    public static void generateEdges(Knot knot, Bonsai bonsai, int numOfEdges, int levels) {
        IntStream.range(0, levels)
                 .forEach(level -> generateEdges(knot, bonsai, numOfEdges));

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
                     Knot newlyCreatedKnot = bonsai.createKnot(accept, null);
                     bonsai.addVariation(knot.getId(), Variation.builder()
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
                                                      public Value visit(NumberValue numberValue) {
                                                          return new NumberValue(numberValue.getValue()
                                                                                            .doubleValue() + 1);
                                                      }

                                                      @Override
                                                      public Value visit(StringValue stringValue) {
                                                          return new StringValue(stringValue.getValue() + i);
                                                      }

                                                      @Override
                                                      public Value visit(BooleanValue booleanValue) {
                                                          return null;
                                                      }

                                                      @Override
                                                      public Value visit(ByteValue byteValue) {
                                                          return null;
                                                      }

                                                      @Override
                                                      public Value visit(JsonValue jsonValue) {
                                                          return null;
                                                      }
                                                  }))
                             .build();
    }

    public static Knot createTestKnot(Bonsai<Context> bonsai, String key) {
        bonsai.createMapping(key, ValuedKnotData.stringValue("value"), null);
        return bonsai.createKnot(MultiKnotData.builder().key(key).build(), null);
    }
}
