# Tree Operations

Bonsai provides a comprehensive set of operations for managing the overall tree structure and key mappings. These operations allow you to create, read, update, and delete tree structures, as well as evaluate trees against a context.

## Key Mapping Operations

### Check if a Key Mapping Exists

```java
boolean exists = bonsai.containsKey("key");
```

### Create a Mapping Between a Key and an Existing Knot

```java
// Create a mapping between a key and an existing Knot
Knot knot = bonsai.createMapping("key", "knotId");
```

### Create a New Knot and Map it to a Key

```java
// Create a new Knot with data and map it to a key
Knot knot = bonsai.createMapping("key", 
    ValuedKnotData.builder().stringValue("Hello, World!").build(),
    Map.of("description", "A simple string knot")
);
```

### Get the Knot ID for a Key

```java
// Get the Knot ID for a key
String knotId = bonsai.getMapping("key");
```

### Remove a Key Mapping

```java
// Remove a key mapping (does not delete the Knot)
Knot unmappedKnot = bonsai.removeMapping("key");
```

## Tree Structure Operations

### Get the Complete Tree for a Key

```java
// Get the complete tree for a key
TreeKnot tree = bonsai.getCompleteTree("key");
```

The `TreeKnot` object represents the entire tree structure, including all Knots and Edges reachable from the root Knot.

### Create a Complete Tree from a TreeKnot Structure

```java
// Create a complete tree from a TreeKnot structure
Knot rootKnot = bonsai.createCompleteTree(treeKnot);
```

This operation is useful for importing tree structures or restoring trees from a backup.

## Tree Evaluation Operations

### Evaluate a Key Against a Context

```java
// Create a context for evaluation
Context context = Context.builder()
    .documentContext(JsonPath.parse("{\"user\": {\"age\": 25, \"country\": \"US\"}}"))
    .build();

// Evaluate the tree
KeyNode result = bonsai.evaluate("key", context);
```

The `KeyNode` object represents the result of the evaluation, containing the data from the final Knot reached during traversal.

### Get a Flat Representation of the Evaluated Tree

```java
// Get a flat representation of the evaluated tree
FlatTreeRepresentation flatTree = bonsai.evaluateFlat("key", context);
```

The `FlatTreeRepresentation` object provides a flattened view of the evaluation result, which can be useful for debugging or visualization.

## Delta Operations

Delta operations allow you to make batch changes to the tree structure. This is useful when you need to make multiple related changes atomically.

### Apply Delta Operations

```java
// Create a list of delta operations
List<DeltaOperation> operations = new ArrayList<>();

// Add an operation to create a new knot
operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_KNOT)
    .knotData(ValuedKnotData.builder().stringValue("New value").build())
    .properties(Map.of("description", "New knot"))
    .build());

// Add an operation to create a mapping
operations.add(DeltaOperation.builder()
    .operationType(OperationType.CREATE_MAPPING)
    .key("newKey")
    .knotId("generatedKnotId") // ID from the previous operation
    .build());

// Apply the delta operations
TreeKnotState result = bonsai.applyDeltaOperations("rootKey", operations);

// The result contains the updated tree and revert operations
TreeKnot updatedTree = result.getTreeKnot();
List<DeltaOperation> revertOperations = result.getRevertDeltaOperations();
```

### Types of Delta Operations

Bonsai supports several types of delta operations:

- `CREATE_KNOT`: Create a new Knot
- `UPDATE_KNOT_DATA`: Update a Knot's data
- `UPDATE_KNOT_PROPERTIES`: Update a Knot's properties
- `DELETE_KNOT`: Delete a Knot
- `CREATE_EDGE`: Create a new Edge
- `UPDATE_EDGE`: Update an Edge
- `DELETE_EDGE`: Delete an Edge
- `CREATE_MAPPING`: Create a key mapping
- `UPDATE_MAPPING`: Update a key mapping
- `DELETE_MAPPING`: Delete a key mapping

## Tree Validation

Bonsai provides validation capabilities to ensure the integrity of tree structures:

```java
// Create a validator
BonsaiTreeValidator validator = new ComponentBonsaiTreeValidator();

// Validate a tree structure
ValidationResult result = validator.validate(treeKnot);

if (!result.isValid()) {
    // Handle validation errors
    List<ValidationError> errors = result.getErrors();
    for (ValidationError error : errors) {
        System.err.println(error.getMessage());
    }
}
```

## Error Handling

Tree operations can throw various exceptions:

- `BonsaiError.KEY_NOT_FOUND`: When trying to access a non-existent key
- `BonsaiError.CYCLE_DETECTED`: When an operation would create a cycle in the tree
- `BonsaiError.TREE_ALREADY_EXIST`: When trying to create a tree that already exists
- `BonsaiError.INVALID_TREE_STRUCTURE`: When a tree structure is invalid

Example of handling errors:

```java
try {
    String knotId = bonsai.getMapping("nonExistentKey");
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.KEY_NOT_FOUND) {
        // Handle key not found error
        System.err.println("Key not found: " + e.getMessage());
    } else {
        // Handle other errors
        throw e;
    }
}
```

## Best Practices

- Use meaningful key names that reflect the purpose of the tree
- Organize related trees with consistent key naming conventions
- Consider using key hierarchies (e.g., "feature.subfeature.config")
- Use delta operations for related changes to maintain consistency
- Validate tree structures before using them in production
- Handle errors appropriately to maintain tree integrity
- Document the expected Context structure for each key to ensure proper usage
