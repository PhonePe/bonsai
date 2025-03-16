package com.phonepe.platform.bonsai.conditions;

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

