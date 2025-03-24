# Building Trees

Building tree structures is a core part of using Bonsai. This section gives several examples of building 
custom trees to paint the picture of how trees are created. But in practice, in you application, you would do this
through a set of APIs exposed by your application, and potentially, a UI on the other side that is facilitating the 
construction of these tress.

## Basic Tree Building

A typical Bonsai tree consists of:

1. Leaf Knots containing values
2. Decision Knots with conditional Edges
3. Root Knots mapped to keys

Here's a simple example of building a tree for user eligibility:

```java
// Create a Bonsai instance
Bonsai<Context> bonsai = BonsaiBuilder.builder()
    .withBonsaiProperties(BonsaiProperties.builder().build())
    .withEdgeStore(new InMemoryEdgeStore())
    .withKeyTreeStore(new InMemoryKeyTreeStore())
    .withKnotStore(new InMemoryKnotStore())
    .build();

// Create leaf knots with values
Knot eligibleKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of("description", "User is eligible")
);

Knot ineligibleKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(false).build(),
    Map.of("description", "User is ineligible")
);

// Create the root knot
Knot rootKnot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "User eligibility decision point")
);

// Add variations to the root knot
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(eligibleKnot.getId())
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

// Add a default variation (no filters) for ineligible users
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(ineligibleKnot.getId())
    .filters(List.of())
    .build());

// Map a key to the root knot
bonsai.createMapping("userEligibility", rootKnot.getId());
```

## Creating Different Types of Knots

### Value Knots

Value Knots contain primitive values:

```java
// String value
Knot stringKnot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("Hello, World!").build(),
    Map.of("description", "A string knot")
);

// Boolean value
Knot booleanKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of("description", "A boolean knot")
);

// Number value
Knot numberKnot = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(42.5).build(),
    Map.of("description", "A number knot")
);

// JSON value
Knot jsonKnot = bonsai.createKnot(
    ValuedKnotData.builder().jsonValue("{\"name\":\"John\",\"age\":30}").build(),
    Map.of("description", "A JSON knot")
);
```

### List Knots

List Knots contain references to multiple other Knots:

```java
// Create a list knot
Knot listKnot = bonsai.createKnot(
    MultiKnotData.builder()
        .knotIds(List.of("knot1", "knot2", "knot3"))
        .build(),
    Map.of("description", "A list knot")
);
```

### Map Knots

Map Knots contain key-based references to other Knots:

```java
// Create a map knot
Knot mapKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "name", nameKnot.getId(),
            "age", ageKnot.getId(),
            "isActive", activeKnot.getId()
        ))
        .build(),
    Map.of("description", "A map knot")
);
```

## Creating Edges with Different Conditions

Edges (or Variations) connect Knots and define conditional paths through the tree. Here are examples of different types of conditions:

```java
// Equals condition
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.type")
            .operator(Operator.EQUALS)
            .value("premium")
            .build()
    ))
    .build());

// Greater than condition
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN)
            .value(18)
            .build()
    ))
    .build());

// In condition (membership in a list)
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.country")
            .operator(Operator.IN)
            .value(List.of("US", "CA", "UK"))
            .build()
    ))
    .build());

// Contains condition (substring match)
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.email")
            .operator(Operator.CONTAINS)
            .value("@example.com")
            .build()
    ))
    .build());

// Regex condition (pattern match)
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.phone")
            .operator(Operator.REGEX)
            .value("^\\+1-\\d{3}-\\d{3}-\\d{4}$")
            .build()
    ))
    .build());

// Multiple conditions (AND logic)
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
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

// Default condition (no filters)
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(defaultKnot.getId())
    .filters(List.of())
    .build());
```

## Building Complex Nested Structures

Bonsai allows you to create complex nested structures using Map and List Knots:

```java
// Create leaf knots with values
Knot nameKnot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("John Doe").build(),
    Map.of()
);

Knot ageKnot = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(30).build(),
    Map.of()
);

Knot activeKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of()
);

// Create a user profile knot that references the leaf knots
Knot userProfileKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "name", nameKnot.getId(),
            "age", ageKnot.getId(),
            "active", activeKnot.getId()
        ))
        .build(),
    Map.of("description", "User profile data")
);

// Create a list of user profiles
Knot userListKnot = bonsai.createKnot(
    MultiKnotData.builder()
        .knotIds(List.of(userProfileKnot.getId(), otherUserProfileKnot.getId()))
        .build(),
    Map.of("description", "List of user profiles")
);

// Map a key to the user list knot
bonsai.createMapping("users", userListKnot.getId());
```

## Creating Complete Trees

You can create a complete tree structure in one operation using the `createCompleteTree` method:

```java
// Create a TreeKnot structure
TreeKnot treeKnot = TreeKnot.builder()
    .id("rootKnot")
    .data(ValuedKnotData.builder().build())
    .properties(Map.of("description", "Root knot"))
    .edges(List.of(
        TreeEdge.builder()
            .id("edge1")
            .filters(List.of(
                Filter.builder()
                    .path("$.user.age")
                    .operator(Operator.GREATER_THAN_EQUAL)
                    .value(18)
                    .build()
            ))
            .targetKnot(
                TreeKnot.builder()
                    .id("eligibleKnot")
                    .data(ValuedKnotData.builder().booleanValue(true).build())
                    .properties(Map.of("description", "User is eligible"))
                    .build()
            )
            .build(),
        TreeEdge.builder()
            .id("edge2")
            .filters(List.of())
            .targetKnot(
                TreeKnot.builder()
                    .id("ineligibleKnot")
                    .data(ValuedKnotData.builder().booleanValue(false).build())
                    .properties(Map.of("description", "User is ineligible"))
                    .build()
            )
            .build()
    ))
    .build();

// Create the complete tree
Knot rootKnot = bonsai.createCompleteTree(treeKnot);

// Map a key to the root knot
bonsai.createMapping("userEligibility", rootKnot.getId());
```

## Best Practices

- **Plan your tree structure**: Design your tree structure before implementing it
- **Use meaningful IDs and descriptions**: Make your tree easier to understand and maintain
- **Order variations appropriately**: Remember that variations are evaluated in order
- **Include default variations**: Always include a default variation (with no filters) as the last variation on a knot
- **Keep trees shallow**: Minimize the depth of your trees for better performance
- **Reuse knots**: Use the same knot in multiple places to avoid duplication
- **Use appropriate knot types**: Choose the right knot type (value, list, or map) for your data
- **Validate your trees**: Use the validator to ensure your trees are valid
