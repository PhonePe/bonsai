package com.phonepe.platform.bonsai.core.visitor.delta.impl;

import com.google.common.base.Preconditions;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.vital.ComponentBonsaiTreeValidator;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.models.TreeKnotState;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.model.Converters;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is the default implementation of trying and adding Delta's operation on given TreeKnot.
 */
@Slf4j
public class TreeKnotStateDeltaOperationModifierVisitor implements DeltaOperationVisitor<TreeKnotState> {

    private final ComponentBonsaiTreeValidator treeComponentValidator;
    private final KnotStore<String, Knot> knotStore;
    private final EdgeStore<String, Edge> edgeStore;


    public TreeKnotStateDeltaOperationModifierVisitor(final ComponentBonsaiTreeValidator treeComponentValidator,
                                                      final KnotStore<String, Knot> knotStore,
                                                      final EdgeStore<String, Edge> edgeStore) {
        Preconditions.checkNotNull(treeComponentValidator, "treeComponentValidator should not be null.");
        Preconditions.checkNotNull(knotStore, "KnotStore should not be null.");
        Preconditions.checkNotNull(edgeStore, "EdgeStore should not be null.");
        this.treeComponentValidator = treeComponentValidator;
        this.knotStore = knotStore;
        this.edgeStore = edgeStore;
    }

    private static <T> int safeSize(Collection<T> treeEdges) {
        return treeEdges == null ? 0 : treeEdges.size();
    }

    /**
     * Function to add {@link KeyMappingDeltaOperation} object into TreeKnot.
     *
     * @param metaData                 - {@link TreeKnotState} object contains TreeKnot and revert-delta-operations.
     * @param keyMappingDeltaOperation - {@link KeyMappingDeltaOperation} object; it contains key and knotId mapping.
     * @return {@link TreeKnot} object.
     */
    @Override
    public TreeKnotState visit(final TreeKnotState metaData,
                               final KeyMappingDeltaOperation keyMappingDeltaOperation) {
        treeComponentValidator.validate(keyMappingDeltaOperation);

        if (metaData.getTreeKnot() == null) {
            final TreeKnot treeKnot = TreeKnot.builder()
                    .id(keyMappingDeltaOperation.getKnotId())
                    .build();
            return TreeKnotState.builder()
                    .treeKnot(treeKnot)
                    .build();
        } else {
            final String errorOutput = "[bonsai] The tree with [Key : %s] and [KnotId : %s] already exist.".formatted(
                    keyMappingDeltaOperation.getKeyId(), keyMappingDeltaOperation.getKnotId());
            log.error(errorOutput);
            throw new BonsaiError(BonsaiErrorCode.TREE_ALREADY_EXIST, errorOutput);
        }
    }

    /**
     * Function to add {@link KnotDeltaOperation} object into TreeKnot.
     *
     * @param metaData           - {@link TreeKnotState} object contains TreeKnot and revert-delta-operations.
     * @param knotDeltaOperation -  {@link KnotDeltaOperation} object; it contains all the data to represent a single knot and
     *                           its corresponding edgeIds.
     * @return {@link TreeKnot} object.
     */
    @Override
    public TreeKnotState visit(final TreeKnotState metaData,
                               final KnotDeltaOperation knotDeltaOperation) {
        treeComponentValidator.validate(knotDeltaOperation);

        final TreeKnot treeKnot = metaData.getTreeKnot();
        if (treeKnot == null) {
            final String errorOutput = "[bonsai] Can't insert KnotDeltaOperation into non-existing tree.";
            log.error(errorOutput);
            throw new BonsaiError(BonsaiErrorCode.TREE_DOES_NOT_EXIST, errorOutput);
        }

        final List<DeltaOperation> deltaOperationsToPreviousState =
                (metaData.getDeltaOperationsToPreviousState() != null)
                        ? metaData.getDeltaOperationsToPreviousState() : new ArrayList<>();

        boolean isSuccessfullyInserted =
                insertKnotDeltaDataIntoTreeKnot(treeKnot, deltaOperationsToPreviousState, knotDeltaOperation);
        if (!isSuccessfullyInserted) {
            final String errorOutput =
                    "[bonsai] Failed to insert knotDeltaOperation :[%s] into TreeKnot.".formatted(knotDeltaOperation);
            log.error(errorOutput);
        }

        metaData.setTreeKnot(treeKnot);
        metaData.setDeltaOperationsToPreviousState(deltaOperationsToPreviousState);
        return metaData;
    }

