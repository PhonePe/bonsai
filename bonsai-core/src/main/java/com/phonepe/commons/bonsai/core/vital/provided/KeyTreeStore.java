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

/**
 * A store for Trees or Tree pointers
 * In Bonsai, this represents keys that actually map to full trees.
 * So basically, all keys here, would ideally point to Knots and edges
 *
 * @param <K> Key for association
 * @param <T> Some form that represents a Tree or pointers to the Tree
 */
public interface KeyTreeStore<K, T> {
    /**
     * checks if k is present
     *
     * @param k key
     * @return true if present
     */
    boolean containsKey(K k);

    /**
     * to create association between a key
     *
     * @param k key
     * @param t tree to map the key with
     * @return the previous tree associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    T createKeyTree(K k, T t);

    /**
     * @param k key
     * @return tree or ref of tree that is mapped to the key
     */
    T getKeyTree(K k);

    /**
     * remove KeyTree
     *
     * @param k key
     * @return the previous tree associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    T removeKeyTree(K k);
}
