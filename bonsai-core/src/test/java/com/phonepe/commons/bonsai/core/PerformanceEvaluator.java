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

package com.phonepe.commons.bonsai.core;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class PerformanceEvaluator {

    @Getter
    private final Timer timer;

    private final ConsoleReporter reporter;

    public PerformanceEvaluator() {
        this("perf-eval");
    }

    public PerformanceEvaluator(String name) {
        MetricRegistry metricRegistry = new MetricRegistry();
        this.timer = metricRegistry.timer(name);
        this.reporter = ConsoleReporter.forRegistry(metricRegistry).convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS).build();
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
     * evaluate the time consumed by a runnable to perform x operations
     *
     * @param numOperations number of times the runnable is to be executed
     * @param runnable      runnable to be executed
     * @return time the runnable takes to run numOperations times
     */
    public Timer evaluate(long numOperations, Runnable runnable) {
        for (long i = 0; i < numOperations; i++) {
            printStatus(numOperations, i);
            try {
                timer.time(() -> {
                    runnable.run();
                    return true;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        reporter.report();
        return timer;
    }
}
