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

package com.phonepe.commons.bonsai.core.vital;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Performance comparison between UUID.randomUUID() (SecureRandom-based)
 * and ThreadLocalRandom-based UUID generation, as used in {@link BonsaiTree#setMDCContext()}.
 */
@Disabled("Performance benchmark — not intended to run in CI.")
class UUIDGenerationPerformanceTest {

    private static final int ITERATIONS = 1_000_000;

    @Test
    void compareUUIDGenerationPerformance() {
        // Warmup
        for (int i = 0; i < 10_000; i++) {
            UUID.randomUUID();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            new UUID(random.nextLong(), random.nextLong());
        }

        // Benchmark: UUID.randomUUID() — SecureRandom based
        long start1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            UUID.randomUUID().toString();
        }
        long duration1 = System.nanoTime() - start1;

        // Benchmark: ThreadLocalRandom based UUID (current implementation)
        long start2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            new UUID(random.nextLong(), random.nextLong()).toString();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        System.out.println(new UUID(random.nextLong(), random.nextLong()).toString());
        long duration2 = System.nanoTime() - start2;

        long avgNs1 = duration1 / ITERATIONS;
        long avgNs2 = duration2 / ITERATIONS;

        System.out.println("=== Single-Threaded UUID Generation Performance ===");
        System.out.printf("UUID.randomUUID()       : total=%dms, avg=%dns/op%n",
                duration1 / 1_000_000, avgNs1);
        System.out.printf("ThreadLocalRandom UUID  : total=%dms, avg=%dns/op%n",
                duration2 / 1_000_000, avgNs2);
        System.out.printf("Speedup (ThreadLocal vs SecureRandom): %.2fx faster%n",
                (double) duration1 / duration2);
    }

    @Test
    void compareUUIDGenerationPerformanceMultiThreaded() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 100_000;

        // --- UUID.randomUUID() ---
        long[] times1 = new long[threadCount];
        Thread[] threads1 = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int idx = t;
            threads1[t] = new Thread(() -> {
                long start = System.nanoTime();
                for (int i = 0; i < iterationsPerThread; i++) {
                    UUID.randomUUID().toString();
                }
                times1[idx] = System.nanoTime() - start;
            });
        }
        for (Thread thread : threads1) thread.start();
        for (Thread thread : threads1) thread.join();

        // --- ThreadLocalRandom UUID (current implementation) ---
        long[] times2 = new long[threadCount];
        Thread[] threads2 = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int idx = t;
            threads2[t] = new Thread(() -> {
                long start = System.nanoTime();
                for (int i = 0; i < iterationsPerThread; i++) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    new UUID(random.nextLong(), random.nextLong()).toString();
                }
                times2[idx] = System.nanoTime() - start;
            });
        }
        for (Thread thread : threads2) thread.start();
        for (Thread thread : threads2) thread.join();

        long totalMs1 = 0, totalMs2 = 0;
        for (int t = 0; t < threadCount; t++) {
            totalMs1 += times1[t] / 1_000_000;
            totalMs2 += times2[t] / 1_000_000;
        }

        System.out.println("=== Multi-Threaded UUID Generation Performance ===");
        System.out.printf("Threads: %d, Iterations/thread: %d%n", threadCount, iterationsPerThread);
        System.out.printf("UUID.randomUUID()       : total thread-time=%dms%n", totalMs1);
        System.out.printf("ThreadLocalRandom UUID  : total thread-time=%dms%n", totalMs2);
        System.out.printf("Speedup (ThreadLocal vs SecureRandom): %.2fx faster%n",
                (double) totalMs1 / totalMs2);
    }
}

