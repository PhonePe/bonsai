# Delta Operations

The Knot/Edge and Tree operations are useful for granular independent operations. However, a more practical way to make
changes, would be through a series of related operations that need to be applied together. Imagine this happening 
through a UI where all changes are first drafted and applied in one go.
Delta operations allow you to make batch changes to the Bonsai tree structure in a single operation. This is
useful when you need to make multiple related changes and want to ensure consistency. This guide explains how to use
delta operations effectively.

!!! note
    Delta operations are not Atomic. The operations get applied one by one across the three storage interfaces. As you
    might have realized, atomicity depends on type of databases used. If an error occurs during the application of delta operations, 
    the tree may be left in an inconsistent state. It is up to the application to handle errors and revert changes if needed.

## Understanding Delta Operations

Delta operations are a way to apply a series of changes to a tree structure as a single unit of work. They provide several benefits:

- **Drafting Changes**: Allows you to draft a series of changes before applying them
- **Auditability**: Changes can be tracked and potentially reverted
- **Efficiency**: Multiple changes can be applied in a single operation

## Types of Delta Operations

Bonsai supports three types of delta operations:

- **KEY_MAPPING_DELTA**: Operations related to key mappings
- **KNOT_DELTA**: Operations related to Knots
- **EDGE_DELTA**: Operations related to Edges

Each operation type has its own specific class and builder pattern.

## Creating Delta Operations

You can create delta operations using the specific operation builders:

```java
// Create a knot delta operation
KnotDeltaOperation knotOperation = KnotDeltaOperation.builder()
    .knot(knot) // Pass a Knot object
    .build();

// Create a key mapping delta operation
KeyMappingDeltaOperation mappingOperation = KeyMappingDeltaOperation.builder()
    .keyId("newKey")
    .knotId("generatedKnotId")
    .build();

// Create an edge delta operation
EdgeDeltaOperation edgeOperation = EdgeDeltaOperation.builder()
    .edge(edge) // Pass an Edge object
    .build();
```

## Applying Delta Operations

To apply a list of delta operations, use the `applyDeltaOperations` method:

```java
// Create a list of delta operations
List<DeltaOperation> operations = new ArrayList<>();
operations.add(createKnotOp);
operations.add(createMappingOp);

// Apply the delta operations
TreeKnotState result = bonsai.applyDeltaOperations("rootKey", operations);

// The result contains the updated tree and revert operations
TreeKnot updatedTree = result.getTreeKnot();
List<DeltaOperation> revertOperations = result.getRevertDeltaOperations();
```

The `applyDeltaOperations` method returns a `TreeKnotState` object that contains:

- The updated tree structure (`TreeKnot`)
- A list of operations that can be used to revert the changes (`revertDeltaOperations`)

## Example: Creating a Complete Tree with Delta Operations

Here's an example of using delta operations to create a complete tree structure:

```java
// Create a list of delta operations
List<DeltaOperation> operations = new ArrayList<>();

// Create the leaf knots
Knot eligibleKnot = Knot.builder()
    .id("eligibleKnot")
    .knotData(ValuedKnotData.builder().booleanValue(true).build())
    .properties(Map.of("description", "User is eligible"))
    .build();

operations.add(KnotDeltaOperation.builder()
    .knot(eligibleKnot)
    .build());

Knot ineligibleKnot = Knot.builder()
    .id("ineligibleKnot")
    .knotData(ValuedKnotData.builder().booleanValue(false).build())
    .properties(Map.of("description", "User is ineligible"))
    .build();

operations.add(KnotDeltaOperation.builder()
    .knot(ineligibleKnot)
    .build());

// Create the root knot
Knot rootKnot = Knot.builder()
    .id("rootKnot")
    .knotData(ValuedKnotData.builder().build())
    .properties(Map.of("description", "User eligibility decision point"))
    .build();

operations.add(KnotDeltaOperation.builder()
    .knot(rootKnot)
    .build());

// Create the edges
Edge edge1 = Edge.builder()
    .id("edge1")
    .sourceKnotId("rootKnot")
    .targetKnotId("eligibleKnot")
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(18)
            .build(),
        Filter.builder()
            .path("$.user.country")
            .operator(Operator.IN)
            .value(List.of("US", "CA", "UK"))
            .build()
    ))
    .build();

operations.add(EdgeDeltaOperation.builder()
    .edge(edge1)
    .build());

Edge edge2 = Edge.builder()
    .id("edge2")
    .sourceKnotId("rootKnot")
    .targetKnotId("ineligibleKnot")
    .filters(List.of())
    .build();

operations.add(EdgeDeltaOperation.builder()
    .edge(edge2)
    .build());

// Create the key mapping
operations.add(KeyMappingDeltaOperation.builder()
    .keyId("userEligibility")
    .knotId("rootKnot")
    .build());

// Apply the delta operations
TreeKnotState result = bonsai.applyDeltaOperations(null, operations);
```

## Example: Updating an Existing Tree with Delta Operations

Here's an example of using delta operations to update an existing tree structure:

```java
// Create a list of delta operations
List<DeltaOperation> operations = new ArrayList<>();

// Update a knot's data
Knot updatedKnot = Knot.builder()
    .id("eligibleKnot")
    .knotData(ValuedKnotData.builder().booleanValue(true).build())
    .properties(Map.of("description", "User is eligible", "version", 1L))
    .build();

operations.add(KnotDeltaOperation.builder()
    .knot(updatedKnot)
    .build());

// Update an edge's filters
Edge updatedEdge = Edge.builder()
    .id("edge1")
    .sourceKnotId("rootKnot")
    .targetKnotId("eligibleKnot")
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(21)
            .build(),
        Filter.builder()
            .path("$.user.country")
            .operator(Operator.IN)
            .value(List.of("US", "CA"))
            .build()
    ))
    .properties(Map.of("version", 1L))
    .build();

operations.add(EdgeDeltaOperation.builder()
    .edge(updatedEdge)
    .build());

// Apply the delta operations
TreeKnotState result = bonsai.applyDeltaOperations("userEligibility", operations);
```

## Reverting Delta Operations

The `applyDeltaOperations` method returns a list of operations that can be used to revert the changes:

```java
// Apply delta operations
TreeKnotState result = bonsai.applyDeltaOperations("userEligibility", operations);

// Get the revert operations
List<DeltaOperation> revertOperations = result.getRevertDeltaOperations();

// If needed, apply the revert operations to undo the changes
TreeKnotState revertResult = bonsai.applyDeltaOperations("userEligibility", revertOperations);
```

This allows you to implement undo functionality or rollback changes if needed.

## Error Handling

Delta operations can throw various exceptions:

```java
try {
    TreeKnotState result = bonsai.applyDeltaOperations("userEligibility", operations);
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.VERSION_MISMATCH) {
        // Handle version mismatch error
        System.err.println("Version mismatch: " + e.getMessage());
    } else if (e.getErrorCode() == BonsaiErrorCode.CYCLE_DETECTED) {
        // Handle cycle detected error
        System.err.println("Cycle detected: " + e.getMessage());
    } else {
        // Handle other errors
        throw e;
    }
}
```

## Best Practices

- **Group related changes**: Include all related changes in a single delta operation
- **Consider versioning**: Include version information in update operations to prevent conflicts
- **Handle errors appropriately**: Be prepared to handle errors and potentially retry operations
- **Use meaningful IDs**: Use meaningful IDs for created Knots and Edges
- **Test thoroughly**: Test delta operations thoroughly to ensure they work as expected
- **Consider transaction boundaries**: Think about what constitutes a logical transaction in your application
- **Use revert operations**: Store revert operations for important changes to enable undo functionality
