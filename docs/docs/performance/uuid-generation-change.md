# UUID Generation Change in `BonsaiTree.setMDCContext()`

## Overview

A change was made to how the evaluation correlation ID (MDC trace ID) is generated inside
`BonsaiTree.setMDCContext()`. The previous implementation used `UUID.randomUUID()`, which
relies on `SecureRandom`. The new implementation uses `ThreadLocalRandom` to construct a UUID.

---

## The Change

### Before
```java
requestId = UUID.randomUUID().toString();
```

### After
```java
ThreadLocalRandom random = ThreadLocalRandom.current();
requestId = new UUID(random.nextLong(), random.nextLong()).toString();
```

---

## Context

`setMDCContext()` is called at the start of every `BonsaiTree.evaluate()` invocation. Its sole
purpose is to set a short-lived correlation ID (`BONSAI-EVAL-ID`) in SLF4J's MDC so that all log
lines emitted during a single evaluation (including recursive calls for `MultiKnotData` and
`MapKnotData`) share the same trace ID.

The ID is:
- **Set** at the entry of the top-level `evaluate()` call (only if one is not already present).
- **Read** explicitly via `MDC.get(BonsaiConstants.EVALUATION_ID)` in `NodeUtils` and `AbstractValueVisitor` for structured log arguments.
- **Read** implicitly via `context.id()` which delegates to `MDC.get(BonsaiConstants.EVALUATION_ID)`.
- **Removed** in the `finally` block, scoped strictly to one evaluation.

---

## Pros of the Change

| # | Benefit | Detail |
|---|---------|--------|
| 1 | **Significantly faster** | `ThreadLocalRandom` avoids the overhead of `SecureRandom`'s entropy gathering (see benchmarks below). |
| 2 | **No thread contention** | `SecureRandom` uses an internal lock for its shared seed state. `ThreadLocalRandom` is per-thread by design, eliminating contention under concurrent load. |
| 3 | **Reduced latency spikes** | On high-throughput services, `SecureRandom` can block waiting for OS entropy, causing latency spikes. `ThreadLocalRandom` has no such dependency. |
| 4 | **No behavioural change** | The ID is used purely as a diagnostic trace ID — the change is functionally transparent. |

---

## Cons / Trade-offs

| # | Trade-off | Detail |
|---|-----------|--------|
| 1 | **Not cryptographically secure** | `ThreadLocalRandom` is not suitable for security-sensitive contexts (tokens, secrets, session IDs). However, this ID is only a log correlation tag, so this is acceptable. |
| 2 | **Non-standard UUID variant/version** | `UUID.randomUUID()` sets RFC 4122 version 4 and variant bits. `new UUID(msb, lsb)` does **not** set these bits, so the generated value is not a strictly conformant UUID v4. |
| 3 | **Slightly lower uniqueness guarantees** | `ThreadLocalRandom` is a pseudo-random number generator (PRNG) seeded per thread, not a CSPRNG. For a trace ID that lives for the duration of a single request, collision probability remains negligible in practice. |

---

## Things to Be Aware Of

1. **Do not reuse this pattern for security-sensitive IDs.** Any token, session key, or identifier
   used for authentication/authorisation must continue to use `SecureRandom` or `UUID.randomUUID()`.

2. **The UUID is not RFC 4122 v4 compliant.** If any downstream system (logging aggregator,
   tracing infrastructure, etc.) validates or parses the UUID version/variant bits, it may behave
   unexpectedly. Verify that your log pipelines treat this value as an opaque string.

3. **Virtual threads (Project Loom).** `ThreadLocalRandom` works correctly with Java virtual
   threads — each virtual thread gets its own instance — so there are no concerns if the codebase
   adopts virtual threads in future.

4. **The MDC value is never printed via `%X{}` pattern.** The `log4j.properties` pattern does not
   include `%X{BONSAI-EVAL-ID}`. The value is surfaced only by explicit `MDC.get(...)` calls in
   `NodeUtils` and `AbstractValueVisitor`. If you want it in every log line automatically, add
   `%X{BONSAI-EVAL-ID}` to the appender conversion pattern.

---

## Benchmark Results

Tests were run in [`UUIDGenerationPerformanceTest`](../../bonsai-core/src/test/java/com/phonepe/commons/bonsai/core/vital/UUIDGenerationPerformanceTest.java).

### Single-Threaded (1,000,000 iterations)

| Implementation         | Total Time | Avg per op |
|------------------------|------------|------------|
| `UUID.randomUUID()`    | 151 ms     | 151 ns/op  |
| `ThreadLocalRandom`    | 10 ms      | 10 ns/op   |
| **Speedup**            | —          | **~15x faster** |

### Multi-Threaded (10 threads × 100,000 iterations)

| Implementation         | Total Thread-Time |
|------------------------|-------------------|
| `UUID.randomUUID()`    | 3556 ms           |
| `ThreadLocalRandom`    | 552 ms            |
| **Speedup**            | **~6.4x faster**  |

The multi-threaded speedup (6.4x) being lower than the single-threaded speedup (15x) is itself
revealing — as thread count increases, `SecureRandom` suffers from lock contention, making the
real-world gain even more significant at production concurrency levels.

---

## Caveats in the Benchmark

The test results above are indicative but should be interpreted with the following limitations in mind:

1. **No JMH (Java Microbenchmark Harness).** The tests use `System.nanoTime()` directly. Without JMH,
   results are susceptible to JIT compilation warm-up effects, GC pauses, and OS scheduling noise.
   The warmup loop (10,000 iterations) partially mitigates JIT effects but does not eliminate them.

2. **Synthetic workload.** The benchmark only measures UUID generation + `.toString()` in a tight
   loop. In production, `setMDCContext()` is called once per `evaluate()` invocation, and the UUID
   generation cost is dwarfed by the actual tree traversal logic. The relative improvement holds,
   but the absolute impact on end-to-end latency will be much smaller.

3. **Single JVM run.** Results vary across JVM versions, hardware, and OS entropy pool state.
   On Linux with low entropy (e.g., a container without `/dev/random` seeded well), `UUID.randomUUID()`
   can be significantly slower than measured here.

4. **The `println` inside the benchmark loop.** The current test file contains a `System.out.println`
   inside `compareUUIDGenerationPerformance()` that fires between the two benchmarks. This introduces
   a minor inconsistency in timing isolation (I/O flush between runs). It should be removed for
   stricter measurements.

5. **Thread scheduling variance.** The multi-threaded test sums per-thread wall-clock time, not
   total elapsed wall-clock time. On a machine with fewer cores than threads, results will reflect
   OS time-slicing rather than pure contention on `SecureRandom`.

---

## Recommendation

The change is **safe and beneficial** for its intended use case. For production confidence, consider:

- Running the benchmark with [JMH](https://github.com/openjdk/jmh) for statistically sound numbers.
- Confirming that log aggregation pipelines treat `BONSAI-EVAL-ID` as an opaque string.
- Removing the stray `System.out.println` from `UUIDGenerationPerformanceTest.compareUUIDGenerationPerformance()`.

