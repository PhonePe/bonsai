package com.phonepe.commons.bonsai.json.eval.hope;

import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
import io.appform.hope.lang.HopeLangEngine;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HopeFactory {
    public HopeLangEngine gethopeLangEngine() {
        return HopeLangEngine.builder()
                .autoFunctionDiscoveryEnabled(false)
                .errorHandlingStrategy(new InjectValueErrorHandlingStrategy())
                .build();
    }
}
