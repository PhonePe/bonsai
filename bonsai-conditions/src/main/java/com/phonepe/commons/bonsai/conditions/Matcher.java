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
     * @param <C> list of condition to be matched with
     * @param <F> An associated entity providing additional context for filtering.
     */
    public interface ConditionalMatcher<V, C, F> {

        /**
         * Try to match a contending entity with a Collection of Criteria,
         * and return the condition which matches the contending entity v
         *
         * @param v1            contending value
         * @param conditionList list of condition to be matched with
         * @return the condition, which matches the contending value
         */
        Optional<C> match(V v1, List<C> conditionList);

        /**
         * check if the condition matches the contender
         *
         * @param v1        contender
         * @param condition condition to be matched
         * @return true if there is a match
         */
        Boolean match(V v1, C condition);

        /**
         * Tries to match a contending entity against a list of conditions, using an additional associated entity for more complex filtering.
         * This allows for nuanced logic where the match criteria might depend on both the primary entity and some other contextual data (e.g., using an evaluation key for uniform sampling).
         *
         * @param v1               The contending entity to be evaluated.
         * @param conditionList       A list of conditions to match against.
         * @param associatedEntity An additional entity that provides context for the matching logic.
         * @return An Optional containing the first matching condition, or an empty Optional if no match is found.
         */
        Optional<C> match(V v1, List<C> conditionList, F associatedEntity);

        /**
         * Checks if a single condition matches the contending entity, using an additional associated entity.
         *
         * @param v1               The contending entity to be evaluated.
         * @param condition        The single condition to match against.
         * @param associatedEntity An additional entity that provides context for the matching logic.
         * @return `true` if the condition is a match, otherwise `false`.
         */
        Boolean match(V v1, C condition, F associatedEntity);
    }
}
