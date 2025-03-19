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

package com.phonepe.commons.bonsai.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractNodeVisitor<T> implements NodeVisitor<T> {
    private final T defaultValue;

    public AbstractNodeVisitor() {
        this(null);
    }

    public AbstractNodeVisitor(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T visit(ListNode listNode) {
        return defaultValue;
    }

    @Override
    public T visit(ValueNode valueNode) {
        return defaultValue;
    }

    @Override
    public T visit(MapNode mapNode) {
        return defaultValue;
    }
}
