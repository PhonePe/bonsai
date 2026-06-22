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

/**
 * DecimalRandomMatcher is a matcher that matches a given value with a random number generated between the lowerBound and
 * higherBound. The random number is generated using the Random class. The random number is then compared with the given
 * value to determine if the value matches the random number.
 */
@NoArgsConstructor
public class DecimalRandomMatcher extends RandomMatcher {

    public DecimalRandomMatcher(long lowerBound, long higherBound) {
        super(lowerBound, higherBound);
    }

    @Override
    public Boolean match(Number value) {
        final int factor = 100;
        final long h = higherBound * factor;
        final long l = lowerBound * factor;
        final long randomNumber = Math.abs(random.nextInt((int) ((h - l) + l)));
        return randomNumber < (value.floatValue() * factor);
    }
}

