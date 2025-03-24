# Contextual Preferences

Contextual preferences in Bonsai allow you to override the normal tree traversal for specific keys. This is a powerful feature that enables user-specific customizations and overrides of default configurations.

## Understanding Contextual Preferences

The Context class in Bonsai includes a `preferences` map, which maps keys to specific Knots. When evaluating a key, if the key is found in the preferences map, Bonsai will return the associated Knot directly, bypassing the normal tree traversal.

This allows you to provide user-specific overrides of default configurations without modifying the tree structure.

## Creating a Context with Preferences

You can create a Context with preferences using the builder pattern:

```java
// Create a custom Knot for a specific user
Knot customHomePageKnot = bonsai.createKnot(
    ValuedKnotData.builder().jsonValue("{\"theme\":\"dark\",\"layout\":\"compact\"}").build(),
    Map.of("description", "Custom home page configuration for user 123")
);

// Create a context with preferences
Map<String, Knot> preferences = Map.of("homePage", customHomePageKnot);
Context context = Context.builder()
    .documentContext(JsonPath.parse(userData))
    .preferences(preferences)
    .build();

// Evaluate the tree - will return the custom Knot directly
KeyNode result = bonsai.evaluate("homePage", context);
```

In this example, when evaluating the "homePage" key, Bonsai will return the customHomePageKnot directly, without traversing the tree.

## Use Cases for Contextual Preferences

Contextual preferences are useful in several scenarios:

### User-Specific Configurations

Allow users to customize their experience by overriding default configurations:

```java
// Get user preferences from a database
Map<String, Knot> userPreferences = userPreferenceService.getPreferences(userId);

// Create a context with user preferences
Context context = Context.builder()
    .documentContext(JsonPath.parse(userData))
    .preferences(userPreferences)
    .build();

// Evaluate the tree with user preferences
KeyNode result = bonsai.evaluate("userInterface", context);
```

### A/B Testing

Assign users to different test groups and override configurations accordingly:

```java
// Determine which test group the user belongs to
String testGroup = abTestingService.getUserTestGroup(userId);

// Get the test configuration for the user's test group
Knot testConfigKnot = abTestingService.getTestConfiguration(testGroup);

// Create a context with test preferences
Map<String, Knot> preferences = Map.of("featureConfig", testConfigKnot);
Context context = Context.builder()
    .documentContext(JsonPath.parse(userData))
    .preferences(preferences)
    .build();

// Evaluate the tree with test preferences
KeyNode result = bonsai.evaluate("featureConfig", context);
```

### Feature Flags

Override feature flags for specific users or environments:

```java
// Determine which features are enabled for the user
Map<String, Knot> featureFlags = featureFlagService.getUserFeatureFlags(userId);

// Create a context with feature flags
Context context = Context.builder()
    .documentContext(JsonPath.parse(userData))
    .preferences(featureFlags)
    .build();

// Evaluate the tree with feature flags
KeyNode result = bonsai.evaluate("features.newUI", context);
```

### Debugging and Testing

Override configurations for debugging or testing purposes:

```java
// Create a debug configuration
Knot debugConfigKnot = bonsai.createKnot(
    ValuedKnotData.builder().jsonValue("{\"logLevel\":\"debug\",\"traceEnabled\":true}").build(),
    Map.of("description", "Debug configuration")
);

// Create a context with debug preferences
Map<String, Knot> preferences = Map.of("appConfig", debugConfigKnot);
Context context = Context.builder()
    .documentContext(JsonPath.parse(appData))
    .preferences(preferences)
    .build();

// Evaluate the tree with debug preferences
KeyNode result = bonsai.evaluate("appConfig", context);
```

## Managing Preferences

In a real application, you would typically store preferences in a database and load them when creating the Context:

```java
@Service
public class PreferenceService {
    private final Bonsai<Context> bonsai;
    private final PreferenceRepository preferenceRepository;
    
    @Autowired
    public PreferenceService(Bonsai<Context> bonsai, PreferenceRepository preferenceRepository) {
        this.bonsai = bonsai;
        this.preferenceRepository = preferenceRepository;
    }
    
    public Map<String, Knot> getUserPreferences(String userId) {
        // Load user preferences from the database
        List<Preference> preferences = preferenceRepository.findByUserId(userId);
        
        // Convert preferences to a map of keys to Knots
        Map<String, Knot> preferenceMap = new HashMap<>();
        for (Preference preference : preferences) {
            // Get or create the Knot for the preference
            Knot knot = getOrCreateKnot(preference);
            preferenceMap.put(preference.getKey(), knot);
        }
        
        return preferenceMap;
    }
    
    private Knot getOrCreateKnot(Preference preference) {
        // Check if the Knot already exists
        if (preference.getKnotId() != null) {
            return bonsai.getKnot(preference.getKnotId());
        }
        
        // Create a new Knot for the preference
        KnotData knotData = createKnotData(preference);
        Knot knot = bonsai.createKnot(knotData, Map.of("description", "User preference"));
        
        // Update the preference with the new Knot ID
        preference.setKnotId(knot.getId());
        preferenceRepository.save(preference);
        
        return knot;
    }
    
    private KnotData createKnotData(Preference preference) {
        // Create KnotData based on the preference type
        switch (preference.getType()) {
            case STRING:
                return ValuedKnotData.builder().stringValue(preference.getStringValue()).build();
            case BOOLEAN:
                return ValuedKnotData.builder().booleanValue(preference.getBooleanValue()).build();
            case NUMBER:
                return ValuedKnotData.builder().numberValue(preference.getNumberValue()).build();
            case JSON:
                return ValuedKnotData.builder().jsonValue(preference.getJsonValue()).build();
            default:
                throw new IllegalArgumentException("Unsupported preference type: " + preference.getType());
        }
    }
}
```

## Custom Context with Preference Management

You can create a custom Context class that includes methods for managing preferences:

```java
public class UserContext extends Context {
    private final String userId;
    private final PreferenceService preferenceService;
    
    public UserContext(String userId, Object data, PreferenceService preferenceService) {
        super(JsonPath.parse(data), preferenceService.getUserPreferences(userId));
        this.userId = userId;
        this.preferenceService = preferenceService;
    }
    
    public void addPreference(String key, Knot knot) {
        getPreferences().put(key, knot);
        preferenceService.savePreference(userId, key, knot);
    }
    
    public void removePreference(String key) {
        getPreferences().remove(key);
        preferenceService.deletePreference(userId, key);
    }
    
    public boolean hasPreference(String key) {
        return getPreferences().containsKey(key);
    }
}
```

## Best Practices

- **Use preferences judiciously**: Overriding the normal tree traversal should be done for specific use cases, not as a general approach
- **Document preference keys**: Document the keys that can be overridden with preferences
- **Consider performance implications**: Loading preferences from a database can impact performance
- **Implement caching**: Cache preferences to improve performance
- **Validate preferences**: Ensure that preferences are valid before using them
- **Consider security implications**: Ensure that users can only override preferences they are allowed to
- **Test with and without preferences**: Test your application both with and without preferences to ensure it works correctly in all scenarios
