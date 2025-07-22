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

package com.phonepe.commons.bonsai.conditions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConditionEngines {

    public static class TrueConditionEngine<C extends Condition, F> extends ConditionEngine<Void, C, F> {
        @Override
        public Boolean match(Void v1, C c) {
            return true;
        }

        @Override
        public Boolean match(Void v1, C c, F v2) {
            return true;
        }
    }

    public static <C extends Condition, F> ConditionEngine<Void, C, F> trueConditionEngine() {
        return new TrueConditionEngine<>();
    }
}
