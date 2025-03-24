# Feature Flagging System

Feature flags (also known as feature toggles or feature switches) are a powerful technique for modifying system behavior without changing code. Bonsai's tree-based rule engine is an excellent fit for implementing a sophisticated feature flagging system.

## Why Use Bonsai for Feature Flags?

Bonsai offers several advantages for feature flagging:

- **Conditional Logic**: Feature flags can be based on various factors like user attributes, environment, or time
- **Hierarchical Structure**: Features can be organized in a hierarchical structure
- **Dynamic Updates**: Feature flags can be updated without code changes or redeployment
- **Versioning**: Changes to feature flags can be tracked and potentially reverted
- **Performance**: Efficient evaluation for high-throughput scenarios

## Basic Feature Flag Implementation

Here's a simple example of implementing feature flags with Bonsai:

```java
// Create a Bonsai instance
Bonsai<Context> bonsai = BonsaiBuilder.builder()
    .withBonsaiProperties(BonsaiProperties.builder().build())
    .withEdgeStore(new InMemoryEdgeStore())
    .withKeyTreeStore(new InMemoryKeyTreeStore())
    .withKnotStore(new InMemoryKnotStore())
    .build();

// Create enabled and disabled knots
Knot enabledKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of("description", "Feature enabled")
);

Knot disabledKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(false).build(),
    Map.of("description", "Feature disabled")
);

// Create a feature flag knot
Knot newUIFeatureKnot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "New UI Feature Flag")
);

// Enable for beta users
bonsai.addVariation(newUIFeatureKnot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.betaProgram")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());

// Default to disabled
bonsai.addVariation(newUIFeatureKnot.getId(), Variation.builder()
    .knotId(disabledKnot.getId())
    .filters(List.of())
    .build());

// Map to a key
bonsai.createMapping("features.newUI", newUIFeatureKnot.getId());

// Evaluate the feature flag
Context context = Context.builder()
    .documentContext(JsonPath.parse("{\"user\": {\"betaProgram\": true}}"))
    .build();
KeyNode result = bonsai.evaluate("features.newUI", context);
boolean isEnabled = result.getValue().getBooleanValue(); // true
```

## Advanced Feature Flag Implementation

For a more sophisticated feature flagging system, you can implement:

### Multiple Conditions

Enable features based on multiple conditions:

```java
// Enable for beta users in specific regions
bonsai.addVariation(newUIFeatureKnot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.betaProgram")
            .operator(Operator.EQUALS)
            .value(true)
            .build(),
        Filter.builder()
            .path("$.user.region")
            .operator(Operator.IN)
            .value(List.of("US-WEST", "EU-CENTRAL"))
            .build()
    ))
    .build());
```

### Percentage Rollouts

Enable features for a percentage of users:

```java
// Enable for 10% of users
bonsai.addVariation(newUIFeatureKnot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.id")
            .operator(Operator.MODULO)
            .value(10)
            .build(),
        Filter.builder()
            .path("$.user.id.modulo")
            .operator(Operator.LESS_THAN)
            .value(1)
            .build()
    ))
    .build());
```

### Time-Based Rollouts

Enable features based on time:

```java
// Enable during business hours
bonsai.addVariation(newUIFeatureKnot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.time.hour")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(9)
            .build(),
        Filter.builder()
            .path("$.request.time.hour")
            .operator(Operator.LESS_THAN)
            .value(17)
            .build()
    ))
    .build());
```

### Feature Dependencies

Enable features based on other features:

```java
// Enable if another feature is enabled
bonsai.addVariation(newUIFeatureKnot.getId(), Variation.builder()
    .knotId(enabledKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.features.parentFeature")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());
```

### Feature Configuration

Provide configuration for features:

