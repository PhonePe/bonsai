package com.phonepe.platform.bonsai.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.platform.bonsai.core.structures.MapEntry;
import com.phonepe.platform.bonsai.models.AbstractNodeVisitor;
import com.phonepe.platform.bonsai.models.BonsaiConstants;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.NodeVisitor;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.model.FlatNode;
import com.phonepe.platform.bonsai.models.model.FlatNodeVisitor;
import com.phonepe.platform.bonsai.models.model.ListFlatNode;
import com.phonepe.platform.bonsai.models.model.MapFlatNode;
import com.phonepe.platform.bonsai.models.model.ValueFlatNode;
import com.phonepe.platform.bonsai.models.value.AbstractValueVisitor;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.ByteValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.ObjectValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.bonsai.models.value.ValueVisitor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class NodeUtils {

    private static final String ERROR_MESSAGE = "[bonsai][{}] Error getting chimera value as JsonNode for keyNode:{}";

    private static final ValueVisitor<String> STRING_VALUE_VISITOR = new ValueVisitor<String>() {
        @Override
        public String visit(final NumberValue numberValue) {
            return String.valueOf(numberValue.getValue());
        }

        @Override
        public String visit(final StringValue stringValue) {
            return stringValue.getValue();
        }

        @Override
        public String visit(final BooleanValue booleanValue) {
            return String.valueOf(booleanValue.isValue());
        }

        @Override
        public String visit(final ByteValue byteValue) {
            return new String(byteValue.getValue());
        }

        @Override
        public String visit(final JsonValue jsonValue) {
            return jsonValue.getValue().asText();
        }

        @Override
        public String visit(final ObjectValue objectValue) {
            return objectValue.getObject().toString();
        }
    };

    public Boolean asBoolean(final KeyNode keyNode, final Boolean defaultValue) {
        try {
            if (keyNode == null || keyNode.getNode() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("[bonsai][{}] default value being returned, keyNode is {}", MDC.get(
                            BonsaiConstants.EVALUATION_ID), keyNode);
                }
                return defaultValue;
            }
            return keyNode.getNode().accept(new NodeVisitor<Boolean>() {
                @Override
                public Boolean visit(final ListNode listNode) {
                    return listNode.getNodes().stream().allMatch(node -> NodeUtils.asBoolean(node, defaultValue));
                }

                @Override
                public Boolean visit(final ValueNode valueNode) {
                    return valueNode.getValue().accept(new AbstractValueVisitor<Boolean>(defaultValue) {

                        @Override
                        public Boolean visit(final BooleanValue booleanValue) {
                            return booleanValue.isValue();
                        }
                    });
                }

                @Override
                public Boolean visit(final MapNode mapNode) {
                    return mapNode.getNodeMap().values().stream()
                            .allMatch(node -> NodeUtils.asBoolean(node, defaultValue));
                }
            });
        } catch (Exception e) {
            log.error("[bonsai][{}] Error getting chimera value as boolean for keyNode:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), keyNode, e);
            return defaultValue;
        }
    }

    public Boolean asBoolean(final FlatNode flatNode, final Boolean defaultValue) {
        try {
            if (flatNode == null) {
                return defaultValue;
            }
            return flatNode.accept(new FlatNodeVisitor<Boolean>() {
                @Override
                public Boolean visit(ValueFlatNode valueFlatNode) {
                    return valueFlatNode.getValue().accept(new AbstractValueVisitor<Boolean>(defaultValue) {
                        @Override
                        public Boolean visit(BooleanValue booleanValue) {
                            return booleanValue.isValue();
                        }
                    });
                }

                @Override
                public Boolean visit(ListFlatNode listFlatNode) {
                    if (log.isDebugEnabled()) {
                        log.debug("[bonsai][{}] default value being returned for listFlatNode:{}",
                                  MDC.get(BonsaiConstants.EVALUATION_ID), listFlatNode);
                    }
                    return defaultValue;
                }

                @Override
                public Boolean visit(MapFlatNode mapFlatNode) {
                    if (log.isDebugEnabled()) {
                        log.debug("[bonsai][{}] default value being returned for mapFlatNode:{}",
                                  MDC.get(BonsaiConstants.EVALUATION_ID), mapFlatNode);
                    }
                    return defaultValue;
                }
            });
        } catch (Exception e) {
            log.error("[bonsai][{}] Error getting chimera value as boolean for flatNode:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), flatNode, e);
            return defaultValue;
        }
    }

    public String asString(final FlatNode flatNode, final String defaultValue) {
        try {
            if (flatNode == null) {
                return defaultValue;
            }
            return flatNode.accept(new FlatNodeVisitor<String>() {
                @Override
                public String visit(ValueFlatNode valueFlatNode) {
                    return valueFlatNode.getValue().accept(STRING_VALUE_VISITOR);
                }

                @Override
                public String visit(ListFlatNode listFlatNode) {
                    if (log.isDebugEnabled()) {
                        log.debug("[bonsai][{}] default value being returned for listFlatNode:{}",
                                  MDC.get(BonsaiConstants.EVALUATION_ID), listFlatNode);
                    }
                    return defaultValue;
                }

                @Override
                public String visit(MapFlatNode mapFlatNode) {
                    if (log.isDebugEnabled()) {
                        log.debug("[bonsai][{}] default value being returned for mapFlatNode:{}",
                                  MDC.get(BonsaiConstants.EVALUATION_ID), mapFlatNode);
                    }
                    return defaultValue;
                }
            });
        } catch (Exception e) {
            log.error("[bonsai][{}] Error getting chimera value as boolean for flatNode:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), flatNode, e);
            return defaultValue;
        }
    }

    public String asString(final KeyNode keyNode, final String defaultValue) {
        try {
            if (keyNode == null || keyNode.getNode() == null) {
                return defaultValue;
            }
            return keyNode.getNode().accept(new AbstractNodeVisitor<String>(defaultValue) {
                @Override
                public String visit(final ValueNode valueNode) {
                    return valueNode.getValue().accept(STRING_VALUE_VISITOR);
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), keyNode, e);
            return defaultValue;
        }
    }

    public Number asNumber(final KeyNode keyNode, final Number defaultValue) {
        try {
            if (keyNode == null || keyNode.getNode() == null) {
                return defaultValue;
            }
            return keyNode.getNode().accept(new AbstractNodeVisitor<Number>(defaultValue) {
                @Override
                public Number visit(final ValueNode valueNode) {
                    return valueNode.getValue().accept(valueToNumberVisitor(defaultValue));
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), keyNode, e);
            return defaultValue;
        }
    }

    public Number asNumber(final FlatNode flatNode, final Number defaultValue) {
        try {
            if (flatNode == null) {
                return defaultValue;
            }
            return flatNode.accept(new FlatNodeVisitor<Number>() {
                @Override
                public Number visit(ValueFlatNode valueFlatNode) {
                    return valueFlatNode.getValue().accept(valueToNumberVisitor(defaultValue));
                }

                @Override
                public Number visit(ListFlatNode listFlatNode) {
                    if (log.isDebugEnabled()) {
                        log.debug("[bonsai][{}] default value being returned for listFlatNode:{}, value:{}",
                                  MDC.get(BonsaiConstants.EVALUATION_ID), listFlatNode, defaultValue);
                    }
                    return defaultValue;
                }

                @Override
                public Number visit(MapFlatNode mapFlatNode) {
                    if (log.isDebugEnabled()) {
                        log.debug("[bonsai][{}] default value being returned for mapFlatNode:{}, value:{}",
                                  MDC.get(BonsaiConstants.EVALUATION_ID), mapFlatNode, defaultValue);
                    }
                    return defaultValue;
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), flatNode, e);
            return defaultValue;
        }
    }

    public JsonNode asJsonNode(final KeyNode keyNode, final JsonNode defaultValue) {
        try {
            if (keyNode == null || keyNode.getNode() == null) {
                return defaultValue;
            }
            return keyNode.getNode().accept(new AbstractNodeVisitor<JsonNode>(defaultValue) {

                @Override
                public JsonNode visit(final ValueNode valueNode) {
                    return valueNode.getValue().accept(valueToJsonNodeVisitor(defaultValue));
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), keyNode, e);
            return defaultValue;
        }
    }

    public JsonNode asJsonNode(final FlatNode flatNode, final JsonNode defaultValue) {
        try {
            if (flatNode == null) {
                return defaultValue;
            }
            return flatNode.accept(new FlatNodeVisitor<JsonNode>() {
                @Override
                public JsonNode visit(ValueFlatNode valueFlatNode) {
                    if (valueFlatNode.getValue() == null) {
                        return defaultValue;
                    }
                    return valueFlatNode.getValue().accept(valueToJsonNodeVisitor(defaultValue));
                }

                @Override
                public JsonNode visit(ListFlatNode listFlatNode) {
                    return defaultValue;
                }

                @Override
                public JsonNode visit(MapFlatNode mapFlatNode) {
                    return defaultValue;
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), flatNode, e);
            return defaultValue;
        }
    }

    public <T> T asObject(final KeyNode node,
                          final Class<T> aClass,
                          final T defaultValue,
                          final ObjectMapper mapper) {
        try {
            if (node == null || node.getNode() == null) {
                return defaultValue;
            }
            return node.getNode().accept(new NodeVisitor<T>() {
                @Override
                public T visit(final ListNode listNode) {
                    return defaultValue;
                }

                @Override
                public T visit(final ValueNode valueNode) {
                    return valueNode.getValue().accept(valueToObjectVisitor(defaultValue, aClass, mapper));
                }

                @Override
                public T visit(final MapNode mapNode) {
                    return defaultValue;
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), node, e);
            return defaultValue;
        }
    }

    public <T> T asObject(final FlatNode flatNode,
                          final Class<T> aClass,
                          final T defaultValue,
                          final ObjectMapper mapper) {
        try {
            if (flatNode == null) {
                return defaultValue;
            }
            return flatNode.accept(new FlatNodeVisitor<T>() {
                @Override
                public T visit(ValueFlatNode valueFlatNode) {
                    if (valueFlatNode.getValue() == null) {
                        return defaultValue;
                    }
                    return valueFlatNode.getValue().accept(valueToObjectVisitor(defaultValue, aClass, mapper));
                }

                @Override
                public T visit(ListFlatNode listFlatNode) {
                    return defaultValue;
                }

                @Override
                public T visit(MapFlatNode mapFlatNode) {
                    return defaultValue;
                }
            });
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, MDC.get(BonsaiConstants.EVALUATION_ID), flatNode, e);
            return defaultValue;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  MapKnot related Helpers  //////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public Map<String, Boolean> asMapOfBoolean(final KeyNode keyNode,
                                               final Map<String, Boolean> defaultValue) {
        return asMap(keyNode, defaultValue, NodeUtils::asBoolean);
    }

    public Map<String, Number> asMapOfNumber(final KeyNode keyNode,
                                             final Map<String, Number> defaultValue) {
        return asMap(keyNode, defaultValue, NodeUtils::asNumber);
    }

    public Map<String, String> asMapOfString(final KeyNode keyNode,
                                             final Map<String, String> defaultValue) {
        return asMap(keyNode, defaultValue, NodeUtils::asString);
    }

    /**
     * convert a KeyNode by parsing its MapNode
     *
     * @param keyNode      key node to be parsed
     * @param defaultValue default value
     * @param converter    a bifunction that takes in node and default value, and returns converted value
     * @param <T>          Type of return
     */
    public <T> Map<String, T> asMap(final KeyNode keyNode,
                                    final Map<String, T> defaultValue,
                                    final BiFunction<KeyNode, T, T> converter) {
        try {
            if (keyNode == null || keyNode.getNode() == null) {
                return defaultValue;
            }
            return keyNode.getNode().accept(new AbstractNodeVisitor<Map<String, T>>(defaultValue) {
                @Override
                public Map<String, T> visit(final MapNode mapNode) {
                    return mapNode.getNodeMap()
                            .entrySet().stream()
                            .map(stringNodePair -> MapEntry
                                    .of(stringNodePair.getKey(),
                                        converter.apply(stringNodePair.getValue(),
                                                        defaultValue == null ? null
                                                                             : defaultValue.get(
                                                                                     stringNodePair.getKey()))))
                            .collect(HashMap::new, (map, entry) -> map
                                    .put(entry.getK(), entry.getV()), HashMap::putAll);
                }
            });
        } catch (Exception e) {
            log.error("[chimera] Error getting chimera value as JsonNode for keyNode:" + keyNode, e);
            return defaultValue;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  ListKnot related Helpers  /////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<Boolean> asListOfBoolean(final KeyNode keyNode,
                                         final List<Boolean> defaultValue) {
        return asList(keyNode, defaultValue, NodeUtils::asBoolean);
    }

    public List<Number> asListOfNumber(final KeyNode keyNode,
                                       final List<Number> defaultValue) {
        return asList(keyNode, defaultValue, NodeUtils::asNumber);
    }

    public List<String> asListOfString(final KeyNode keyNode,
                                       final List<String> defaultValue) {
        return asList(keyNode, defaultValue, NodeUtils::asString);
    }

    /**
     * convert a KeyNode by parsing its ListNode
     *
     * @param keyNode      key node to be parsed
     * @param defaultValue default value
     * @param converter    a bifunction that takes in node and default value, and returns converted value
     * @param <T>          Type of return
     */
    public <T> List<T> asList(final KeyNode keyNode,
                              final List<T> defaultValue,
                              final BiFunction<KeyNode, T, T> converter) {
        try {
            if (keyNode == null || keyNode.getNode() == null) {
                return defaultValue;
            }
            return keyNode.getNode().accept(new AbstractNodeVisitor<List<T>>(defaultValue) {
                @Override
                public List<T> visit(ListNode listNode) {
                    return listNode
                            .getNodes()
                            .stream()
                            .map(node -> converter.apply(node, null))
                            .collect(Collectors.toList());
                }
            });
        } catch (Exception e) {
            log.error("[chimera] Error getting chimera value as JsonNode for keyNode:" + keyNode, e);
            return defaultValue;
        }
    }

    private static AbstractValueVisitor<Number> valueToNumberVisitor(Number defaultValue) {
        return new AbstractValueVisitor<Number>(defaultValue) {
            @Override
            public Number visit(final NumberValue numberValue) {
                return numberValue.getValue();
            }

            @Override
            public Number visit(final StringValue stringValue) {
                return Double.parseDouble(stringValue.getValue());
            }

            @Override
            public Number visit(final BooleanValue booleanValue) {
                return booleanValue.isValue() ? 1 : 0;
            }
        };
    }

    private static AbstractValueVisitor<JsonNode> valueToJsonNodeVisitor(JsonNode defaultValue) {
        return new AbstractValueVisitor<JsonNode>(defaultValue) {
            @Override
            public JsonNode visit(final JsonValue jsonValue) {
                return jsonValue.getValue();
            }
        };
    }

    private static <T> AbstractValueVisitor<T> valueToObjectVisitor(final T defaultValue,
                                                                    final Class<T> type,
                                                                    final ObjectMapper mapper) {
        return new AbstractValueVisitor<T>(defaultValue) {
            @Override
            public T visit(final StringValue stringValue) {
                return tryFunction(
                        () -> readType(
                                () -> stringValue.getValue().getBytes(StandardCharsets.UTF_8),
                                () -> type.cast(stringValue.getValue()),
                                () -> mapper.readValue(stringValue.getValue(), type)), defaultValue);
            }

            @Override
            public T visit(final ByteValue byteValue) {
                return tryFunction(
                        () -> readType(
                                byteValue::getValue,
                                () -> type.cast(byteValue.getValue()),
                                () -> mapper.readValue(byteValue.getValue(), type)), defaultValue);
            }

            @Override
            public T visit(final JsonValue jsonValue) {
                return tryFunction(
                        () -> readType(
                                () -> jsonValue.getValue().asText().getBytes(StandardCharsets.UTF_8),
                                () -> type.cast(jsonValue.getValue().asText()),
                                () -> mapper.treeToValue(jsonValue.getValue(), type)), defaultValue);
            }

            @Override
            public T visit(final ObjectValue objectValue) {
                return tryFunction(
                        () -> readType(
                                () -> objectValue.getObject().toString().getBytes(StandardCharsets.UTF_8),
                                () -> type.cast(objectValue.getObject()),
                                () -> type.cast(objectValue.getObject())), defaultValue);
            }

            private T readType(final ESupplier<byte[]> byteValueSupplier,
                               final ESupplier<T> stringValueSupplier,
                               final ESupplier<T> mappedValueSupplier) throws Exception {
                if (byte[].class.equals(type)) {
                    return (T) byteValueSupplier.get();
                }
                if (String.class.equals(type)) {
                    return stringValueSupplier.get();
                }
                return mappedValueSupplier.get();
            }

            private <R> R tryFunction(ESupplier<R> supplier, R defaultValue) {
                try {
                    return supplier.get();
                } catch (Exception e) {
                    return defaultValue;
                }
            }
        };
    }

    @FunctionalInterface
    private interface ESupplier<T> {
        T get() throws Exception;
    }
}
