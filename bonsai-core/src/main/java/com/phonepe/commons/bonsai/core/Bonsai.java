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

package com.phonepe.commons.bonsai.core;

import com.phonepe.commons.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.commons.bonsai.models.data.MapKnotData;
import com.phonepe.commons.bonsai.models.data.MultiKnotData;
import com.phonepe.commons.bonsai.models.data.ValuedKnotData;
import com.phonepe.commons.bonsai.core.exception.BonsaiError;
import com.phonepe.commons.bonsai.core.vital.Context;
import com.phonepe.commons.bonsai.models.KeyNode;
import com.phonepe.commons.bonsai.models.TreeKnotState;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.blocks.Variation;
import com.phonepe.commons.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.commons.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.commons.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.commons.bonsai.models.data.KnotData;
import com.phonepe.commons.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.commons.bonsai.models.structures.OrderedList;

import java.util.List;
import java.util.Map;

/**
 * This is a multi level Forest implementation.
 * This FOREST contains TREEs which consists of {@link Knot}s and {@link Edge}s
 * Every {@link Knot} in the tree is associated with a <String> Key
 * Knots contain {@link OrderedList} of edges, with some priority.
 * Edges contain a {@link Knot}
 * The {@link Knot}s can, in turn, contain {@link KnotData}
 * KnotData can then point to:
 * - Some form of value {@link ValuedKnotData}
 * - List of keys , which could point to {@link Knot}s in the Bonsai Tree {@link MultiKnotData}
 * - bunch of mapped keys , which could point to {@link Knot}s in the Bonsai Tree {@link MapKnotData}
 */
public interface Bonsai<C extends Context> {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  KNOT OPERATIONS  //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Function to check if the given knotId is present or not.
     *
     * @param knotId - Id of Knot.
     * @return boolean response.
     */
    boolean containsKnot(String knotId);

    /**
     * create a {@link Knot} out of the {@link KnotData}
     *
     * @param knotData   some kind of data knot
     * @param properties updated properties
     * @return created Knot with some id, version, etc
     */
    Knot createKnot(KnotData knotData, Map<String, Object> properties);

    /**
     * create a Knot directly
     *
     * @param knot knot
     * @return older knot if some other data was present (on the knotId)
     */
    Knot createKnot(Knot knot);

    /**
     * get the knot for an id
     *
     * @param knotId id to be retrieved
     * @return knot with id
     */
    Knot getKnot(String knotId);

    /**
     * update the data associated with a {@link Knot}
     *
     * @param knotId     knotId of the knot
     * @param knotData   new data
     * @param properties updated properties
     * @return older knot if some other data was present (on the knotId)
     */
    Knot updateKnotData(String knotId, KnotData knotData, Map<String, Object> properties);

    /**
     * delete the knot and its associated edges
     *
     * @param knotId    knotId of the knot to be deleted
     * @param recursive true if you want to delete all related {@link Edge}s and {@link Knot}s below it
     * @return the list of knots that were deleted
     */
    TreeKnot deleteKnot(String knotId, boolean recursive);


    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////  EDGE OPERATIONS  /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Function to check if the given edgeId is present or not.
     *
     * @param edgeId - Id of Edge.
     * @return boolean response.
     */
    boolean containsEdge(String edgeId);

    /**
     * create edge directly
     *
     * @param edge edge
     * @return edge if an older mapping existed
     */
    Edge createEdge(Edge edge);

    /**
     * @param knotId    id of the {@link Knot}
     * @param variation variation of a {@link Knot}. An edge to connect to the {@link Knot} with another variation of the Knot
     * @return edgeId
     * @throws BonsaiError 1. if there are cycl`es {@link BonsaiErrorCode#CYCLE_DETECTED}
     *                     2. if the edge's pivot is violated at that level {@link BonsaiErrorCode#VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR}
     */
    Edge addVariation(String knotId, Variation variation);

    /**
     * update the edge with new Variation components
     * needs to be a valid update id
     *
     * @param knotId    knot id to which the edge is associated with
     * @param edgeId    edge id to be updated
     * @param variation updatedVariation to be added
     * @return false if edge id wasn't present, or if edge wasn't updated
     */
    Edge updateVariation(String knotId, String edgeId, Variation variation);

