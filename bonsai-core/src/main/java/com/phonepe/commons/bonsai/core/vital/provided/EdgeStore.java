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

package com.phonepe.commons.bonsai.core.vital.provided;

import java.util.List;
import java.util.Map;

/**
 * A store for edges
 * All operations related to edges will reside here
 *
 * @param <I> id
 * @param <E> edge (could be a reference too
 */
public interface EdgeStore<I, E> {

    /**
     * checks if id is present
     *
     * @param id key
     * @return true if present
     */
    boolean containsEdge(I id);

    /**
     * associate an edge with the id
     *
     * @param i    id
     * @param edge edge to be associated
     * @return the previous value associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    E mapEdge(I i, E edge);

    /**
     * @param i id
     * @return edge that is mapped to the id
     */
    E getEdge(I i);

    /**
     * delete the edge that is mapped to the id
     *
     * @param i id
     * @return the previous edge associated with <tt>id</tt>, or <tt>null</tt> if there was no mapping for <tt>id</tt>.
     */
    E deleteEdge(I i);

    /**
     * given a list of ids, fetch all the edges
     *
     * @param i ids
     * @return map of ids to corresponding edges (it returns a linkedHashMap, to maintain the order)
     */
    Map<I, E> getAllEdges(List<I> i);
}
