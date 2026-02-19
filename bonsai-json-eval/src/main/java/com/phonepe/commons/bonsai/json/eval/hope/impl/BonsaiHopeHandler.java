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

package com.phonepe.commons.bonsai.json.eval.hope.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.phonepe.commons.bonsai.json.eval.hope.HopeHandler;
import com.phonepe.commons.bonsai.json.eval.hope.HopeFactory;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.lang.HopeLangEngine;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BonsaiHopeHandler implements HopeHandler {

    private static final int FILTER_EXPRESSION_CACHE_SIZE = 100;

    private final HopeLangEngine hopeLangEngine;

    private final Cache<String, Evaluatable> filterExpressionCache;

    public BonsaiHopeHandler() {
        this.hopeLangEngine = HopeFactory.createNewHopeLangEngine();
        this.filterExpressionCache = buildFilterExpressionCache(FILTER_EXPRESSION_CACHE_SIZE);
    }

    public BonsaiHopeHandler(final String userPackageForHope) {
        this.hopeLangEngine = HopeFactory.createNewHopeLangEngine(userPackageForHope);
        this.filterExpressionCache = buildFilterExpressionCache(FILTER_EXPRESSION_CACHE_SIZE);
    }

    public BonsaiHopeHandler(final HopeLangEngine hopeLangEngine,
                             final int filterExpressionCacheSize) {
        this.hopeLangEngine = hopeLangEngine;
        this.filterExpressionCache = buildFilterExpressionCache(filterExpressionCacheSize);
    }

    @Override
    public Evaluatable parse(final String filterExpression) {
        try {
            return filterExpressionCache.get(filterExpression, () -> hopeLangEngine.parse(filterExpression));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean parseAndEvaluate(final String filterExpression, final JsonNode jsonNode) {
        return hopeLangEngine.evaluate(parse(filterExpression), jsonNode);
    }

    private Cache<String, Evaluatable> buildFilterExpressionCache(final int filterExpressionCacheSize) {
        return CacheBuilder.newBuilder()
                .maximumSize(filterExpressionCacheSize == 0
                             ? FILTER_EXPRESSION_CACHE_SIZE
                             : filterExpressionCacheSize)
                .build();
    }
}
