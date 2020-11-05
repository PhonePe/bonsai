package com.phonepe.platform.bonsai.models;

import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POJO class, contains MetaData around DeltaOperations.
 *
 * @author - suraj.s
 * @version - 1.0
 * @since - 05-10-2020
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeKnotState {
    private TreeKnot treeKnot;
    private List<DeltaOperation> deltaOperationsToPreviousState;
}
