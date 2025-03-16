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

package com.phonepe.platform.bonsai.conditions;

import java.util.List;
import java.util.Optional;

/**
 * Matcher is a utility class that provides interfaces for matching a given value with a condition.
 */
public class Matcher {
    /**
     * A Matcher interface, that uses a single argument for its matching purpose
     *
     * @param <R> Return value
     * @param <V> Input value
     */
    @FunctionalInterface
    public interface UniMatcher<R, V> {

        /**
         * Try to match the given argument with a default condition
         *
         * @param v argument in contention
         * @return result of the match
         */
        R match(V v);
    }

    /**
     * A special unimatcher, which returns a boolean result to a match() invocation
     *
     * @param <V>
     */
    @FunctionalInterface
    public interface BooleanUniMatcher<V> extends UniMatcher<Boolean, V> {
    }

    /**
     * A conditional matcher interface is a Bimatcher, which uses 2 arguments for its matching purposes
     *
     * @param <V>         contending entity
     * @param <Condition> list of condition to be matched with
     */
    public interface ConditionalMatcher<V, Condition> {

        /**
         * Try to match a contending entity with a Collection of Criteria,
         * and return the condition which matches the contending entity v
         *
         * @param v1            contending value
         * @param conditionList list of condition to be matched with
         * @return the condition, which matches the contending value
         */
        Optional<Condition> match(V v1, List<Condition> conditionList);

        /**
         * check if the condition matches the contender
         *
         * @param v1        contender
         * @param condition condition to be matched
         * @return true if there is a match
         */
        Boolean match(V v1, Condition condition);
    }
}
