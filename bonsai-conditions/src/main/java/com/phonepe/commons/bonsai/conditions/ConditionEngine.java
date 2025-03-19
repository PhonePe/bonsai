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

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

/**
 * An abstract conditional matcher, who matches an entity against a list of conditions
 */
public abstract class ConditionEngine<E, C extends Condition> implements Matcher.ConditionalMatcher<E, C> {

    /**
     * a random matcher to check applicability of a criteria, which is configured with a percentage
     */
    private static final RandomMatcher RANDOM_MATCHER = new DecimalRandomMatcher();

    /**
     * matches the entity in contention with a list of conditions
     *
     * @param entity     single entity
     * @param conditions list of criteria, to which the incoming entity will be matched tos
     * @return matching criteria
     */
    @Override
    public Optional<C> match(E entity, final List<C> conditions) {
        return conditions.stream()
                .filter(condition -> condition.isLive() && (RANDOM_MATCHER.match(condition.getPercentage()) && match(
                        entity, condition)))
                .findFirst();
    }

    /**
     * check if the contender is null, or if the value and contender are the same
     *
     * @param value     value to be checked
     * @param contender check equality contention
     * @param <T>       type of the value and the contender>
     * @return true if contender is null, or if the value and contender are the same
     */
    public static <T> boolean testEqualityContention(T value, T contender) {
        return contender == null || value.equals(contender);
    }

    /**
     * check if the contender is null, or if the value and contender are the same
     *
     * @param value     value to be checked
     * @param contender check equality contention
     * @return true if contender is null, or if the value and contender are the same
     */
    public static boolean testStringEqualityContention(String value, String contender) {
        return contender == null || value.equalsIgnoreCase(contender);
    }

    /**
     * check if the regex check is required, and if there is a match between the string and the regex string
     *
     * @param string      string to be checked
     * @param regexString regex string for matching purpose
     * @return true if regex is null, or if the string matches the regex
     */
    public static boolean testRegexContention(String string, String regexString) {
        return regexString == null
                || string.matches(regexString);
    }

    /**
     * check if set of values is contained entirely in another iterable set of values
     *
     * @param iterable super set which could contain the set of values
     * @param value    set of values to be checked with the iterable set
     * @param <T>      type of values/iterables
     * @return true, if values is null or if the set of values is entirely contained in the set of iterables
     */
    public static <T> boolean testContainsContention(Set<T> iterable, Set<T> value) {
        return value == null
                || (iterable != null && iterable.size() >= value.size() && Sets.intersection(iterable, value)
                .size() == iterable.size());
    }


    /**
     * check if baseString contains all Strings of a collection
     *
     * @param baseString string
     * @param value      collection of strings to be checked with baseString
     * @return true if all values are contained in string
     */
    public static boolean testContainsAll(String baseString, Collection<String> value) {
        return value == null
                || (baseString != null && !baseString.isEmpty() && value.stream()
                .allMatch(baseString::contains));
    }

    /**
     * check if baseString contains any Strings of a collection
     *
     * @param baseString string
     * @param value      collection of strings to be checked with baseString
     * @return true if any values are contained in string
     */
    public static boolean testContainsAny(String baseString, Collection<String> value) {
        return value == null
                || (baseString != null && !baseString.isEmpty() && value.stream()
                .anyMatch(baseString::contains));
    }

    /**
     * check if baseString doesnt contains all Strings of a collection
     *
     * @param baseString string
     * @param value      collection of strings to be checked with baseString
     * @return true if all values are not contained in string
     */
    public static boolean testExcludesAll(String baseString, Collection<String> value) {
        return value == null
                || (baseString != null && !baseString.isEmpty() && !value.stream()
                .allMatch(baseString::contains));
    }

    /**
     * check if baseString doesnt contains any Strings of a collection
     *
     * @param baseString string
     * @param value      collection of strings to be checked with baseString
     * @return true if any values are not contained in string
     */
    public static boolean testExcludesAny(String baseString, Collection<String> value) {
        return value == null
                || (baseString != null && !baseString.isEmpty() && value.stream()
                .noneMatch(baseString::contains));
    }

    /**
     * check if the entire value map is entirely contained in the contender map's 0th keys
     *
     * @param contender contender map (Eg. the parameter map)
     * @param value     the value map
     * @param <K>       key types
     * @param <V>       value types
     * @return true if value is null or if all key-value pairs of value are entirely contained in the contender map
     */
    public static <K, V> boolean testContainsContention(Map<K, V[]> contender, Map<K, V> value) {
        return value == null
                || (contender != null && value.keySet()
                .stream()
                .allMatch(k -> contender.containsKey(k) && contender.get(k)[0]
                        .equals(value.get(k))));
    }

    /**
     * check if the entire value map is entirely contained in the contender map's 0th keys
     *
     * @param contender contender map (Eg. the parameter map)
     * @param value     the value map
     * @param <K>       key types
     * @param <V>       value types
     * @return true if value is null or if all key-value pairs of value are entirely contained in the contender map
     */
    public static <K, V> boolean testContainsMap(Map<K, V> contender, Map<K, V> value) {
        return value == null
                || (contender != null && value.keySet()
                .stream()
                .allMatch(k -> contender.containsKey(k) && contender.get(k)
                        .equals(value.get(k))));
    }

    /**
     * check if the entire value map is entirely contained in the contender map's 0th keys
     *
     * @param contender contender map (Eg. the parameter map)
     * @param value     the value map
     * @param <K>       key types
     * @param <V>       value types
     * @return true if value is null or if all key-value pairs of value are entirely contained in the contender map
     */
    public static <K, V> boolean testContainsContention(Map<K, V> contender, List<K> value) {
        return value == null || (contender != null && value.stream().allMatch(contender::containsKey));
    }

    /**
     * check if contender id not null
     *
     * @param contender contender
     * @param <T>       type of contender
     * @return true if not null
     */
    public static <T> boolean notNull(T contender) {
        return contender != null;
    }

    /**
     * @param value     actual value
     * @param contender contender to be compared against
     * @return return true (not applicable) if contender is off (negative), or the value is greater than contender
     */
    public static boolean checkIfGreaterThan(Number value, Number contender) {
        return contender == null || value.doubleValue() > contender.doubleValue();
    }

    /**
     * @param value     actual value
     * @param contender contender to be compared against
     * @return return true (not applicable) if contender is off (negative), or the value is lesser than contender
     */
    public static boolean checkIfLesserThan(Number value, Number contender) {
        return contender == null || value.doubleValue() < contender.doubleValue();
    }

    /**
     * @param supplier value supplier
     * @return return true if supplier returns true, else false
     */
    public static boolean checkIfApplicable(BooleanSupplier supplier) {
        return supplier == null || supplier.getAsBoolean();
    }

    /**
     * @param biFunction function
     * @param t1         value
     * @param t2         contender
     * @param <T>        type of value
     * @return true if function is null OR if contender (t2) is null, or if function returns true
     */
    public static <T> boolean checkIfApplicable(BiPredicate<T, T> biFunction, T t1, T t2) {
        return biFunction == null || t2 == null || biFunction.test(t1, t2);
    }

    /**
     * @param value     value
     * @param contender contender
     * @return true if contender is null or if value and contender are same
     */
    public static boolean testBooleanEquality(Boolean value, Boolean contender) {
        return contender == null || Objects.equals(value, contender);
    }
}
