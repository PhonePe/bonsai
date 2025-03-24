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

Bonsai supports several types of delta operations:

- **CREATE_KNOT**: Create a new Knot
- **UPDATE_KNOT_DATA**: Update a Knot's data
- **UPDATE_KNOT_PROPERTIES**: Update a Knot's properties
- **DELETE_KNOT**: Delete a Knot
- **CREATE_EDGE**: Create a new Edge
- **UPDATE_EDGE**: Update an Edge
- **DELETE_EDGE**: Delete an Edge
- **CREATE_MAPPING**: Create a key mapping
- **UPDATE_MAPPING**: Update a key mapping
- **DELETE_MAPPING**: Delete a key mapping

## Creating Delta Operations

You can create delta operations using the `DeltaOperation.builder()` method:

```java
// Create a delta operation to create a new knot
DeltaOperation createKnotOp = DeltaOperation.builder()
    .operationType(OperationType.CREATE_KNOT)
    .knotData(ValuedKnotData.builder().stringValue("New value").build())
    .properties(Map.of("description", "New knot"))
    .build();

// Create a delta operation to create a mapping
DeltaOperation createMappingOp = DeltaOperation.builder()
    .operationType(OperationType.CREATE_MAPPING)
    .key("newKey")
    .knotId("generatedKnotId") // ID from the previous operation
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
operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_KNOT)
    .id("eligibleKnot")
    .knotData(ValuedKnotData.builder().booleanValue(true).build())
    .properties(Map.of("description", "User is eligible"))
    .build());

operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_KNOT)
    .id("ineligibleKnot")
    .knotData(ValuedKnotData.builder().booleanValue(false).build())
    .properties(Map.of("description", "User is ineligible"))
    .build());

// Create the root knot
operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_KNOT)
    .id("rootKnot")
    .knotData(ValuedKnotData.builder().build())
    .properties(Map.of("description", "User eligibility decision point"))
    .build());

// Create the edges
operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_EDGE)
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
    .build());

operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_EDGE)
    .id("edge2")
    .sourceKnotId("rootKnot")
    .targetKnotId("ineligibleKnot")
    .filters(List.of())
    .build());

// Create the key mapping
operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_MAPPING)
    .key("userEligibility")
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
operations.add(DeltaOperation.builder()
    .operationType(OperationType.UPDATE_KNOT_DATA)
    .id("eligibleKnot")
    .knotData(ValuedKnotData.builder().booleanValue(true).build())
    .properties(Map.of("description", "User is eligible", "version", 1L))
    .build());

// Update an edge's filters
operations.add(DeltaOperation.builder()
    .operationType(OperationType.UPDATE_EDGE)
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
