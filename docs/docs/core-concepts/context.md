# Context

Context is the evaluation entity against which the Bonsai tree is traversed. It contains the data needed to evaluate conditions on Edges and determine which paths to follow during tree traversal.

## Core Functionality

The Context serves several key functions in the Bonsai system:

1. **Provides Data for Condition Evaluation**: Contains the data that Edge conditions are evaluated against
2. **Enables JsonPath Evaluation**: Wraps a JsonPath DocumentContext for accessing data using path expressions
3. **Supports Contextual Preferences**: Can include preferences that override normal tree traversal

## Structure

At its core, the Context class contains:

```java
public class Context {
    private final DocumentContext documentContext;
    private final JsonNode contextAsJsonNode;
    private final Map<String, Knot> preferences;
    // ...
}
```

The `DocumentContext` is a JsonPath object that wraps the data being evaluated. This allows Edge conditions to use JsonPath expressions to access specific parts of the data.

The `contextAsJsonNode` contains the data being evaluated in JSON format. 
This comes in handy while evaluating Edge conditions that require raw JSON data such as [Hope](https://github.com/santanusinha/hope) expressions.

## JsonPath Integration

Bonsai uses [JsonPath](https://github.com/json-path/JsonPath) for evaluating conditions against the Context. JsonPath provides a way to navigate JSON structures using path expressions, similar to how XPath is used for XML.

For example, a path expression like `$.user.age` would access the age property of the user object in the Context data.

## Creating a Context

There are several ways to create a Context:

```java
// Create a Context from a JSON string
String json = "{\"user\": {\"age\": 25, \"country\": \"US\"}}";
Context context = Context.builder()
    .documentContext(JsonPath.parse(json))
    .build();

// Create a Context from an object
User user = new User("John", 25, "US");
Context context = Context.builder()
    .documentContext(JsonPath.parse(user))
    .build();

// Create a Context with preferences
Map<String, Knot> preferences = Map.of("homePageConfig", customKnot);
Context context = Context.builder()
    .documentContext(JsonPath.parse(user))
    .preferences(preferences)
    .build();
```

## Contextual Preferences

The Context can include a `preferences` map, which maps keys to specific Knots. When evaluating a key, if the key is found in the preferences map, Bonsai will return the associated Knot directly, bypassing the normal tree traversal.

This is useful for scenarios like user-specific overrides of default configurations:

```java
// Get the current version of a knot
Knot knot = bonsai.getKnot("home_page");
Map<String, Knot> preferences = preferenceStore.get(userId); // this storage will have to be implemented by you
Context context = new Context(JsonPath.parse(userData), preferences); 
bonsai.evaluate("home_page", context);
```

## Custom Context Implementation

You can create custom Context implementations for specific application needs:

```java
public class UserContext extends Context {
    private User user;
    private Map<String, Object> additionalData;
    
    // Implement methods to access user data for condition evaluation
    public int getUserAge() {
        return user.getAge();
    }
    
    public String getUserCountry() {
        return user.getCountry();
    }
}
```

## Best Practices

- Structure your Context data in a way that makes JsonPath expressions simple and intuitive
- Use consistent data structures across similar contexts for easier maintenance
- Consider performance implications of large Context objects
- Use preferences judiciously to avoid bypassing the rule engine unnecessarily
- Document the expected Context structure for each tree to ensure proper usage
- Set the JsonNode context if your Edge conditions have filters using [Hope](https://github.com/santanusinha/hope) expressions for better performance.
