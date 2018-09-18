package com.phonepe.platform.bonsai.core;

import java.time.Duration;
import java.time.Instant;

/**
 * @author tushar.naik
 * @version 1.0  21/04/17 - 11:15 PM
 */
public class PerformanceEvaluator {

    /**
     * evaluate the time consumed by a runnable to perform x operations
     *
     * @param numOperations number of times the runnable is to be executed
     * @param runnable      runnable to be executed
     * @return time the runnable takes to run numOperations times
     */
    public static long evaluate(long numOperations, Runnable runnable) {
        Instant start = Instant.now();
        for (long i = 0; i < numOperations; i++) {
            printStatus(numOperations, i);
            runnable.run();
        }
        System.out.println();
        return getElapsedDuration(start).toMillis();
    }

    /**
     * evaluate the average time consumed by a runnable to perform x operations
     *
     * @param numOperations number of times the runnable is to be executed
     * @param runnable      runnable to be executed
     * @return average time the runnable takes to run numOperations times
     */
    public static float evaluateAndAvg(long numOperations, Runnable runnable) {
        return evaluate(numOperations, runnable) / (float) numOperations;
    }

    private static void printStatus(long numOperations, long iteration) {
        if (numOperations < 100) {
            final long i1 = (100 / numOperations);
            for (long j = 0; j < i1; j++) {
                System.out.print("#");
            }
        } else {
            final long i1 = (numOperations / 100);
            if (iteration % i1 == 0) {
                System.out.print("#");
            }
        }
    }

    /**
     * get elapsed duration between an older instant and now
     *
     * @param instant older instant
     * @return duration
     */
    public static Duration getElapsedDuration(Instant instant) {
        return Duration.between(instant, Instant.now());
    }
}
