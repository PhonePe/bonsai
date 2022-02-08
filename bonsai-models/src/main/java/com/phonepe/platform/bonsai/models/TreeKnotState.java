package com.phonepe.platform.bonsai.models;

import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeKnotState {
    private TreeKnot treeKnot;
    private List<DeltaOperation> deltaOperationsToPreviousState;
}
