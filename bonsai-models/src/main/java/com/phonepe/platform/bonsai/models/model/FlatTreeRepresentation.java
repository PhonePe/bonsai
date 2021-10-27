package com.phonepe.platform.bonsai.models.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class FlatTreeRepresentation {
    private String root;
    private Map<String, FlatNodeDetail> flatNodeMapping;
}
