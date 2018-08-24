package com.phonepe.platform.bonsai.core.vital;

import java.util.UUID;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 3:49 PM
 */
public interface BonsaiIdGen {
    static String newId() {
        return UUID.randomUUID().toString();
    }
}
