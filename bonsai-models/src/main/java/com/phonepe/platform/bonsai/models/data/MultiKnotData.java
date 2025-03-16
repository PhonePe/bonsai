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

package com.phonepe.platform.bonsai.models.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultiKnotData extends KnotData {
    private List<String> keys;

    public MultiKnotData() {
        super(KnotDataType.MULTI_KNOT);
    }

    @Builder
    public MultiKnotData(@Singular List<String> keys) {
        super(KnotDataType.MULTI_KNOT);
        this.keys = keys;
    }

    @Override
    public <T> T accept(KnotDataVisitor<T> knotDataVisitor) {
        return knotDataVisitor.visit(this);
    }
}