    /**
     * Recursive function to add new knot into TreeKnot.
     *
     * @param treeKnot             - In-memory TreeKnot which stores entire details related to the tree.
     * @param revertDeltaOperation - List of Delta Operations needed to revert the collective changes being carried out.
     * @param knotDeltaOperation   - {@link KnotDeltaOperation} object, stores the details of knot is to be added into TreeKnot.
     * @return true, if successfully inserted or false, if not.
     */
    private boolean insertKnotDeltaDataIntoTreeKnot(final TreeKnot treeKnot,
                                                    final List<DeltaOperation> revertDeltaOperation,
                                                    final KnotDeltaOperation knotDeltaOperation) {
        boolean isSuccessfullyInserted = false;
        if (knotDeltaOperation.getKnot().getId().equals(treeKnot.getId())) {
            final List<EdgeIdentifier> edgeIdentifierList = (knotDeltaOperation.getKnot().getEdges() == null)
                    ? new ArrayList<>() :
                    knotDeltaOperation.getKnot().getEdges(); // If-else check to avoid error in loop.

            updateOnExistingKnot(treeKnot, revertDeltaOperation, edgeIdentifierList);

            final List<TreeEdge> treeEdgeList = new ArrayList<>();
            int edgeNumber = safeSize(treeKnot.getTreeEdges()) + 1;
            for (EdgeIdentifier edgeIdentifier : edgeIdentifierList) {
                edgeIdentifier.setNumber(edgeNumber++);
                final Edge fetchedEdge = edgeStore.getEdge(edgeIdentifier.getId());
                if (fetchedEdge == null) {
                    final TreeEdge treeEdge = TreeEdge.builder()
                            .edgeIdentifier(edgeIdentifier)
                            .build();
                    treeEdgeList.add(treeEdge);
                } else {
                    treeKnot.getTreeEdges()
                            .stream()
                            .filter(treeEdgeSingle -> edgeIdentifier.getId()
                                    .equals(treeEdgeSingle.getEdgeIdentifier().getId()))
                            .findFirst()
                            .ifPresent(treeEdgeList::add);
                }
            }
            treeKnot.setTreeEdges(treeEdgeList);
            treeKnot.setVersion(knotDeltaOperation.getKnot().getVersion());
            treeKnot.setKnotData(knotDeltaOperation.getKnot().getKnotData());
            treeKnot.setProperties(knotDeltaOperation.getKnot().getProperties());
            return true;
        }

        if (treeKnot.getTreeEdges() == null || treeKnot.getTreeEdges().isEmpty()) {
            return false;
        } else {
            final List<TreeKnot> childrenTreeKnots = treeKnot.getTreeEdges()
                    .stream()
                    .map(TreeEdge::getTreeKnot)
                    .filter(Objects::nonNull)
                    .toList();

            for (int i = 0; !isSuccessfullyInserted && i < childrenTreeKnots.size(); i++) {
                isSuccessfullyInserted = insertKnotDeltaDataIntoTreeKnot(childrenTreeKnots.get(i), revertDeltaOperation,
                        knotDeltaOperation);
            }
        }

        return isSuccessfullyInserted;
    }

    private void updateOnExistingKnot(TreeKnot treeKnot, List<DeltaOperation> revertDeltaOperation,
                           List<EdgeIdentifier> edgeIdentifierList) {
        // To ensure if the existing setup is being updated/deleted.
        if (knotStore.containsKnot(treeKnot.getId())) {
            final Knot revertKnotState = Converters.toKnot(treeKnot);
            if (revertKnotState != null) {
                revertKnotState.setVersion(0);
            }
            final KnotDeltaOperation revertKnotDeltaOperation = new KnotDeltaOperation(revertKnotState);
            revertDeltaOperation.add(revertKnotDeltaOperation);

            Optional.ofNullable(treeKnot.getTreeEdges())
                    .orElse(Collections.emptyList()) // To avoid NullPointerException.
                    .stream()
                    .filter(treeEdge -> edgeIdentifierList.stream()
                            .noneMatch(updatedEdge -> updatedEdge.getId()
                                    .equals(treeEdge.getEdgeIdentifier().getId())))
                    .forEach(treeEdge -> captureRevertTreeEdge(treeEdge, revertDeltaOperation));
        }
    }

    /**
     * Function to add {@link EdgeDeltaOperation} object into TreeKnot.
     *
     * @param metaData           - {@link TreeKnotState} object contains TreeKnot and revert-delta-operations.
     * @param edgeDeltaOperation - {@link EdgeDeltaOperation} object; it contains all the data to represent a single edge and
     *                           its corresponding child knotId.
     * @return {@link TreeKnot} object.
     */
    @Override
    public TreeKnotState visit(final TreeKnotState metaData,
                               final EdgeDeltaOperation edgeDeltaOperation) {
        treeComponentValidator.validate(edgeDeltaOperation);

        final TreeKnot treeKnot = metaData.getTreeKnot();
        if (treeKnot == null) {
            final String errorOutput = "[bonsai] Can't insert EdgeDeltaOperation into non-existing tree.";
            log.error(errorOutput);
            throw new BonsaiError(BonsaiErrorCode.TREE_DOES_NOT_EXIST, errorOutput);
        }

        final List<DeltaOperation> deltaOperationsToPreviousState =
                (metaData.getDeltaOperationsToPreviousState() != null)
                        ? metaData.getDeltaOperationsToPreviousState() : new ArrayList<>();

        boolean isSuccessfullyInserted =
                insertEdgeDeltaDataIntoTreeKnot(treeKnot, deltaOperationsToPreviousState, edgeDeltaOperation);
        if (!isSuccessfullyInserted) {
            final String errorOutput =
                    "[bonsai] Failed to insert edgeDeltaOperation :[%s] into TreeKnot.".formatted(edgeDeltaOperation);
            log.error(errorOutput);
        }

        metaData.setTreeKnot(treeKnot);
        metaData.setDeltaOperationsToPreviousState(deltaOperationsToPreviousState);
        return metaData;
    }

