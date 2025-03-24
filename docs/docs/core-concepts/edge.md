# Edge/Variation

Edges (also called Variations) are a fundamental component of the Bonsai tree structure. They connect Knots and define conditional paths through the tree, enabling the rule engine's decision-making capabilities.

## Characteristics

- Each Edge has a unique identifier (`edgeId`)
- Points to a target Knot
- Contains a list of conditions/filters that determine when this path should be taken
- Edges are evaluated in priority order during tree traversal
- Maintains version information for tracking changes
- Can have associated properties for metadata

## Structure

An Edge typically consists of:

```java
public class Edge {
    private String id;
    private String knotId;  // Target Knot ID
    private List<Filter> filters;
    private long version;
    private Map<String, Object> properties;
    // ...
}
```

## Filters/Conditions

Filters (or conditions) define when an Edge should be followed during tree traversal. Each filter consists of:

- **Path**: A JsonPath expression that identifies the data to evaluate in the Context
- **Operator**: The comparison operation to perform (equals, greater than, in, etc.)
- **Value**: The value to compare against

For example:

```java
Filter.builder()
    .path("$.user.age")
    .operator(Operator.GREATER_THAN_EQUAL)
    .value(18)
    .build()
```

This filter checks if the user's age in the Context is greater than or equal to 18.

## Evaluation Order

Edges on a Knot are evaluated in the order they are defined. During tree traversal:

1. Bonsai starts at the root Knot
2. Evaluates each Edge's filters against the Context in order
3. Follows the first Edge whose filters all evaluate to true
4. If no Edge's filters match, the traversal stops at the current Knot

This ordered evaluation allows for fallback logic and default cases.

## Mutual Exclusivity

Bonsai can be configured to enforce mutual exclusivity of Edges, meaning that at most one Edge's conditions can match
for any given Context. This is controlled by the `mutualExclusivitySettingTurnedOn` property in `BonsaiProperties`.

When mutual exclusivity is enabled:

- Bonsai validates that Edge conditions don't overlap at a level
- Level is the depth of the tree where the Edge is present
- Eg: At level 1, the filters for the variation, can only be on a single path (say `$.user.location` == "Bangalore"). At this level
  you would not be able to setup another variation with a filter on `$.user.gender` or any other path. 

## Example

Here's an example of adding an Edge to a Knot:

```java
// Add a variation to the root knot
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
```

This Edge connects the root Knot to the eligible Knot and will be followed if the user is at least 18 years old and from the US, Canada, or the UK.

## Edge Operations

Bonsai provides several operations for working with Edges:

- **Create**: Create a new Edge between Knots
- **Read**: Retrieve an Edge by its ID
- **Update**: Modify an Edge's target Knot or filters
- **Delete**: Remove an Edge from the tree
- **Unlink**: Remove the Edge but keep the target Knot

For more details on these operations, see the [Edge Operations](../operations/edge-operations.md) page.

## Best Practices

- Order Edges from most specific to most general
- Use a default Edge (with no filters) as the last Edge on a Knot to handle all remaining cases
- Keep filter conditions simple and focused
- Consider the performance impact of complex JsonPath expressions
- Use meaningful properties to document the purpose of each Edge
- Be mindful of the maximum number of conditions per Edge (configured in BonsaiProperties)
