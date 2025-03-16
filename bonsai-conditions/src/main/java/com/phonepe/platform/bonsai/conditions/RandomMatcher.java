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

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

/**
 * A BooleanUniMatcher, which uses random number generation to match x% of the calls with equal probability
 */
@NoArgsConstructor
@Setter
public class RandomMatcher implements Matcher.BooleanUniMatcher<Number> {

    /**
     * defaults of 0 to 100 (for percentage use-cases, set a different value for custom scenarios)
     */
    protected long lowerBound = 0;
    protected long higherBound = 100;
    protected Random random = new Random(System.currentTimeMillis());

    public RandomMatcher(long lowerBound, long higherBound) {
        this.lowerBound = lowerBound;
        this.higherBound = higherBound;
    }

    /**
     * check if the given value matches the percentage criteria
     * Eg: to achieve an approximate match of 10%:
     * if value passed it 10,
     * this method will return true for 10% of the calls
     * and false for 90% of the calls with random probability
     *
     * @param value threshold
     * @return if
     */
    @Override
    public Boolean match(Number value) {
        final long randomNumber = Math.abs(random.nextInt((int) ((higherBound - lowerBound) + lowerBound)));
        return randomNumber < value.longValue();
    }

}
