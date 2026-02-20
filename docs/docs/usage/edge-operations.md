# Edge Operations

Bonsai provides a comprehensive set of operations for managing Edges (also called Variations) in the tree structure. These operations allow you to create, read, update, and delete Edges, as well as manage their properties and conditions.

## Creating Edges

### Create an Edge Directly

```java
// Create an Edge directly
Edge edge = bonsai.createEdge(Edge.builder()
    .id("customEdgeId") // Optional, Bonsai can generate an ID
    .knotId("targetKnotId")
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(18)
            .build()
    ))
    .properties(Map.of("description", "Age verification edge"))
    .build());
```

### Add a Variation to a Knot

```java
// Add a Variation to a Knot
Edge edge = bonsai.addVariation("sourceKnotId", Variation.builder()
    .knotId("targetKnotId")
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
```

## Reading Edges

### Check if an Edge Exists

```java
boolean exists = bonsai.containsEdge("edgeId");
```

### Get an Edge by ID

```java
Edge edge = bonsai.getEdge("edgeId");
```

### Get Multiple Edges by IDs

```java
Set<String> edgeIds = Set.of("edge1", "edge2", "edge3");
Map<String, Edge> edges = bonsai.getAllEdges(edgeIds);
```

### Get Edges for a Knot

```java
List<Edge> edges = bonsai.getEdgesForKnot("knotId");
```

## Updating Edges

### Update a Variation

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

### Update an Edge's Properties

```java
// Update an Edge's properties
Map<String, Object> newProperties = new HashMap<>(edge.getProperties());
newProperties.put("lastUpdated", System.currentTimeMillis());
newProperties.put("updatedBy", "user123");
newProperties.put("version", edge.getVersion());

Edge oldEdge = bonsai.updateEdgeProperties("edgeId", newProperties);
```

## Deleting Edges

### Delete a Variation

```java
// Delete a Variation (without recursive deletion)
TreeEdge deletedEdge = bonsai.deleteVariation("knotId", "edgeId", false);

// Delete a Variation and all its children (recursive deletion)
TreeEdge deletedEdge = bonsai.deleteVariation("knotId", "edgeId", true);
```

The `deleteVariation` method returns a `TreeEdge` object representing the deleted subtree. This can be useful for auditing or potentially restoring the deleted structure.

### Unlink a Variation

```java
// Unlink a Variation (remove the Edge but keep the target Knot)
bonsai.unlinkVariation("knotId", "edgeId");
```

## Edge Ordering

Edges on a Knot are evaluated in the order they are added. You can reorder Edges to change the evaluation priority:

```java
// Reorder Edges on a Knot
List<String> newOrder = List.of("edge3", "edge1", "edge2");
bonsai.reorderEdges("knotId", newOrder);
```

## Filter Operations

Filters (or conditions) define when an Edge should be followed during tree traversal. Here are some examples of creating filters:

```java
// Equals filter
Filter equalsFilter = Filter.builder()
    .path("$.user.type")
    .operator(Operator.EQUALS)
    .value("premium")
    .build();

// Greater than filter
Filter greaterThanFilter = Filter.builder()
    .path("$.user.age")
    .operator(Operator.GREATER_THAN)
    .value(18)
    .build();

// In filter (membership in a list)
Filter inFilter = Filter.builder()
    .path("$.user.country")
    .operator(Operator.IN)
    .value(List.of("US", "CA", "UK"))
    .build();

// Contains filter (substring match)
Filter containsFilter = Filter.builder()
    .path("$.user.email")
    .operator(Operator.CONTAINS)
    .value("@example.com")
    .build();

// Regex filter (pattern match)
Filter regexFilter = Filter.builder()
    .path("$.user.phone")
    .operator(Operator.REGEX)
    .value("^\\+1-\\d{3}-\\d{3}-\\d{4}$")
    .build();

// Hope filter (Hope expression)
Filter hopeFilter = Filter.builder()
    .field("$.user.age")
    .operator(Operator.HOPE)
    .value("\"$.user.age\" > 18")
    .build();
```

## Error Handling

Edge operations can throw various exceptions:

- `BonsaiError.EDGE_ABSENT`: When trying to access a non-existent Edge
- `BonsaiError.KNOT_ABSENT`: When referencing a non-existent Knot
- `BonsaiError.CYCLE_DETECTED`: When an operation would create a cycle in the tree
- `BonsaiError.MAX_CONDITIONS_EXCEEDED`: When adding too many conditions to an Edge
- `BonsaiError.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR`: When Edge conditions violate mutual exclusivity

Example of handling errors:

```java
try {
    Edge edge = bonsai.getEdge("nonExistentEdgeId");
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.EDGE_NOT_FOUND) {
        // Handle edge not found error
        System.err.println("Edge not found: " + e.getMessage());
    } else {
        // Handle other errors
        throw e;
    }
}
```

## Best Practices

- Order Edges from most specific to most general
- Use a default Edge (with no filters) as the last Edge on a Knot to handle all remaining cases
- Keep filter conditions simple and focused
- Consider the performance impact of complex JsonPath expressions
- Use meaningful properties to document the purpose of each Edge
- Be mindful of the maximum number of conditions per Edge (configured in BonsaiProperties)
- Consider mutual exclusivity settings when designing Edge conditions
