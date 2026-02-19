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

import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.commons.bonsai.json.eval.hope.HopeHandler;
import io.appform.hope.core.Evaluatable;

public class BonsaiHopeEngine implements HopeHandler {

    private final HopeHandler hopeHandler;

    public BonsaiHopeEngine(final HopeHandler hopeHandler) {
        this.hopeHandler = hopeHandler;
    }

    @Override
    public Evaluatable parse(final String filterExpression) {
        return hopeHandler.parse(filterExpression);
    }

    @Override
    public boolean parseAndEvaluate(final String filterExpression, final JsonNode jsonNode) {
        return hopeHandler.parseAndEvaluate(filterExpression, jsonNode);
    }
}
