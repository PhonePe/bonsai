/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.KnotDataVisitor;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.ByteValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.ObjectValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.bonsai.models.value.Value;
import com.phonepe.platform.bonsai.models.value.ValueVisitor;
import com.phonepe.commons.query.dsl.general.EqualsFilter;

import java.util.stream.IntStream;

public class TreeGenerationHelper {

    public static void generateEdges(Knot knot, Bonsai bonsai, int numOfEdges) {
        IntStream.range(0, numOfEdges)
                .forEach(i -> {
                    KnotData accept = knot.getKnotData().accept(new KnotDataVisitor<>() {
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
                        .accept(new ValueVisitor<>() {
                            @Override
                            public Value visit(NumberValue numberValue) {
                                return new NumberValue(numberValue.getValue().doubleValue() + 1);
                            }

                            @Override
                            public Value visit(StringValue stringValue) {
                                return new StringValue(stringValue.getValue() + i);
                            }

                            @Override
                            public Value visit(BooleanValue booleanValue) {
                                return new BooleanValue(i % 2 == 0);
                            }

                            @Override
                            public Value visit(ByteValue byteValue) {
                                return new ByteValue(byteValue.getValue());
                            }

                            @Override
                            public Value visit(JsonValue jsonValue) {
                                return new JsonValue(jsonValue.getValue());
                            }

                            @Override
                            public Value visit(final ObjectValue objectValue) {
                                return new ObjectValue(objectValue.getObject());
                            }
                        }))
                .build();
    }

    public static Knot createTestKnot(Bonsai<Context> bonsai, String key) {
        bonsai.createMapping(key, ValuedKnotData.stringValue("value"), null);
        return bonsai.createKnot(MultiKnotData.builder().key(key).build(), null);
    }
}
