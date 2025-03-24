# Common Errors

This guide covers common errors you might encounter when working with Bonsai and provides guidance on how to resolve them.

## BonsaiError

Bonsai throws a `BonsaiError` when an operation fails. The error includes an error code and a message that provides more information about the error.

```java
try {
    KeyNode result = bonsai.evaluate("nonExistentKey", context);
} catch (BonsaiError e) {
    System.err.println("Error code: " + e.getErrorCode());
    System.err.println("Error message: " + e.getMessage());
}
```

## Common Error Codes

| Error Code                                    | Description                                | Resolution                                                                    |
|-----------------------------------------------|--------------------------------------------|-------------------------------------------------------------------------------|
| CYCLE_DETECTED                                | A cycle was detected in the tree structure | Review your tree structure to ensure there are no circular references         |
| VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR | Edge variations violate mutual exclusivity | Ensure edge conditions don't overlap when mutual exclusivity is enabled       |
| TREE_ALREADY_EXIST                            | Tree creation through delta ops failure    | A tree for the said key mapping already exists, try with a new key            |
| MAX_VARIATIONS_EXCEEDED                       | Too many variations on a knot              | Increase the maxAllowedVariationsPerKnot property or restructure your tree    |
| MAX_CONDITIONS_EXCEEDED                       | Too many conditions on an edge             | Increase the maxAllowedConditionsPerEdge property or simplify your conditions |


### KEY_NOT_FOUND

This error occurs when you try to evaluate a key that doesn't exist.

```java
try {
    KeyNode result = bonsai.evaluate("nonExistentKey", context);
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.KEY_NOT_FOUND) {
        // Handle key not found error
        System.err.println("Key not found: " + e.getMessage());
    }
}
```

**Resolution**:
- Check that the key exists in the key-tree store
- Use `bonsai.hasKey(key)` to check if a key exists before evaluating it
- Create the key mapping if it doesn't exist

### KNOT_ABSENT

This error occurs when you try to access a Knot that doesn't exist.

```java
try {
    Knot knot = bonsai.getKnot("nonExistentKnotId");
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.KNOT_ABSENT) {
        // Handle knot not found error
        System.err.println("Knot not found: " + e.getMessage());
    }
}
```

**Resolution**:
- Check that the Knot ID is correct
- Use `bonsai.hasKnot(knotId)` to check if a Knot exists before accessing it
- Create the Knot if it doesn't exist

### EDGE_ABSENT

This error occurs when you try to access an Edge that doesn't exist.

```java
try {
    Edge edge = bonsai.getEdge("nonExistentEdgeId");
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.EDGE_ABSENT) {
        // Handle edge not found error
        System.err.println("Edge not found: " + e.getMessage());
    }
}
```

**Resolution**:
- Check that the Edge ID is correct
- Use `bonsai.hasEdge(edgeId)` to check if an Edge exists before accessing it
- Create the Edge if it doesn't exist

### CYCLE_DETECTED

This error occurs when a cycle is detected in the tree structure.

```java
try {
    TreeKnotState result = bonsai.applyDeltaOperations("rootKey", operations);
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.CYCLE_DETECTED) {
        // Handle cycle detected error
        System.err.println("Cycle detected: " + e.getMessage());
    }
}
```

**Resolution**:
- Check the tree structure for cycles
- Ensure that Edges don't create cycles
- Use a validator to check for cycles before applying changes


### INVALID_INPUT

This error occurs when the KnotData or filters are invalid

```java
try {
    Knot knot = bonsai.createKnot(
        null, // Invalid KnotData
        Map.of("description", "Invalid knot")
    );
} catch (BonsaiError e) {
    if (e.getErrorCode() == BonsaiErrorCode.INVALID_INPUT) {
        // Handle invalid knot data error
        System.err.println("Invalid knot data: " + e.getMessage());
    }
}
```

**Resolution**:
- Check that the KnotData is valid
- Ensure that the KnotData is appropriate for the Knot type
- Validate KnotData before creating or updating Knots

## Runtime Errors

### OutOfMemoryError

This error occurs when the JVM runs out of memory.

```
java.lang.OutOfMemoryError: Java heap space
```

**Resolution**:
- Increase the JVM heap size (e.g., `-Xmx2g`)
- Optimize memory usage (e.g., reuse Knots, limit tree depth)
- Implement caching to reduce memory pressure
- Consider using persistent storage for large trees

### StackOverflowError

This error occurs when the call stack exceeds its limit, often due to infinite recursion.

```
java.lang.StackOverflowError
```

**Resolution**:
- Check for cycles in the tree structure
- Limit tree depth
- Optimize recursive operations
- Increase the stack size (e.g., `-Xss2m`)

## Concurrency Errors

### ConcurrentModificationException

This error occurs when a collection is modified while being iterated.

```
java.util.ConcurrentModificationException
```

**Resolution**:
- Use thread-safe collections
- Synchronize access to shared collections
- Use concurrent data structures
- Implement proper locking

## Best Practices for Error Handling

- **Use try-catch blocks**: Wrap Bonsai operations in try-catch blocks to handle errors
- **Check error codes**: Check the error code to determine the type of error
- **Provide meaningful error messages**: Include relevant information in error messages
- **Log errors**: Log errors for debugging and monitoring
- **Implement retry logic**: Implement retry logic for transient errors
- **Validate inputs**: Validate inputs before passing them to Bonsai
- **Test error scenarios**: Test error scenarios to ensure proper error handling
- **Provide fallbacks**: Provide fallback values or behavior when errors occur
