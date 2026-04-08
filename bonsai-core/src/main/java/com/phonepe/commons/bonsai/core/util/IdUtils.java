package com.phonepe.commons.bonsai.core.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdUtils {
    public String generateUUD() {
        return UUID.randomUUID().toString();
    }

    @SuppressWarnings("java:S2245")
    public String generateFastUUID() {
        return new UUID(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong()).toString();
    }
}