```java
// Create configuration knots
Knot defaultConfigKnot = bonsai.createKnot(
    ValuedKnotData.builder().jsonValue("{\"theme\":\"light\",\"layout\":\"standard\"}").build(),
    Map.of("description", "Default UI configuration")
);

Knot betaConfigKnot = bonsai.createKnot(
    ValuedKnotData.builder().jsonValue("{\"theme\":\"dark\",\"layout\":\"compact\"}").build(),
    Map.of("description", "Beta UI configuration")
);

// Create a configuration knot
Knot uiConfigKnot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "UI Configuration")
);

// Beta configuration for beta users
bonsai.addVariation(uiConfigKnot.getId(), Variation.builder()
    .knotId(betaConfigKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.betaProgram")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());

// Default configuration
bonsai.addVariation(uiConfigKnot.getId(), Variation.builder()
    .knotId(defaultConfigKnot.getId())
    .filters(List.of())
    .build());

// Map to a key
bonsai.createMapping("features.uiConfig", uiConfigKnot.getId());
```

## Hierarchical Feature Flags

Organize feature flags in a hierarchical structure:

```java
// Create a map of feature flags
Knot featureFlagsKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "newUI", newUIFeatureKnot.getId(),
            "newCheckout", checkoutFeatureKnot.getId(),
            "newSearch", searchFeatureKnot.getId()
        ))
        .build(),
    Map.of("description", "Feature Flags")
);

// Map to a key
bonsai.createMapping("features", featureFlagsKnot.getId());

// Evaluate all feature flags
KeyNode result = bonsai.evaluate("features", context);
Map<String, KeyNode> featureFlags = result.getKeyNodeMap();
boolean newUIEnabled = featureFlags.get("newUI").getValue().getBooleanValue();
boolean newCheckoutEnabled = featureFlags.get("newCheckout").getValue().getBooleanValue();
boolean newSearchEnabled = featureFlags.get("newSearch").getValue().getBooleanValue();
```

## Feature Flag Service

In a real application, you would typically implement a service layer for feature flags:

```java
@Service
public class FeatureFlagService {
    private final Bonsai<Context> bonsai;
    
    @Autowired
    public FeatureFlagService(Bonsai<Context> bonsai) {
        this.bonsai = bonsai;
    }
    
    public boolean isFeatureEnabled(String featureKey, User user) {
        // Create a context with user data
        Context context = Context.builder()
            .documentContext(JsonPath.parse(user))
            .build();
        
        // Evaluate the feature flag
        try {
            KeyNode result = bonsai.evaluate("features." + featureKey, context);
            return result.getValue().getBooleanValue();
        } catch (BonsaiError e) {
            // Handle errors (e.g., feature flag not found)
            return false;
        }
    }
    
    public <T> T getFeatureConfig(String featureKey, User user, Class<T> configClass) {
        // Create a context with user data
        Context context = Context.builder()
            .documentContext(JsonPath.parse(user))
            .build();
        
        // Evaluate the feature configuration
        try {
            KeyNode result = bonsai.evaluate("features." + featureKey + ".config", context);
            String json = result.getValue().getJsonValue();
            return new ObjectMapper().readValue(json, configClass);
        } catch (Exception e) {
            // Handle errors (e.g., feature config not found, invalid JSON)
            return null;
        }
    }
}
```

## Feature Flag Management UI

For a complete feature flagging system, you would typically implement a management UI that allows non-technical users to:

- Create and manage feature flags
- Define conditions for enabling features
- Monitor feature flag usage
- A/B test features
- Gradually roll out features

While implementing a UI is beyond the scope of this guide, Bonsai's API provides all the necessary operations to support such a UI.

## Best Practices

- **Use a consistent naming convention**: Use a consistent naming convention for feature flag keys
- **Document feature flags**: Document the purpose and conditions of each feature flag
- **Clean up old feature flags**: Remove feature flags that are no longer needed
- **Test feature flags**: Test both enabled and disabled states of feature flags
- **Monitor feature flag usage**: Track which features are enabled for which users
- **Consider performance**: Optimize feature flag evaluation for high-throughput scenarios
- **Implement caching**: Cache feature flag results to improve performance
- **Handle errors gracefully**: Provide sensible defaults when feature flags cannot be evaluated
