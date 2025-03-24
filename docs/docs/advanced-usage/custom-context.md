# Custom Context Implementation

The Context class in Bonsai is designed to be extensible, allowing you to create custom implementations for specific application needs. This guide explains how to create and use custom Context implementations.

## Why Create a Custom Context?

There are several reasons to create a custom Context implementation:

- **Add application-specific data**: Include additional data that's relevant to your application
- **Provide convenience methods**: Add methods that make it easier to access specific data
- **Implement custom evaluation logic**: Override the default evaluation behavior
- **Optimize for specific use cases**: Tailor the Context to your specific needs
- **Encapsulate business logic**: Keep business logic related to context evaluation in one place

## Basic Custom Context

Here's a simple example of a custom Context implementation:

```java
public class UserContext extends Context {
    private User user;
    
    public UserContext(User user) {
        // Initialize the base Context with a JsonPath DocumentContext
        super(JsonPath.parse(user));
        this.user = user;
    }
    
    // Convenience methods to access user data
    public int getUserAge() {
        return user.getAge();
    }
    
    public String getUserCountry() {
        return user.getCountry();
    }
    
    public String getUserType() {
        return user.getType();
    }
}
```

This custom Context provides convenience methods to access user data directly, without having to use JsonPath expressions.

## Using a Custom Context

To use a custom Context, you create an instance of it and pass it to the `evaluate` method:

```java
// Create a user object
User user = new User("John", 25, "US", "premium");

// Create a custom context
UserContext context = new UserContext(user);

// Evaluate the tree with the custom context
KeyNode result = bonsai.evaluate("userEligibility", context);
```

## Advanced Custom Context

For more advanced use cases, you can override additional methods or add more functionality:

```java
public class EnhancedUserContext extends Context {
    private User user;
    private Map<String, Object> additionalData;
    private Logger logger;
    
    public EnhancedUserContext(User user, Map<String, Object> additionalData) {
        super(JsonPath.parse(user));
        this.user = user;
        this.additionalData = additionalData;
        this.logger = LoggerFactory.getLogger(EnhancedUserContext.class);
    }
    
    // Override the read method to add logging
    @Override
    public <T> T read(String path) {
        logger.debug("Reading path: {}", path);
        T result = super.read(path);
        logger.debug("Result: {}", result);
        return result;
    }
    
    // Add a method to access additional data
    public Object getAdditionalData(String key) {
        return additionalData.get(key);
    }
    
    // Add a method to check if the user is eligible for a feature
    public boolean isEligibleForFeature(String featureId) {
        // Implement custom eligibility logic
        if ("premium-feature".equals(featureId)) {
            return "premium".equals(user.getType()) && user.getAge() >= 18;
        }
        return false;
    }
}
```

This enhanced Context adds logging, access to additional data, and custom eligibility logic.

## Context with Custom Evaluation Logic

You can also create a Context that implements custom evaluation logic:

```java
public class CustomEvaluationContext extends Context {
    private Map<String, Boolean> featureFlags;
    
    public CustomEvaluationContext(Object data, Map<String, Boolean> featureFlags) {
        super(JsonPath.parse(data));
        this.featureFlags = featureFlags;
    }
    
    // Override the evaluate method to check feature flags first
    @Override
    public boolean evaluate(Filter filter) {
        // Check if the filter is for a feature flag
        if (filter.getPath().startsWith("$.features.")) {
            String featureId = filter.getPath().substring("$.features.".length());
            Boolean flagValue = featureFlags.get(featureId);
            if (flagValue != null) {
                // If the feature flag is set, use it instead of evaluating the filter
                return flagValue;
            }
        }
        
        // Fall back to the default evaluation logic
        return super.evaluate(filter);
    }
}
```

This Context checks a map of feature flags before falling back to the default evaluation logic.

## Context with Caching

For performance-critical applications, you can create a Context that caches evaluation results:

```java
public class CachingContext extends Context {
    private Map<String, Object> cache = new HashMap<>();
    
    public CachingContext(Object data) {
        super(JsonPath.parse(data));
    }
    
    @Override
    public <T> T read(String path) {
        // Check if the result is already in the cache
        if (cache.containsKey(path)) {
            @SuppressWarnings("unchecked")
            T result = (T) cache.get(path);
            return result;
        }
        
        // If not, read it and cache the result
        T result = super.read(path);
        cache.put(path, result);
        return result;
    }
    
    // Add a method to clear the cache
    public void clearCache() {
        cache.clear();
    }
}
```

This Context caches the results of JsonPath expressions to avoid re-evaluating them.

## Context with Preferences

The Context class supports preferences, which allow you to override the normal tree traversal:

```java
public class PreferencesContext extends Context {
    public PreferencesContext(Object data, Map<String, Knot> preferences) {
        super(JsonPath.parse(data), preferences);
    }
    
    // Add a method to add a preference
    public void addPreference(String key, Knot knot) {
        getPreferences().put(key, knot);
    }
    
    // Add a method to remove a preference
    public void removePreference(String key) {
        getPreferences().remove(key);
    }
    
    // Add a method to check if a preference exists
    public boolean hasPreference(String key) {
        return getPreferences().containsKey(key);
    }
}
```

This Context adds methods to manage preferences.

## Best Practices

- **Keep it simple**: Start with a simple custom Context and add complexity as needed
- **Focus on your use case**: Tailor your Context to your specific application needs
- **Document your Context**: Document the expected data structure and behavior
- **Test thoroughly**: Test your custom Context with various inputs and edge cases
- **Consider performance**: Be mindful of the performance implications of your custom logic
- **Use inheritance wisely**: Create a hierarchy of Context classes if it makes sense for your application
- **Be consistent**: Use a consistent approach across your application
