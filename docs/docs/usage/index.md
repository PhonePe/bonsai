# Usage

This section provides practical examples and guidance on how to use Bonsai in your applications. 

## Getting Started

To start using Bonsai in your Java application, you'll need to:

1. [Install Bonsai](installation.md) by adding it to your project dependencies
2. [Create a Bonsai instance](creating-bonsai.md) with appropriate configuration
3. [Build trees](building-trees.md) to represent your rule structure
4. [Evaluate trees](evaluating-trees.md) against a context to get results
5. [Apply delta operations](delta-operations.md) to modify trees as needed

## Basic Workflow

The typical workflow for using Bonsai involves:

1. **Setup**: Create a Bonsai instance with appropriate storage implementations and configuration
2. **Tree Building**: Create Knots, connect them with Edges, and map keys to root Knots
3. **Evaluation**: Create a Context with the data to evaluate, and evaluate a key against it
4. **Result Processing**: Process the evaluation result (KeyNode) to extract the data
5. **Maintenance**: Update the tree structure as needed over time

## Example: User Eligibility Check

Here's a simple example of using Bonsai to check if a user is eligible for a service:

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

bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(ineligibleKnot.getId())
    .filters(List.of())
    .build());

// Map a key to the root knot
bonsai.createMapping("userEligibility", rootKnot.getId());

// Create a context for evaluation
String json = "{\"user\": {\"age\": 25, \"country\": \"US\"}}";
Context context = Context.builder()
    .documentContext(JsonPath.parse(json))
    .build();

// Evaluate the tree
KeyNode result = bonsai.evaluate("userEligibility", context);

// Access the evaluation result
Boolean isEligible = result.getValue().getBooleanValue();
System.out.println("User is eligible: " + isEligible); // true
```
