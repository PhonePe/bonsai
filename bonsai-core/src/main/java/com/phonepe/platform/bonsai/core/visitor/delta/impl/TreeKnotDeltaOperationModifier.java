package com.phonepe.platform.bonsai.core.visitor.delta.impl;

import com.google.common.base.Preconditions;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.vital.ComponentBonsaiTreeValidator;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is the default implementation of trying and adding Delta's operation on given TreeKnot.
 *
 * @author - suraj.s
 * @date - 2019-10-15
 */
@Slf4j
public class TreeKnotDeltaOperationModifier implements DeltaOperationVisitor<TreeKnot> {

    private final ComponentBonsaiTreeValidator treeComponentValidator;
    private final KnotStore<String, Knot> knotStore;
    private final EdgeStore<String, Edge> edgeStore;


    public TreeKnotDeltaOperationModifier(final ComponentBonsaiTreeValidator treeComponentValidator,
                                          final KnotStore<String, Knot> knotStore,
                                          final EdgeStore<String, Edge> edgeStore) {
        Preconditions.checkNotNull(treeComponentValidator, "treeComponentValidator should not be null.");
        Preconditions.checkNotNull(knotStore, "KnotStore should not be null.");
        Preconditions.checkNotNull(edgeStore, "EdgeStore should not be null.");
        this.treeComponentValidator = treeComponentValidator;
        this.knotStore = knotStore;
        this.edgeStore = edgeStore;
    }

    /**
     * Function to add {@link KeyMappingDeltaOperation} object into TreeKnot.
     * @param treeKnot - {@link TreeKnot} object.
     * @param keyMappingDeltaOperation - {@link KeyMappingDeltaOperation} object; it contains key and knotId mapping.
     * @return {@link TreeKnot} object.
     */
    @Override
    public TreeKnot visit(final TreeKnot treeKnot,
                          final KeyMappingDeltaOperation keyMappingDeltaOperation) {
        treeComponentValidator.validate(keyMappingDeltaOperation);

        if (treeKnot == null) {
            return TreeKnot.builder()
                    .id(keyMappingDeltaOperation.getKnotId())
                    .build();
        } else {
            final String errorOutput = String.format("The tree with [Key : %s] and [KnotId : %s] already exist.",
                    keyMappingDeltaOperation.getKeyId(), keyMappingDeltaOperation.getKnotId());
            log.error(errorOutput);
            throw new BonsaiError(BonsaiErrorCode.TREE_ALREADY_EXIST, errorOutput);
        }
    }

    /**
     * Function to add {@link KnotDeltaOperation} object into TreeKnot.
     * @param treeKnot  - {@link TreeKnot} object.
     * @param knotDeltaOperation -  {@link KnotDeltaOperation} object; it contains all the data to represent a single knot and
     *                      its corresponding edgeIds.
     * @return {@link TreeKnot} object.
     */
    @Override
    public TreeKnot visit(final TreeKnot treeKnot, final KnotDeltaOperation knotDeltaOperation) {
        treeComponentValidator.validate(knotDeltaOperation);

        if (treeKnot == null) {
            final String errorOutput = "Can't insert KnotDeltaOperation into non-existing tree.";
            log.error(errorOutput);
            throw new BonsaiError(BonsaiErrorCode.TREE_DOES_NOT_EXIST, errorOutput);
        }

        boolean isSuccessfullyInserted = insertKnotDeltaDataIntoTreeKnot(treeKnot, knotDeltaOperation);
        if (!isSuccessfullyInserted) {
            final String errorOutput = String.format("Failed to insert knotDeltaOperation :[%s] into TreeKnot.", knotDeltaOperation);
            log.error(errorOutput);
        }

        return treeKnot;
    }

