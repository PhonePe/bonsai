# Debugging Tips

Debugging Bonsai applications can be challenging due to the complex nature of tree structures and conditional logic. This guide provides tips and techniques for debugging Bonsai applications effectively.

## Logging

### Add Logging to Key Operations

Add logging statements to key operations to trace the execution flow:

```java
// Add logging to trace Bonsai operations
Logger logger = LoggerFactory.getLogger(YourClass.class);

// Log before evaluating a tree
logger.debug("Evaluating tree for key: {}", key);

// Log the context data
logger.debug("Context data: {}", context.getDocumentContext().jsonString());

// Log the result
KeyNode result = bonsai.evaluate(key, context);
logger.debug("Evaluation result: {}", result);
```

### Tracing

Bonsai provides a `TraceWrappedJsonPathFilterEvaluationEngine` that can be used to trace filter evaluation/
This gets used by default, if log level is set to `TRACE`. 

### Flat Evaluation

Bonsai provides a `evaluateFlat` method that returns a flat representation of the evaluated tree:

```java
// Get a flat representation of the evaluated tree
FlatTreeRepresentation flatTree = bonsai.evaluateFlat("key", context);

// Access the flat tree information
String rootKnotId = flatTree.getRootKnotId();
Map<String, Knot> knots = flatTree.getKnots();
Map<String, Edge> edges = flatTree.getEdges();
List<String> traversedEdgeIds = flatTree.getTraversedEdgeIds();

// Print the traversed path
System.out.println("Root knot: " + knots.get(rootKnotId));
for (String edgeId : traversedEdgeIds) {
    Edge edge = edges.get(edgeId);
    Knot targetKnot = knots.get(edge.getTargetKnotId());
    System.out.println("Edge: " + edge);
    System.out.println("Target knot: " + targetKnot);
}
```

## Performance Issues

If Bonsai operations are taking longer than expected:

- Optimize the tree structure
- Limit the depth of the tree
- Reduce the number of Edges per Knot
- Simplify filter conditions
- Implement caching

## Memory Issues

If Bonsai is using more memory than expected:

- Limit the size of the tree
- Reuse Knots where possible
- Optimize KnotData
- Consider using persistent storage