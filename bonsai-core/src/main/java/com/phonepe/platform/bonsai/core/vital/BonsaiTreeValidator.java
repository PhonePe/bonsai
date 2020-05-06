package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:05 PM
 */
public interface BonsaiTreeValidator {

    /**
     * Function signature to validate given {@link Knot} object.
     * @param knot - {@link Knot} object; it contains the details of knot.
     */
    void validate(Knot knot);

    /**
     * Function signature to validate and compare the similarity of given {@link Knot} objects.
     * @param existingKnot - {@link Knot} object; it contains the details of existing knot in the system.
     * @param newKnot - {@link Knot} object; it contains the details of newly going to be added knot into the system.
     */
    void validate(Knot existingKnot, Knot newKnot);

    /**
     * Function signature to validate given {@link Edge} object.
     * @param edge - {@link Edge} object; it contains edge details.
     */
    void validate(Edge edge);

    /**
     * Function signature to validate given {@link Context} object.
     * @param context - {@link Context} object; it contains user preferred contextual details.
     */
    void validate(Context context);

    /**
     * Function signature to validate given {@link Variation} object.
     * @param variation - {@link Variation} object; it contains the details of knotId and filters with
     *                  which new edge is going to be connected to.
     */
    void validate(Variation variation);

    /**
     * Function signature to validate given {@link KeyMappingDeltaOperation} object.
     * @param keyMappingDeltaOperation - {@link KeyMappingDeltaOperation} object; it contains key and knotId mapping.
     */
    void validate(KeyMappingDeltaOperation keyMappingDeltaOperation);

    /**
     * Function signature to validate given {@link KnotDeltaOperation} object.
     * @param knotDeltaOperation -  {@link KnotDeltaOperation} object; it contains all the data to represent
     *                           a single knot and its corresponding edgeIds.
     */
    void validate(KnotDeltaOperation knotDeltaOperation);

    /**
     * Function signature to validate given {@link EdgeDeltaOperation} object.
     * @param edgeDeltaOperation - {@link EdgeDeltaOperation} object; it contains all the data to represent
     *                           a single edge and its corresponding child knotId.
     */
    void validate(EdgeDeltaOperation edgeDeltaOperation);


    /**
     * Function signature to validate given {@link TreeKnot} object.
     * @param treeKnot - {@link TreeKnot} object, it contain an entire root knot with all its variation.
     */
    void validate(TreeKnot treeKnot);
}
