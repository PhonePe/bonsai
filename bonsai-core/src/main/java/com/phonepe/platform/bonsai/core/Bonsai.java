package com.phonepe.platform.bonsai.core;

import com.phonepe.platform.bonsai.core.core.Context;
import com.phonepe.platform.bonsai.core.core.Edge;
import com.phonepe.platform.bonsai.core.core.Knot;
import com.phonepe.platform.bonsai.core.core.KnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.KeyNode;

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

    /**
     * create a {@link Knot} out of the {@link KnotData}
     *
     * @param knotData some kind of data knot
     * @return created Knot with some id, version, etc
     */
    Knot create(KnotData knotData);

    /**
     * associate a key with some {@link KnotData}
     *
     * @param key      a string for association
     * @param knotData data for the knot
     * @return created Knot with some id, version, etc
     */
    Knot add(String key, KnotData knotData);

    /**
     * @param key  key to associate the Knot with
     * @param knot Knot to be associated with the key
     * @return created Knot with some id, version, etc
     * @throws BonsaiError if there are some sort of cycles
     */
    Knot add(String key, Knot knot) throws BonsaiError;

    /**
     * @param id   id of the {@link Knot}
     * @param edge edge to connect to the {@link Knot}
     * @return true if successfully connected
     * @throws BonsaiError 1. if there are cycles {@link com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode#CYCLE_DETECTED}
     *                     2. if the edge's pivot is violated at that level {@link com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode#EDGE_PIVOT_CONSTRAINT_ERROR}
     */
    boolean connect(String id, Edge edge) throws BonsaiError;

    /**
     * get the Knot associated with the key
     *
     * @param key key
     * @return matching Knot
     */
    Knot get(String key);

    /**
     * get Knot for the id
     *
     * @param id id of Knot
     * @return Knot with the id
     */
    Knot getForId(String id);

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
