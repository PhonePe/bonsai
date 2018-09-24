package com.phonepe.platform.bonsai.models.model;

import lombok.Builder;
import lombok.Data;

/**
 * An edge signifies a directional component of a Bonsai, which points to a single {@link ProfoundKnot}
 * It is part of a {@link ProfoundKnot}
 *
 * @author tushar.naik
 * @version 1.0  10/07/18 - 2:41 PM
 */
@Data
public class ProfoundEdge {
    private String id;
    private ProfoundKnot profoundKnot;
    private int priority;
    private int version;

    @Builder
    public ProfoundEdge(String id, ProfoundKnot profoundKnot, int priority, int version) {
        this.id = id;
        this.profoundKnot = profoundKnot;
        this.priority = priority;
        this.version = version;
    }
}
