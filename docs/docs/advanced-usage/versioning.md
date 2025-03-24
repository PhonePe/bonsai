# Versioning and Concurrency

Bonsai provides built-in support for versioning of tree components, which helps handle concurrent modifications and maintain data integrity. This guide explains how to use versioning in Bonsai.

## Understanding Versioning

Each Knot and Edge in Bonsai has a version number that is automatically incremented when the component is modified. This version number can be used to detect concurrent modifications and prevent data corruption.

## Version Checking

When updating a Knot or Edge, you can include the current version number in the properties map to ensure that the component hasn't been modified since you retrieved it:

```java
// Get a Knot
Knot knot = bonsai.getKnot("knotId");
long version = knot.getVersion();

// Update the Knot with version check
try {
    Knot oldKnot = bonsai.updateKnotData(
        "knotId",
        ValuedKnotData.builder().stringValue("Updated value").build(),
        Map.of("description", "Updated knot", "version", version)
    );
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.VERSION_MISMATCH) {
        // Handle version mismatch error
        System.err.println("Knot was modified by another process");
    } else {
        // Handle other errors
        throw e;
    }
}
```

If the Knot has been modified since you retrieved it, the `updateKnotData` method will throw a `BonsaiError` with the error code `VERSION_MISMATCH`.

## Handling Version Mismatch

When a version mismatch occurs, you have several options:

1. **Retry the operation**: Retrieve the Knot again and retry the update with the new version
2. **Merge the changes**: Retrieve the Knot again, merge your changes with the new version, and update with the merged changes
3. **Notify the user**: Inform the user that the Knot has been modified and ask them to review the changes
4. **Force the update**: Update the Knot without version checking (not recommended for most cases)

Here's an example of retrying the operation:

```java
boolean updated = false;
int maxRetries = 3;
int retryCount = 0;

while (!updated && retryCount < maxRetries) {
    try {
        // Get the Knot
        Knot knot = bonsai.getKnot("knotId");
        long version = knot.getVersion();
        
        // Update the Knot with version check
        Knot oldKnot = bonsai.updateKnotData(
            "knotId",
            ValuedKnotData.builder().stringValue("Updated value").build(),
            Map.of("description", "Updated knot", "version", version)
        );
        
        // Update successful
        updated = true;
    } catch (BonsaiError e) {
        if (e.getErrorCode() == BonsaiErrorCode.VERSION_MISMATCH) {
            // Retry
            retryCount++;
            if (retryCount >= maxRetries) {
                System.err.println("Failed to update Knot after " + maxRetries + " retries");
            }
        } else {
            // Handle other errors
            throw e;
        }
    }
}
```

## Versioning in Delta Operations

When using delta operations, you can include version information in the properties map for each operation:

```java
// Get the current version of a Knot
Knot knot = bonsai.getKnot("knotId");
long knotVersion = knot.getVersion();

// Get the current version of an Edge
Edge edge = bonsai.getEdge("edgeId");
long edgeVersion = edge.getVersion();

// Create a list of delta operations with version checks
List<DeltaOperation> operations = new ArrayList<>();

// Update a Knot with version check
operations.add(DeltaOperation.builder()
    .operationType(OperationType.UPDATE_KNOT_DATA)
    .id("knotId")
    .knotData(ValuedKnotData.builder().stringValue("Updated value").build())
    .properties(Map.of("description", "Updated knot", "version", knotVersion))
    .build());

// Update an Edge with version check
operations.add(DeltaOperation.builder()
    .operationType(OperationType.UPDATE_EDGE)
    .id("edgeId")
    .sourceKnotId("sourceKnotId")
    .targetKnotId("targetKnotId")
    .filters(List.of(
        Filter.builder()
            .path("$.user.age")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(21)
            .build()
    ))
    .properties(Map.of("version", edgeVersion))
    .build());

// Apply the delta operations
try {
    TreeKnotState result = bonsai.applyDeltaOperations("rootKey", operations);
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.VERSION_MISMATCH) {
        // Handle version mismatch error
        System.err.println("One or more components were modified by another process");
    } else {
        // Handle other errors
        throw e;
    }
}
```

If any of the components have been modified since you retrieved them, the `applyDeltaOperations` method will throw a `BonsaiError` with the error code `VERSION_MISMATCH`.

## Optimistic Locking

Bonsai's versioning system implements optimistic locking, which assumes that conflicts are rare and allows multiple processes to read the same data without locking. When a process wants to update the data, it checks that the data hasn't been modified since it was read.

This approach has several advantages:

- **Better performance**: No need to acquire and release locks for every operation
- **No deadlocks**: Since there are no locks, there's no risk of deadlocks
- **Better scalability**: Multiple processes can read the same data concurrently

However, it also has some limitations:

- **Conflicts can occur**: If multiple processes try to update the same data concurrently, some updates may fail
- **Retry logic required**: You need to implement retry logic to handle conflicts
- **Not suitable for high-contention scenarios**: If conflicts are frequent, optimistic locking may not be the best approach

## Versioning in a Service Layer

In a typical application, you might implement versioning in a service layer:

```java
@Service
public class BonsaiService {
    private final Bonsai<Context> bonsai;
    
    @Autowired
    public BonsaiService(Bonsai<Context> bonsai) {
        this.bonsai = bonsai;
    }
    
    public Knot updateKnot(String knotId, KnotData newData, Map<String, Object> properties) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // Get the Knot
                Knot knot = bonsai.getKnot(knotId);
                long version = knot.getVersion();
                
                // Add version to properties
                Map<String, Object> propertiesWithVersion = new HashMap<>(properties);
                propertiesWithVersion.put("version", version);
                
                // Update the Knot with version check
                return bonsai.updateKnotData(knotId, newData, propertiesWithVersion);
            } catch (BonsaiError e) {
                if (e.getErrorCode() == BonsaiErrorCode.VERSION_MISMATCH) {
                    // Retry
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw new ConcurrentModificationException("Failed to update Knot after " + maxRetries + " retries");
                    }
                } else {
                    // Handle other errors
                    throw e;
                }
            }
        }
        
        // This should never happen
        throw new IllegalStateException("Failed to update Knot");
    }
}
```

This service method automatically retries the update if a version mismatch occurs, up to a maximum number of retries.

## Best Practices

- **Always include version information** when updating Knots or Edges
- **Implement retry logic** to handle version mismatch errors
- **Limit the number of retries** to avoid infinite loops
- **Consider using a service layer** to encapsulate versioning logic
- **Be mindful of high-contention scenarios** where optimistic locking may not be the best approach
- **Document your versioning strategy** to make it easier to understand and maintain
- **Test concurrent modifications** to ensure your versioning logic works correctly
