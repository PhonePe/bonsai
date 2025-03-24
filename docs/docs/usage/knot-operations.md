# Knot Operations

Bonsai provides a comprehensive set of operations for managing Knots in the tree structure. These operations allow you to create, read, update, and delete Knots, as well as manage their properties and data.

## Creating Knots

### Create a Knot with Data

```java
// Create a Knot with a string value
Knot stringKnot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("Hello, World!").build(),
    Map.of("description", "A simple string knot")
);

// Create a Knot with a boolean value
Knot booleanKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of("description", "A boolean knot")
);

// Create a Knot with a number value
Knot numberKnot = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(42.5).build(),
    Map.of("description", "A number knot")
);

// Create a Knot with references to other Knots (Map)
Knot mapKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "string", stringKnot.getId(),
            "boolean", booleanKnot.getId(),
            "number", numberKnot.getId()
        ))
        .build(),
    Map.of("description", "A map knot")
);

// Create a Knot with references to other Knots (List)
Knot listKnot = bonsai.createKnot(
    MultiKnotData.builder()
        .knotIds(List.of(stringKnot.getId(), booleanKnot.getId(), numberKnot.getId()))
        .build(),
    Map.of("description", "A list knot")
);
```

## Reading Knots

### Check if a Knot Exists

```java
boolean exists = bonsai.containsKnot("knotId");
```

### Get a Knot by ID

```java
Knot knot = bonsai.getKnot("knotId");
```

### Get Multiple Knots by IDs

```java
Set<String> knotIds = Set.of("knot1", "knot2", "knot3");
Map<String, Knot> knots = bonsai.getAllKnots(knotIds);
```

## Updating Knots

### Update a Knot's Data

```java
// Update a Knot's data
Knot oldKnot = bonsai.updateKnotData(
    "knotId",
    ValuedKnotData.builder().stringValue("Updated value").build(),
    Map.of("description", "Updated knot", "version", knot.getVersion())
);
```

The `updateKnotData` method returns the previous version of the Knot before the update.

### Update a Knot's Properties

```java
// Update a Knot's properties
Map<String, Object> newProperties = new HashMap<>(knot.getProperties());
newProperties.put("lastUpdated", System.currentTimeMillis());
newProperties.put("updatedBy", "user123");
newProperties.put("version", knot.getVersion());

Knot oldKnot = bonsai.updateKnotProperties("knotId", newProperties);
```

## Deleting Knots

### Delete a Knot

```java
// Delete a Knot (without recursive deletion)
TreeKnot deletedTree = bonsai.deleteKnot("knotId", false);

// Delete a Knot and all its children (recursive deletion)
TreeKnot deletedTree = bonsai.deleteKnot("knotId", true);
```

The `deleteKnot` method returns a `TreeKnot` object representing the deleted subtree. This can be useful for auditing or potentially restoring the deleted structure.

## Managing Variations (Edges)

### Add a Variation to a Knot

```java
// Add a Variation to a Knot
Edge edge = bonsai.addVariation("knotId", Variation.builder()
    .knotId("targetKnotId")
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(18)
            .build()
    ))
    .build());
```

### Update a Variation on a Knot

```java
// Update a Variation on a Knot
Edge updatedEdge = bonsai.updateVariation("knotId", "edgeId", Variation.builder()
    .knotId("newTargetKnotId")
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(21)
            .build()
    ))
    .build());
```

### Delete a Variation from a Knot

```java
// Delete a Variation (without recursive deletion)
TreeEdge deletedEdge = bonsai.deleteVariation("knotId", "edgeId", false);

// Delete a Variation and all its children (recursive deletion)
TreeEdge deletedEdge = bonsai.deleteVariation("knotId", "edgeId", true);
```

### Unlink a Variation

```java
// Unlink a Variation (remove the Edge but keep the target Knot)
bonsai.unlinkVariation("knotId", "edgeId");
```

## Error Handling

Knot operations can throw various exceptions:

- `BonsaiError.KNOT_NOT_FOUND`: When trying to access a non-existent Knot
- `BonsaiError.VERSION_MISMATCH`: When trying to update a Knot with an outdated version
- `BonsaiError.CYCLE_DETECTED`: When an operation would create a cycle in the tree
- `BonsaiError.MAX_VARIATIONS_EXCEEDED`: When adding too many variations to a Knot

Example of handling errors:

```java
try {
    Knot knot = bonsai.getKnot("nonExistentKnotId");
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.KNOT_NOT_FOUND) {
        // Handle knot not found error
        System.err.println("Knot not found: " + e.getMessage());
    } else {
        // Handle other errors
        throw e;
    }
}
```

## Best Practices

- Use meaningful IDs or let Bonsai generate them for you
- Include descriptive properties to make Knots easier to understand and manage
- Consider versioning implications when updating Knots
- Use recursive deletion carefully, as it can remove large portions of the tree
- Handle errors appropriately to maintain tree consistency
