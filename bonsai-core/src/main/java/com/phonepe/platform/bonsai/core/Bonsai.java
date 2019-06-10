package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.data.KnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.core.vital.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.core.vital.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.query.dsl.Filter;

import java.util.List;
import java.util.Map;

/**
 * This is a multi level Forest implementation.
 * This FOREST contains TREEs which consists of {@link Knot}s and {@link Edge}s
 * Every {@link Knot} in the tree is associated with a <String> Key
 * Knots contain {@link com.phonepe.platform.bonsai.core.structures.OrderedList} of edges, with some priority.
 * Edges contain a {@link Knot}
 * The {@link Knot}s can, in turn, contain {@link KnotData}
 * KnotData can then point to:
 * - Some form of value {@link com.phonepe.platform.bonsai.core.data.ValuedKnotData}
 * - List of keys , which could point to {@link Knot}s in the Bonsai Tree {@link com.phonepe.platform.bonsai.core.data.MultiKnotData}
 * - bunch of mapped keys , which could point to {@link Knot}s in the Bonsai Tree {@link com.phonepe.platform.bonsai.core.data.MapKnotData}
 *
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:54 AM
 */
public interface Bonsai<C extends Context> {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  KNOT OPERATIONS  //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * create a {@link Knot} out of the {@link KnotData}
     *
     * @param knotData some kind of data knot
     * @return created Knot with some id, version, etc
     */
    Knot createKnot(KnotData knotData);

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
     * @param knotId   knotId of the knot
     * @param knotData new data
     * @return older knot if some other data was present (on the knotId)
     */
    Knot updateKnotData(String knotId, KnotData knotData);

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
     * @throws BonsaiError 1. if there are cycles {@link com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode#CYCLE_DETECTED}
     *                     2. if the edge's pivot is violated at that level {@link com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode#VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR}
     */
    Edge addVariation(String knotId, Variation variation);

    /**
     * update the edge with a new set of filters
     * needs to be a valid update id
     *
     * @param knotId  knot id to which the edge is associated with
     * @param edgeId  edge id to be updated
     * @param filters filters set
     * @return false if edge id wasnt present, or if edge wasnt updated
     */
    Edge updateEdgeFilters(String knotId, String edgeId, List<Filter> filters);

    /**
     * add a bunch of filters to an edge.
     * this method will append to the existing list of filters that already exist
     *
     * @param edgeId  edge id to which filters will be added to
     * @param filters filters to be added
     * @return false if edge wasnt updated
     */
    Edge addEdgeFilters(String edgeId, List<Filter> filters);

    /**
     * remove the edge only (all {@link Knot}s that the {@link Edge} contains, will continue to exist, but will be disconnected from the main tree
     *
     * @param edgeId edge id to be removed
     * @return true if it was successfully removed
     */
    boolean unlinkVariation(String knotId, String edgeId);

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
     * @param key      key to associate the Knot with
     * @param knotData data with which the Knot will be created
     * @return created knot
     * @throws BonsaiError if there are som sort of cycles while creating the knot
     */
    Knot createMapping(String key, KnotData knotData);

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
     * Perform a full evaluation of the Key
     * Returns the full Tree of Keyed Nodes, after evaluation
     * The following will be done:
     * 1. get the Node for the key
     * 2. recursively traverse the Node, along the matching edges. Match of the edge, will be based on the condition.
     * 3. also, follow the keys for {@link com.phonepe.platform.bonsai.core.data.MapKnotData}, {@link com.phonepe.platform.bonsai.core.data.MultiKnotData}
     *
     * @param key     key to start evaluation
     * @param context context to be evaluated against
     * @return {@link KeyNode} after evaluation
     */
    KeyNode evaluate(String key, C context);
}
