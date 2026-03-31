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

import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * DecimalRandomMatcher is a matcher that matches a given value with a random number generated between the lowerBound and
 * higherBound. The random number is generated using the Random class. The random number is then compared with the given
 * value to determine if the value matches the random number.
 */
@NoArgsConstructor
public class DecimalRandomMatcher extends RandomMatcher {

    private static final int FACTOR = 100;

    public DecimalRandomMatcher(long lowerBound, long higherBound) {
        super(lowerBound, higherBound);
    }

    @Override
    @SuppressWarnings("java:S2245") // We are using ThreadLocalRandom which is a better alternative to SecureRandom in multi-threaded environments
    public Boolean match(Number value) {
        final long h = higherBound * FACTOR;
        final long l = lowerBound * FACTOR;
        final long randomNumber = Math.abs(ThreadLocalRandom.current().nextLong(l, h));
        return randomNumber < (value.floatValue() * FACTOR);
    }
}

