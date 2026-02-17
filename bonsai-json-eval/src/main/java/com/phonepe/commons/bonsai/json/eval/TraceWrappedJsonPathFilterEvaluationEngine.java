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

package com.phonepe.commons.bonsai.json.eval;

import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.general.AnyFilter;
import com.phonepe.commons.query.dsl.general.ContainsFilter;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.ExistsFilter;
import com.phonepe.commons.query.dsl.general.GenericFilter;
import com.phonepe.commons.query.dsl.general.HopeFilter;
import com.phonepe.commons.query.dsl.general.InFilter;
import com.phonepe.commons.query.dsl.general.MissingFilter;
import com.phonepe.commons.query.dsl.general.NotEqualsFilter;
import com.phonepe.commons.query.dsl.general.NotInFilter;
import com.phonepe.commons.query.dsl.logical.AndFilter;
import com.phonepe.commons.query.dsl.logical.NotFilter;
import com.phonepe.commons.query.dsl.logical.OrFilter;
import com.phonepe.commons.query.dsl.numeric.BetweenFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterThanFilter;
import com.phonepe.commons.query.dsl.numeric.LessEqualFilter;
import com.phonepe.commons.query.dsl.numeric.LessThanFilter;
import com.phonepe.commons.query.dsl.string.StringEndsWithFilter;
import com.phonepe.commons.query.dsl.string.StringRegexMatchFilter;
import com.phonepe.commons.query.dsl.string.StringStartsWithFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

@Slf4j
public class TraceWrappedJsonPathFilterEvaluationEngine<C extends JsonEvalContext, F>
        extends JsonPathFilterEvaluationEngine<C, F> {

    public TraceWrappedJsonPathFilterEvaluationEngine(final String entityId,
                                                      final C context,
                                                      final Predicate<GenericFilterContext<C, F>> genericFilterHandler,
                                                      final BonsaiHopeEngine hopeEngine) {
        super(entityId, context, genericFilterHandler, null, hopeEngine);
    }

    public TraceWrappedJsonPathFilterEvaluationEngine(final String entityId,
                                                      final C context,
                                                      final Predicate<GenericFilterContext<C, F>> genericFilterHandler,
                                                      final F entityMetadata,
                                                      final BonsaiHopeEngine hopeEngine) {
        super(entityId, context, genericFilterHandler, entityMetadata, hopeEngine);
    }

    @Override
    public Boolean visit(ContainsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(LessThanFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(LessEqualFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(GreaterThanFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(BetweenFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(GreaterEqualFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(NotInFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(NotEqualsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(MissingFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(InFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(ExistsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(EqualsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(AnyFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(AndFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(OrFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(NotFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(StringStartsWithFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(StringEndsWithFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(StringRegexMatchFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(GenericFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(HopeFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    private void trace(Filter filter, boolean result) {
        log.trace("[bonsai][{}][{}][{}] eval result: {}", filter.getClass().getSimpleName(), entityId, context.id(),
                result);
    }
}