    /**
     * Recursive function to add new knot into TreeKnot.
     * @param treeKnot - In-memory TreeKnot which stores entire details related to the tree.
     * @param knotDeltaOperation - {@link KnotDeltaOperation} object, stores the details of knot is to be added into TreeKnot.
     * @return true, if successfully inserted or false, if not.
     */
    private boolean insertKnotDeltaDataIntoTreeKnot(final TreeKnot treeKnot, final KnotDeltaOperation knotDeltaOperation) {
        boolean isSuccessfullyInserted = false;
        if (knotDeltaOperation.getKnot().getId().equals(treeKnot.getId())) {
            final List<EdgeIdentifier> edgeIdentifierList = (knotDeltaOperation.getKnot().getEdges() == null)
                    ? new ArrayList<>() : knotDeltaOperation.getKnot().getEdges(); // If-else check to avoid error in loop.
            final List<TreeEdge> treeEdgeList = new ArrayList<>();
            for(EdgeIdentifier edgeIdentifier: edgeIdentifierList) {
                final Edge fetchedEdge = edgeStore.getEdge(edgeIdentifier.getId());
                if (fetchedEdge == null) {
                    TreeEdge treeEdge = TreeEdge.builder()
                            .edgeIdentifier(edgeIdentifier)
                            .build();
                    treeEdgeList.add(treeEdge);
                }
            }
            Optional.ofNullable(treeKnot.getTreeEdges()).ifPresent(treeEdgeList::addAll);
            treeKnot.setTreeEdges(treeEdgeList);
            treeKnot.setVersion(knotDeltaOperation.getKnot().getVersion());
            treeKnot.setKnotData(knotDeltaOperation.getKnot().getKnotData());
            return true;
        }

        if (treeKnot.getTreeEdges() == null || treeKnot.getTreeEdges().isEmpty()) {
            return false;
        } else {
           final List<TreeKnot> childrenTreeKnots = treeKnot.getTreeEdges()
                   .stream()
                   .filter(treeEdge -> treeEdge.getTreeKnot() != null)
                   .map(TreeEdge::getTreeKnot)
                   .collect(Collectors.toList());

           for(int i=0; !isSuccessfullyInserted && i<childrenTreeKnots.size(); i++) {
               isSuccessfullyInserted = insertKnotDeltaDataIntoTreeKnot(childrenTreeKnots.get(i), knotDeltaOperation);
           }
        }

        return isSuccessfullyInserted;
    }

    /**
     * Function to add {@link EdgeDeltaOperation} object into TreeKnot.
     * @param treeKnot - {@link TreeKnot} object.
     * @param edgeDeltaOperation - {@link EdgeDeltaOperation} object; it contains all the data to represent a single edge and
     *                      its corresponding child knotId.
     * @return {@link TreeKnot} object.
     */
    @Override
    public TreeKnot visit(final TreeKnot treeKnot, final EdgeDeltaOperation edgeDeltaOperation) {
        treeComponentValidator.validate(edgeDeltaOperation);

        if (treeKnot == null) {
            final String errorOuput = "Can't insert EdgeDeltaOperation into non-existing tree.";
            log.error(errorOuput);
            throw new BonsaiError(BonsaiErrorCode.TREE_DOES_NOT_EXIST, errorOuput);
        }

        boolean isSuccessfullyInserted = insertEdgeDeltaDataIntoTreeKnot(treeKnot, edgeDeltaOperation);
        if (!isSuccessfullyInserted) {
            final String errorOutput = String.format("Failed to insert edgeDeltaOperation :[%s] into TreeKnot.", edgeDeltaOperation);
            log.error(errorOutput);
        }

        return treeKnot;
    }

    /**
     * Recursive function to add edge into TreeKnot.
     * @param treeKnot - In-memory TreeKnot which stores entire details related to the tree.
     * @param edgeDeltaOperation - {@link EdgeDeltaOperation} object, stores the details of edge is to be added into TreeKnot.
     * @return true, if successfully inserted or false, if not.
     */
    private boolean insertEdgeDeltaDataIntoTreeKnot(final TreeKnot treeKnot, final EdgeDeltaOperation edgeDeltaOperation) {
        boolean isSuccessfullyInserted = false;
        final List<TreeEdge> treeEdgeList = (treeKnot.getTreeEdges() != null) ?
                treeKnot.getTreeEdges() : new ArrayList<>();

        for (int i=0; i<treeEdgeList.size(); i++) {
            final TreeEdge treeEdge = treeEdgeList.get(i);
            final EdgeIdentifier edgeIdentifier = treeEdge.getEdgeIdentifier();
            if (edgeDeltaOperation.getEdge().getEdgeIdentifier().getId().equals(edgeIdentifier.getId())) {
                treeEdge.setVersion(edgeDeltaOperation.getEdge().getVersion());
                treeEdge.setFilters(edgeDeltaOperation.getEdge().getFilters());

                final String childKnotId = edgeDeltaOperation.getEdge().getKnotId();
                final Knot fetchedKnot = knotStore.getKnot(childKnotId);
                if (fetchedKnot == null) {
                    final TreeKnot childTreeKnot = TreeKnot.builder()
                            .id(edgeDeltaOperation.getEdge().getKnotId())
                            .build();
                    treeEdge.setTreeKnot(childTreeKnot);
                }
                return true;
            }
        }

        if (treeKnot.getTreeEdges() == null || treeKnot.getTreeEdges().isEmpty()) {
            return false;
        } else {
            final List<TreeKnot> childrenTreeKnots = treeKnot.getTreeEdges()
                    .stream()
                    .filter(treeEdge -> treeEdge.getTreeKnot() != null)
                    .map(TreeEdge::getTreeKnot)
                    .collect(Collectors.toList());

            for(int i=0; !isSuccessfullyInserted && i<childrenTreeKnots.size(); i++) {
                isSuccessfullyInserted = insertEdgeDeltaDataIntoTreeKnot(childrenTreeKnots.get(i), edgeDeltaOperation);
            }
        }

        return isSuccessfullyInserted;
    }
}