    /**
     * Recursive function to add edge into TreeKnot.
     *
     * @param treeKnot             - In-memory TreeKnot which stores entire details related to the tree.
     * @param revertDeltaOperation - List of Delta Operations needed to revert the collective changes being carried out.
     * @param edgeDeltaOperation   - {@link EdgeDeltaOperation} object, stores the details of edge is to be added into TreeKnot.
     * @return true, if successfully inserted or false, if not.
     */
    private boolean insertEdgeDeltaDataIntoTreeKnot(final TreeKnot treeKnot,
                                                    final List<DeltaOperation> revertDeltaOperation,
                                                    final EdgeDeltaOperation edgeDeltaOperation) {
        boolean isSuccessfullyInserted = false;
        final List<TreeEdge> treeEdgeList = (treeKnot.getTreeEdges() != null) ?
                treeKnot.getTreeEdges() : new ArrayList<>();

        for (final TreeEdge treeEdge : treeEdgeList) {
            final EdgeIdentifier edgeIdentifier = treeEdge.getEdgeIdentifier();
            if (edgeDeltaOperation.getEdge().getEdgeIdentifier().getId().equals(edgeIdentifier.getId())) {
                // To ensure if the existing setup is being updated/deleted.
                if (edgeStore.containsEdge(treeEdge.getEdgeIdentifier().getId())) {
                    final Edge revertEdgeState = Converters.toEdge(treeEdge);
                    if (revertEdgeState != null) {
                        revertEdgeState.setVersion(0);
                    }
                    final EdgeDeltaOperation revertEdgeDeltaOperation = new EdgeDeltaOperation(revertEdgeState);
                    revertDeltaOperation.add(revertEdgeDeltaOperation);

                    // Capture in-case the link is directing to the new variation.
                    if (!treeEdge.getTreeKnot().getId().equals(edgeDeltaOperation.getEdge().getKnotId())) {
                        captureRevertTreeKnot(treeEdge.getTreeKnot(), revertDeltaOperation);
                    }
                }

                treeEdge.setVersion(edgeDeltaOperation.getEdge().getVersion());
                treeEdge.setFilters(edgeDeltaOperation.getEdge().getFilters());
                treeEdge.setProperties(edgeDeltaOperation.getEdge().getProperties());
                treeEdge.setLive(edgeDeltaOperation.getEdge().isLive());
                treeEdge.setPercentage(edgeDeltaOperation.getEdge().getPercentage());

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
                    .toList();

            for (int i = 0; !isSuccessfullyInserted && i < childrenTreeKnots.size(); i++) {
                isSuccessfullyInserted = insertEdgeDeltaDataIntoTreeKnot(childrenTreeKnots.get(i), revertDeltaOperation,
                        edgeDeltaOperation);
            }
        }

        return isSuccessfullyInserted;
    }

    private void captureRevertTreeEdge(final TreeEdge treeEdge, final List<DeltaOperation> revertDeltaOperation) {
        final Edge revertEdge = Converters.toEdge(treeEdge);
        if (revertEdge != null) {
            revertEdge.setVersion(0);
        }
        final EdgeDeltaOperation revertEdgeDeltaOperation = new EdgeDeltaOperation(revertEdge);
        revertDeltaOperation.add(revertEdgeDeltaOperation);

        captureRevertTreeKnot(treeEdge.getTreeKnot(), revertDeltaOperation);
    }

    private void captureRevertTreeKnot(final TreeKnot treeKnot, final List<DeltaOperation> revertDeltaOperation) {
        final Knot revertKnot = Converters.toKnot(treeKnot);
        if (revertKnot != null) {
            revertKnot.setVersion(0);
        }
        final KnotDeltaOperation revertKnotDeltaOperation = new KnotDeltaOperation(revertKnot);
        revertDeltaOperation.add(revertKnotDeltaOperation);

        Optional.ofNullable(treeKnot.getTreeEdges())
                .orElse(Collections.emptyList()) // To avoid NullPointerException.
                .forEach(treeEdge -> captureRevertTreeEdge(treeEdge, revertDeltaOperation));
    }
}

