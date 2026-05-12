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

package com.phonepe.commons.bonsai.core.vital.random.impl;

import com.phonepe.commons.bonsai.core.vital.random.RandomIdProvider;

import java.util.UUID;

/**
 * Cryptographically secure UUID provider using {@link UUID#randomUUID()},
 * which internally delegates to {@link java.security.SecureRandom}.
 * Slower than {@link ThreadLocalRandomIdProvider} but suitable when
 * unpredictability of generated IDs is important.
 */
public class SecureRandomIdProvider implements RandomIdProvider {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}

