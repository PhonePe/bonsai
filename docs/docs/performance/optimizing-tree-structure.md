# Optimizing Tree Structure

The structure of your Bonsai trees has a significant impact on performance. This guide provides recommendations for designing efficient tree structures that optimize evaluation performance.

## Tree Depth

The depth of a tree is the number of edges that need to be traversed from the root to the deepest leaf. Deep trees can lead to longer evaluation times:

- **Keep trees shallow**: Aim for a maximum depth of 5-10 levels
- **Use flatter structures**: Consider reorganizing deep hierarchies into flatter structures
- **Balance the tree**: Distribute decision points evenly to avoid heavily skewed trees

## Edge Count and Order

The number of edges per knot and their order affects how quickly Bonsai can find the right path:

- **Limit edges per knot**: Keep the number of edges per knot below 10-20
- **Order edges by likelihood**: Put the most likely matches first to reduce the number of evaluations
- **Use default edges wisely**: Always include a default edge (with no filters) as the last edge on a knot

Example of ordering edges by likelihood:

```java
// Add the most common case first
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(commonCaseKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.type")
            .operator(Operator.EQUALS)
            .value("regular")
            .build()
    ))
    .build());

// Add less common cases next
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(premiumCaseKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.type")
            .operator(Operator.EQUALS)
            .value("premium")
            .build()
    ))
    .build());

// Add the default case last
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(defaultCaseKnot.getId())
    .filters(List.of())
    .build());
```

## Filter Complexity

The complexity of filter conditions affects evaluation time:

- **Use simple filters**: Keep filter conditions simple and focused
- **Limit the number of filters per edge**: Keep the number of filters per edge below 5-10
- **Avoid complex JsonPath expressions**: Complex expressions can be expensive to evaluate
- **Use efficient operators**: Some operators are more efficient than others

Example of simplifying filters:

```java
// Instead of this complex filter
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.preferences.settings.theme.color")
            .operator(Operator.EQUALS)
            .value("dark")
            .build()
    ))
    .build());

// Consider restructuring your context data
// And using a simpler filter
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(targetKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.theme")
            .operator(Operator.EQUALS)
            .value("dark")
            .build()
    ))
    .build());
```

## Tree Structure Patterns

Certain tree structure patterns can lead to better performance:

### Decision Trees

For simple decision logic, use a traditional decision tree structure:

```
Root Knot
├── Edge: If condition A → Knot A
├── Edge: If condition B → Knot B
└── Edge: Default → Default Knot
```

This structure is efficient for simple decisions with a small number of outcomes.

### Two-Level Trees

For configuration selection, consider a two-level tree structure:

```
Root Knot
├── Edge: If condition A → Config A Knot
├── Edge: If condition B → Config B Knot
└── Edge: Default → Default Config Knot
```

This structure is efficient for selecting one of several configurations based on conditions.

### Segmentation Trees

For user segmentation, consider a segmentation tree structure:

```
Root Knot
├── Edge: If user is premium → Premium Segment Knot
│   ├── Edge: If user is new → New Premium User Knot
│   └── Edge: Default → Regular Premium User Knot
├── Edge: If user is free → Free Segment Knot
│   ├── Edge: If user is new → New Free User Knot
│   └── Edge: Default → Regular Free User Knot
└── Edge: Default → Unknown Segment Knot
```

This structure efficiently segments users into groups and subgroups.

### Feature Flag Trees

For feature flags, consider a feature flag tree structure:

```
Root Knot (Map)
├── "feature1" → Feature 1 Knot
│   ├── Edge: If user is in beta → Enabled Knot
│   └── Edge: Default → Disabled Knot
├── "feature2" → Feature 2 Knot
│   ├── Edge: If user is premium → Enabled Knot
│   └── Edge: Default → Disabled Knot
└── "feature3" → Feature 3 Knot
    ├── Edge: If user is in region A → Enabled Knot
    └── Edge: Default → Disabled Knot
```

This structure efficiently manages multiple feature flags.

## Reusing Knots

Reusing knots can reduce memory usage and improve cache efficiency:

- **Identify common subtrees**: Look for patterns that repeat in your tree structure
- **Extract common subtrees**: Create separate knots for common subtrees
- **Reference common knots**: Use the same knot in multiple places

Example of reusing knots:

```java
// Create common knots
Knot enabledKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of("description", "Feature enabled")
);

Knot disabledKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(false).build(),
    Map.of("description", "Feature disabled")
);

// Reuse the common knots in multiple places
bonsai.addVariation(feature1Knot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.beta")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());

bonsai.addVariation(feature1Knot.getId(), Variation.builder()
    .knotId(disabledKnot.getId())
    .filters(List.of())
    .build());

bonsai.addVariation(feature2Knot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.premium")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());

bonsai.addVariation(feature2Knot.getId(), Variation.builder()
    .knotId(disabledKnot.getId())
    .filters(List.of())
    .build());
```

## Context Structure

The structure of your context data can also affect performance:

- **Keep context data small**: Include only the necessary data in the context
- **Structure context data for efficient access**: Organize data to minimize the complexity of JsonPath expressions
- **Flatten deeply nested structures**: Consider flattening deeply nested structures to simplify JsonPath expressions

Example of restructuring context data:

```java
// Instead of this deeply nested structure
{
    "user": {
        "preferences": {
            "settings": {
                "theme": {
                    "color": "dark"
                }
            }
        }
    }
}

// Consider this flatter structure
{
    "user": {
        "theme": "dark"
    }
}
```

## Measuring and Testing

To optimize your tree structure effectively:

- **Measure baseline performance**: Establish a baseline for comparison
- **Test with realistic data**: Use representative data for testing
- **Isolate changes**: Test one optimization at a time
- **Measure the impact**: Quantify the performance improvement of each optimization
- **Consider trade-offs**: Balance performance with maintainability and readability

## Best Practices

- **Start simple**: Begin with a simple tree structure and optimize as needed
- **Focus on hot paths**: Optimize the most frequently used paths first
- **Consider the full lifecycle**: Balance creation, evaluation, and maintenance costs
- **Document your design decisions**: Document why you structured the tree a certain way
- **Review and refactor**: Periodically review and refactor your tree structure
- **Test thoroughly**: Test your optimizations with various inputs and edge cases