    /**
     * remove the edge only (all {@link Knot}s that the {@link Edge} contains, will continue to exist, but will be disconnected from the main tree
     *
     * @param edgeId edge id to be removed
     */
    void unlinkVariation(String knotId, String edgeId);

    /**
     * delete the variation that is mapped to a knot
     *
     * @param knotId    knot id
     * @param edgeId    edge id
     * @param recursive if all {@link Knot}s and {@link Edge}s connected to this Edge, are supposed to be deleted recursively
     * @return list of knots that were removed
     */
    TreeEdge deleteVariation(String knotId, String edgeId, boolean recursive);

    /**
     * return the edge that matched the id
     *
     * @param edgeId id
     * @return edge
     */
    Edge getEdge(String edgeId);

    /**
     * return all edges (in the same order)
     *
     * @param edgeIds edge ids
     * @return mapping of id to edge
     */
    Map<String, Edge> getAllEdges(List<String> edgeIds);


    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  TREE OPERATIONS  //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Function to check if the given key is present or not.
     *
     * @param key - key of a tree.
     * @return boolean response.
     */
    boolean containsKey(String key);

    /**
     * map a key with a Knot
     *
     * @param key    key to associate the Knot with
     * @param knotId if of the Knot to be associated with the key
     * @return knot that was mapped
     * @throws BonsaiError if there are some sort of cycles
     */
    Knot createMapping(String key, String knotId);

    /**
     * create create a Knot out of the knot data, and then create a mapping between the key and the newly created knot
     *
     * @param key        key to associate the Knot with
     * @param knotData   data with which the Knot will be created
     * @param properties updated properties
     * @return created knot
     * @throws BonsaiError if there are som sort of cycles while creating the knot
     */
    Knot createMapping(String key, KnotData knotData, Map<String, Object> properties);

    /**
     * @param key key
     * @return knot id
     */
    String getMapping(String key);

    /**
     * remove the mapping between the key and the corresponding knot
     *
     * @param key key to be unmapped
     * @return knot that was unmapped
     */
    Knot removeMapping(String key);

    /**
     * get the complete tree recursively)
     * use this only for debugging purposes
     *
     * @param key key with mapping
     * @return TreeKnot
     */
    TreeKnot getCompleteTree(String key);

    /**
     * recursive function that creates the full tree (if not already present in the datastores) using the TreeKnot
     * (missing nodes and edges will be deleted
     *
     * @param treeKnot tree knot
     * @return root knot
     */
    Knot createCompleteTree(TreeKnot treeKnot);

    /**
     * This function is to validate and get the complete tree (including the pending updates)
     * corresponding to given key and corresponding List of Deltas.
     *
     * @param key                - Name of the root node.
     * @param deltaOperationList - List of Delta Operations.
     * @return TreeKnotState - object contains TreeKnot and revert-delta-operations.
     */
    TreeKnotState getCompleteTreeWithDeltaOperations(String key, List<DeltaOperation> deltaOperationList);

    /**
     * This interface allows you to make changes to the tree as a list of operations
     * and corresponding List of Deltas.
     *
     * @param key                - Name of the root node.
     * @param deltaOperationList - List of Delta Data.
     * @return TreeKnotState - object contains TreeKnot and revert-delta-operations.
     */
    TreeKnotState applyDeltaOperations(String key, List<DeltaOperation> deltaOperationList);

    /**
     * This utility interface to calculate the delta operations required to create the key recursively
     *
     * @param key - key for which delta operations are to be calculated.
     * @return List of Delta Operations or empty list if the key does not exist
     */
    List<DeltaOperation> calculateDeltaOperations(String key);

    /**
     * Perform a full evaluation of the Key
     * Returns the full Tree of Keyed Nodes, after evaluation
     * The following will be done:
     * 1. get the Node for the key
     * 2. recursively traverse the Node, along the matching edges. Match of the edge, will be based on the condition.
     * 3. also, follow the keys for {@link MapKnotData}, {@link MultiKnotData}
     *
     * @param key     key to start evaluation
     * @param context context to be evaluated against
     * @return {@link KeyNode} after evaluation
     */
    KeyNode evaluate(String key, C context);

    FlatTreeRepresentation evaluateFlat(String key, C context);
}
