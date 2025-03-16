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

package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;

import java.util.Map;

public class ImmutableBonsaiBuilder<C extends Context> {
    private final Bonsai<C> bonsai;

    public ImmutableBonsaiBuilder(final Bonsai<C> bonsai) {
        this.bonsai = bonsai;
    }

    public static <C extends Context> ImmutableBonsaiBuilder<C> builder(final Bonsai<C> bonsai) {
        return new ImmutableBonsaiBuilder<>(bonsai);
    }

    public ImmutableBonsaiBuilder<C> createKnot(final KnotData knotData, final Map<String, Object> properties) {
        bonsai.createKnot(knotData, properties);
        return this;
    }

    public Knot createKnotAndCapture(final KnotData knotData, final Map<String, Object> properties) {
        return bonsai.createKnot(knotData, properties);
    }

    public ImmutableBonsaiBuilder<C> createKnot(final Knot knot) {
        bonsai.createKnot(knot);
        return this;
    }

    public Knot createKnotAndCapture(final Knot knot) {
        return bonsai.createKnot(knot);
    }

    public ImmutableBonsaiBuilder<C> updateKnotData(final String id, final KnotData knotData,
                                                    final Map<String, Object> properties) {
        bonsai.updateKnotData(id, knotData, properties);
        return this;
    }

    public ImmutableBonsaiBuilder<C> deleteKnot(final String id, final boolean recursive) {
        bonsai.deleteKnot(id, recursive);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createEdge(final Edge edge) {
        bonsai.createEdge(edge);
        return this;
    }

    public ImmutableBonsaiBuilder<C> addVariation(final String knotId, final Variation variation) {
        bonsai.addVariation(knotId, variation);
        return this;
    }

    public Edge addVariationAndCapture(final String knotId, final Variation variation) {
        return bonsai.addVariation(knotId, variation);
    }

    public ImmutableBonsaiBuilder<C> updateVariation(final String knotId, final String edgeId,
                                                     final Variation variation) {
        bonsai.updateVariation(knotId, edgeId, variation);
        return this;
    }

    public ImmutableBonsaiBuilder<C> unlinkVariation(final String knotId, final String edgeId) {
        bonsai.unlinkVariation(knotId, edgeId);
        return this;
    }

    public ImmutableBonsaiBuilder<C> deleteVariation(final String knotId, final String edgeId,
                                                     final boolean recursive) {
        bonsai.deleteVariation(knotId, edgeId, recursive);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createMapping(final String key, final String knotId) {
        bonsai.createMapping(key, knotId);
        return this;
    }

    public ImmutableBonsaiBuilder<C> createMapping(final String key, final KnotData knotData,
                                                   Map<String, Object> properties) {
        bonsai.createMapping(key, knotData, properties);
        return this;
    }

    public Knot createMappingAndCapture(final String key, final KnotData knotData, Map<String, Object> properties) {
        return bonsai.createMapping(key, knotData, properties);
    }

    public ImmutableBonsaiBuilder<C> removeMapping(final String key) {
        bonsai.removeMapping(key);
        return this;
    }

    public ImmutableBonsaiBuilder<C> addTree(final TreeKnot treeKnot) {
        bonsai.createCompleteTree(treeKnot);
        return this;
    }

    public Bonsai<C> build() {
        return new ImmutableBonsaiTree<>(bonsai);
    }

}
