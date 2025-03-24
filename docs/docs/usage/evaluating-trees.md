# Evaluating Trees

Evaluating trees is the process of traversing a Bonsai tree based on a context to retrieve the appropriate data. This
guide explains how to create contexts, evaluate trees, and process the results.

## Creating a Context

Before evaluating a tree, you need to create a Context object that contains the data needed to evaluate conditions on Edges:

```java
// Create a Context from a JSON string
String json = "{\"user\": {\"age\": 25, \"country\": \"US\", \"type\": \"premium\"}}";
Context context = Context.builder()
    .documentContext(JsonPath.parse(json))
    .build();

// Create a Context from an object
User user = new User("John", 25, "US", "premium");
Context context = Context.builder()
    .documentContext(JsonPath.parse(user))
    .build();

// Create a Context with preferences
Map<String, Knot> preferences = Map.of("homePageConfig", customKnot);
Context context = Context.builder()
    .documentContext(JsonPath.parse(user))
    .preferences(preferences)
    .build();

// Create a Context with metadata
Context context = Context.builder()
    .documentContext(JsonPath.parse(user))
    .metadata(Map.of(
        "requestId", UUID.randomUUID().toString(),
        "timestamp", System.currentTimeMillis(),
        "source", "mobile-app"
    ))
    .build();
```

## Basic Evaluation

To evaluate a tree, you call the `evaluate` method with a key and a context:

```java
// Evaluate the tree
KeyNode result = bonsai.evaluate("userEligibility", context);

// Access the evaluation result
Boolean isEligible = result.getValue().getBooleanValue();
System.out.println("User is eligible: " + isEligible); // true
```

## Accessing Evaluation Results

The result of an evaluation is a `KeyNode` object that reflects the structure of the KnotData in the final Knot reached during traversal:

### Value Results

For ValuedKnotData, you can access the primitive value using NodeUtils helper methods:

```java
// String value
String stringValue = NodeUtils.asString(result.getValue(), "defaultValue");

// Boolean value
Boolean booleanValue = NodeUtils.asBoolean(result.getValue(), false);

// Number value
Double numberValue = NodeUtils.asNumber(result.getValue(), 1);

// JSON value
JsonNode jsonValue = NodeUtils.asJsonNode(result.getValue(), JsonNodeFactory.instance.objectNode());
```

### List Results

For MultiKnotData, you can access the list of KeyNodes:

```java
// Get the list of KeyNodes
List<KeyNode> items = result.getKeyNodeList();

// Process each item
for (KeyNode item : items) {
    // Access the item's value
    String value = NodeUtils.asString(item.getValue(), "");
    System.out.println("Item value: " + value);
}
```

### Map Results

For MapKnotData, you can access the map of string keys to KeyNodes:

```java
// Get the map of KeyNodes
Map<String, KeyNode> properties = result.getKeyNodeMap();

// Access specific properties
String name = NodeUtils.asString(properties.get("name").getValue(), "");
Double age = NodeUtils.asString(properties.get("age").getValue(), "");
Boolean active = NodeUtils.asString(properties.get("active").getValue(), "");

System.out.println("User: " + name + ", Age: " + age + ", Active: " + active);
```

## Flat Evaluation

For debugging or visualization purposes, you can get a flat representation of the evaluated tree:

```java
// Get a flat representation of the evaluated tree
FlatTreeRepresentation flatTree = bonsai.evaluateFlat("userEligibility", context);

// Access the flat tree information
String rootKnotId = flatTree.getRootKnotId();
Map<String, Knot> knots = flatTree.getKnots();
Map<String, Edge> edges = flatTree.getEdges();
List<String> traversedEdgeIds = flatTree.getTraversedEdgeIds();
```

## Contextual Preferences

The Context can include a `preferences` map, which maps keys to specific Knots. When evaluating a key, if the key is found in the preferences map, Bonsai will return the associated Knot directly, bypassing the normal tree traversal:

```java
// Get a custom knot for a specific user
Knot customHomePageKnot = bonsai.getKnot("custom-home-page");

// Create a context with preferences
Map<String, Knot> preferences = Map.of("homePage", customHomePageKnot);
Context context = Context.builder()
    .documentContext(JsonPath.parse(userData))
    .preferences(preferences)
    .build();

// Evaluate the tree - will return the custom knot directly
KeyNode result = bonsai.evaluate("homePage", context);
```

This is useful for scenarios like user-specific overrides of default configurations.

## Custom Context Implementation

You can create custom Context implementations for specific application needs:

```java
public class UserContext extends Context {
    private User user;
    
    public UserContext(User user) {
        super(JsonPath.parse(user));
        this.user = user;
    }
    
    // Custom methods to access user data
    public int getUserAge() {
        return user.getAge();
    }
    
    public String getUserCountry() {
        return user.getCountry();
    }
}

// Create a custom context
User user = new User("John", 25, "US", "premium");
UserContext context = new UserContext(user);

// Evaluate the tree with the custom context
KeyNode result = bonsai.evaluate("userEligibility", context);
```

## Evaluation Process

Understanding the evaluation process can help you design effective tree structures:

1. Bonsai starts at the root Knot for the specified key
2. For each Knot, it evaluates the Edges in order
3. For each Edge, it evaluates all filters against the Context
4. If all filters on an Edge evaluate to true, it follows that Edge to the target Knot
5. If no Edge's filters match, the traversal stops at the current Knot
6. The process continues until it reaches a Knot with no matching Edges
7. The final Knot's data is returned as the result

## Error Handling

Evaluation can throw various exceptions:

```java
try {
    KeyNode result = bonsai.evaluate("nonExistentKey", context);
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

## Performance Considerations

- **Context Size**: Large Context objects can impact performance
- **Tree Depth**: Deep trees require more traversal steps
- **Filter Complexity**: Complex JsonPath expressions can be expensive to evaluate
- **Caching**: Consider caching evaluation results for frequently used keys and contexts

## Best Practices

- **Structure your Context data** in a way that makes JsonPath expressions simple and intuitive
- **Use consistent data structures** across similar contexts for easier maintenance
- **Document the expected Context structure** for each key to ensure proper usage
- **Consider performance implications** of large Context objects
- **Handle evaluation errors** appropriately in your application
- **Use preferences judiciously** to avoid bypassing the rule engine unnecessarily
