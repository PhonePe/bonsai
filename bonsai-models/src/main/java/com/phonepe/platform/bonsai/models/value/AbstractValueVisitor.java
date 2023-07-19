package com.phonepe.platform.bonsai.models.value;

import com.phonepe.platform.bonsai.models.BonsaiConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class AbstractValueVisitor<T> implements ValueVisitor<T> {

    private final T defaultValue;

    public AbstractValueVisitor() {
        this(null);
    }

    public AbstractValueVisitor(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T visit(NumberValue numberValue) {
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][{}] default value being returned, numberValue is {}, defaultValue:{}", MDC.get(
                    BonsaiConstants.EVALUATION_ID), numberValue, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public T visit(StringValue stringValue) {
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][{}] default value being returned, stringValue is {}, defaultValue:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), stringValue, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public T visit(BooleanValue booleanValue) {
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][{}] default value being returned, booleanValue is {}, defaultValue:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), booleanValue, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public T visit(ByteValue byteValue) {
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][{}] default value being returned, byteValue is {}, defaultValue:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), byteValue, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public T visit(JsonValue jsonValue) {
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][{}] default value being returned, jsonValue is {}, defaultValue:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), jsonValue, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public T visit(final ObjectValue objectValue) {
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][{}] default value being returned, jsonValue is {}, defaultValue:{}",
                      MDC.get(BonsaiConstants.EVALUATION_ID), objectValue, defaultValue);
        }
        return defaultValue;
    }
}
