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

package com.phonepe.platform.bonsai.core.exception;

public enum BonsaiErrorCode {
    INTERNAL_SERVER_ERROR,
    CYCLE_DETECTED,
    VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR,
    INVALID_INPUT,
    INVALID_STATE,
    UNSUPPORTED_OPERATION,
    KNOT_ABSENT,
    EDGE_ABSENT,
    MAPPING_ALREADY_PRESENT,
    KNOT_RESOLUTION_ERROR,
    TREE_ALREADY_EXIST,
    TREE_DOES_NOT_EXIST
}
