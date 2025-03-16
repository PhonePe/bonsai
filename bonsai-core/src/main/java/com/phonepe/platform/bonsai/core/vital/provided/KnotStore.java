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

package com.phonepe.platform.bonsai.core.vital.provided;

import com.phonepe.platform.bonsai.models.blocks.Knot;

/**
 * A store for knots
 *
 * @param <I> id
 * @param <K> knot
 */
public interface KnotStore<I, K> {
    /**
     * check if id is mapped to a knot
     *
     * @param id id
     * @return true if knot exists
     */
    boolean containsKnot(I id);

    /**
     * map an id to a knot
     *
     * @param id   id to be mapped to the knot
     * @param knot knot being mapped
     * @return the previous value associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    Knot mapKnot(I id, K knot);

    /**
     * get the mapped knot
     *
     * @param id id to be fetched
     * @return knot mapped to it
     */
    K getKnot(I id);

    /**
     * remove the knot associated with id
     *
     * @param i id
     * @return the previous value associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    K deleteKnot(I i);
}
