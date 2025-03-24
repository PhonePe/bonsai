# KnotData

KnotData represents the content stored within a Knot in the Bonsai tree structure. It defines what information a Knot holds and how it relates to other Knots in the tree.

## Types of KnotData

Bonsai supports three main types of KnotData:

### 1. ValuedKnotData

Contains a primitive value of one of the following types:

- **String**: Text values
- **Boolean**: True/false values
- **Number**: Numeric values (integers or decimals)
- **Bytes**: Binary data
- **JSON**: Structured JSON data

Example:

```java
// Create a ValuedKnotData with a string value
ValuedKnotData stringData = ValuedKnotData.builder()
    .stringValue("Hello, World!")
    .build();

// Create a ValuedKnotData with a boolean value
ValuedKnotData booleanData = ValuedKnotData.builder()
    .booleanValue(true)
    .build();

// Create a ValuedKnotData with a number value
ValuedKnotData numberData = ValuedKnotData.builder()
    .numberValue(42.5)
    .build();

// Create a ValuedKnotData with JSON value
ValuedKnotData jsonData = ValuedKnotData.builder()
    .jsonValue("{\"name\":\"John\",\"age\":30}")
    .build();
```

### 2. MultiKnotData

Contains a list of keys that reference other Knots. This enables one-to-many relationships and allows for list-based structures.

Example:

```java
// Create a MultiKnotData with references to other Knots
MultiKnotData listData = MultiKnotData.builder()
    .knotIds(List.of("knot1", "knot2", "knot3"))
    .build();
```

### 3. MapKnotData

Contains a map of string keys to other Knot references. This enables key-based lookups and allows for structured, nested data.

Example:

```java
// Create a MapKnotData with key-based references to other Knots
MapKnotData mapData = MapKnotData.builder()
    .keyMapping(Map.of(
        "user", userKnot.getId(),
        "preferences", preferencesKnot.getId(),
        "history", historyKnot.getId()
    ))
    .build();
```

## Usage Patterns

Different KnotData types serve different purposes in the Bonsai tree:

- **ValuedKnotData**: Typically used for leaf nodes that contain the actual values or results
- **MultiKnotData**: Used when you need to represent a collection or list of related items
- **MapKnotData**: Used when you need to represent a structured object with named properties

## Nested Structures

One of the powerful features of Bonsai is the ability to create nested structures using MapKnotData and MultiKnotData. For example:

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
```

This creates a structured user profile with name, age, and active status properties.

## Evaluation Results

When evaluating a Bonsai tree, the result is a KeyNode that reflects the structure of the KnotData:

- For ValuedKnotData, the KeyNode contains the primitive value
- For MultiKnotData, the KeyNode contains a list of KeyNodes
- For MapKnotData, the KeyNode contains a map of string keys to KeyNodes

This allows you to access the evaluated data in a structured way that mirrors the original KnotData structure.

## Best Practices

- Choose the appropriate KnotData type based on your data structure needs
- Use ValuedKnotData for simple values and leaf nodes
- Use MultiKnotData for collections of similar items
- Use MapKnotData for structured objects with named properties
- Consider the evaluation performance implications of deeply nested structures
- Keep your data structures consistent across similar Knots for easier maintenance
