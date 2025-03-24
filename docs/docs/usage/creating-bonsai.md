# Creating a Bonsai Instance

Creating a properly configured Bonsai instance is the first step in using the Bonsai rule engine. This guide explains
the various configuration options and how to create a Bonsai instance tailored to your needs.

## Using the BonsaiBuilder

The recommended way to create a Bonsai instance is using the `BonsaiBuilder` class. In a production environment, the 
properties could come from an external configuration.

```java
Bonsai<Context> bonsai = BonsaiBuilder.builder()
    .withBonsaiProperties(
        BonsaiProperties.builder()
            .maxAllowedVariationsPerKnot(10)  // Limit variations per knot
            .maxAllowedConditionsPerEdge(10)  // Limit conditions per variation
            .mutualExclusivitySettingTurnedOn(false)
            .build())
    .withBonsaiIdGenerator(new UUIDGenerator())    // Custom ID generation strategy, which you can create
    .withEdgeStore(new InMemoryEdgeStore())        // Edge storage implementation
    .withKeyTreeStore(new InMemoryKeyTreeStore())  // Key-Tree mapping storage
    .withKnotStore(new InMemoryKnotStore())        // Knot storage implementation
    .build();
```

## Configuration Options

### BonsaiProperties

`BonsaiProperties` controls the behavior of the Bonsai instance:

```java
BonsaiProperties properties = BonsaiProperties.builder()
    .maxAllowedVariationsPerKnot(10)  // Maximum number of variations (edges) per knot
    .maxAllowedConditionsPerEdge(10)  // Maximum number of conditions per edge
    .mutualExclusivitySettingTurnedOn(false)  // Whether edge conditions must be mutually exclusive
    .build();
```

Key properties include:

- **maxAllowedVariationsPerKnot**: Limits the number of outgoing edges from a knot
- **maxAllowedConditionsPerEdge**: Limits the number of conditions on an edge
- **mutualExclusivitySettingTurnedOn**: When true, ensures that at most one edge's conditions can match for any given context

### ID Generation

The `BonsaiIdGenerator` determines how IDs are generated for knots and edges:

```java

// Or create a custom ID generator
BonsaiIdGenerator customGenerator = new BonsaiIdGenerator() {
    @Override
    public String generateId() {
        return "custom-" + System.currentTimeMillis();
    }
};
```

### Storage Implementations

Bonsai requires three storage implementations:

1. **KnotStore**: Stores knot data
2. **EdgeStore**: Stores edge data
3. **KeyTreeStore**: Stores mappings between keys and root knots

#### In-Memory Storage

For development or simple applications, you can use the in-memory implementations:

```java
KnotStore knotStore = new InMemoryKnotStore();
EdgeStore edgeStore = new InMemoryEdgeStore();
KeyTreeStore keyTreeStore = new InMemoryKeyTreeStore();
```

#### Custom Storage Implementations

For production use, you'll likely want to implement custom storage backends. You should definitely go through
the [Storage](../storage.md) section for more details.
Here's an example Redis-based knot store:

```java
// Example Redis-based knot store
public class RedisKnotStore implements KnotStore {
    private final RedisClient redisClient;
    
    public RedisKnotStore(RedisClient redisClient) {
        this.redisClient = redisClient;
    }
    
    @Override
    public Knot getKnot(String knotId) {
        String json = redisClient.get("knot:" + knotId);
        return json != null ? deserialize(json, Knot.class) : null;
    }
    
    @Override
    public void putKnot(Knot knot) {
        redisClient.set("knot:" + knot.getId(), serialize(knot));
    }
    
    // Implement other methods...
}
```

## Type Parameters

Bonsai is a generic class that can be parameterized with the type of context you'll be using:

```java
// Using the default Context class
Bonsai<Context> bonsai = BonsaiBuilder.builder()
    // ... configuration ...
    .build();

// Using a custom Context subclass
Bonsai<UserContext> bonsai = BonsaiBuilder.builder()
    // ... configuration ...
    .build();
```

## Complete Example

Here's a complete example of creating a Bonsai instance with custom configuration:

```java
// Create a Bonsai instance with custom configuration
Bonsai<UserContext> bonsai = BonsaiBuilder.builder()
    .withBonsaiProperties(
        BonsaiProperties.builder()
            .maxAllowedVariationsPerKnot(20)
            .maxAllowedConditionsPerEdge(15)
            .mutualExclusivitySettingTurnedOn(true)
            .build())
    .withEdgeStore(new RedisEdgeStore(redisClient))
    .withKeyTreeStore(new RedisKeyTreeStore(redisClient))
    .withKnotStore(new RedisKnotStore(redisClient))
    .build();
```

## Best Practices

- **Choose appropriate limits**: Set `maxAllowedVariationsPerKnot` and `maxAllowedConditionsPerEdge` based on your application's needs
- **Consider mutual exclusivity**: Enable `mutualExclusivitySettingTurnedOn` if you want to ensure deterministic behavior
- **Use meaningful IDs**: Consider a custom ID generator that produces readable IDs
- **Select the right storage**: Use in-memory storage for development and testing, but implement persistent storage for production
- **Create a singleton**: In most applications, you'll want to create a single Bonsai instance and reuse it
