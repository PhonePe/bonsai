package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.vital.*;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicKnot;
import com.phonepe.platform.bonsai.models.KeyNode;

import java.util.List;

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
public interface Bonsai {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  KNOT OPERATIONS  //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * create a {@link Knot} out of the {@link KnotData}
     *
     * @param knotData some kind of data knot
     * @return created Knot with some id, version, etc
     */
    AtomicKnot createKnot(KnotData knotData);

    AtomicKnot getKnot(String id);

    /**
     * update the data associated with a {@link Knot}
     *
     * @param id       id of the knot
     * @param knotData new data
     * @return true if updated
     */
    boolean updateKnotData(String id, KnotData knotData);

    /**
     * delete the knot and its associated edges
     *
     * @param id        id of the knot to be deleted
     * @param recursive true if you want to delete all related {@link Edge}s and {@link Knot}s below it
     * @return true if successfully deleted
     */
    List<AtomicKnot> deleteKnot(String id, boolean recursive);


    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////  EDGE OPERATIONS  /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @param knotId    id of the {@link Knot}
     * @param variation variation of a {@link Knot}. An edge to connect to the {@link Knot} with another variation of the Knot
     * @return edgeId
     * @throws BonsaiError 1. if there are cycles {@link com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode#CYCLE_DETECTED}
     *                     2. if the edge's pivot is violated at that level {@link com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode#EDGE_PIVOT_CONSTRAINT_ERROR}
     */
    String addVariation(String knotId, Variation variation) throws BonsaiError;

    /**
     * update the edge with a new set of filters
     * needs to be a valid update id
     *
     * @param knotId  knot id to which the edge is associated with
     * @param edgeId  edge id to be updated
     * @param filters filters set
     * @return false if edge id wasnt present, or if edge wasnt updated
     */
    boolean updateEdgeFilters(String knotId, String edgeId, List<Filter> filters);

    /**
     * add a bunch of filters to an edge.
     * this method will append to the existing list of filters that already exist
     *
     * @param edgeId  edge id to which filters will be added to
     * @param filters filters to be added
     * @return false if edge wasnt updated
     */
    boolean addEdgeFilters(String edgeId, List<Filter> filters);

    /**
     * remove the edge only (all {@link Knot}s that the {@link Edge} contains, will continue to exist, but will be disconnected from the main tree
     *
     * @param edgeId edge id to be removed
     * @return true if it was successfully removed
     */
    boolean unlinkVariation(String knotId, String edgeId);

    List<AtomicKnot> deleteVariation(String knotId, String edgeId, boolean recursive);

    AtomicEdge getEdge(String edgeId);


    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  TREE OPERATIONS  //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param key    key to associate the Knot with
     * @param knotId if of the Knot to be associated with the key
     * @return knot that was mapped
     * @throws BonsaiError if there are some sort of cycles
     */
    AtomicKnot createMapping(String key, String knotId) throws BonsaiError;

    AtomicKnot createMapping(String key, KnotData knotData) throws BonsaiError;

    AtomicKnot removeMapping(String key) throws BonsaiError;

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
    KeyNode evaluate(String key, Context context);
}
