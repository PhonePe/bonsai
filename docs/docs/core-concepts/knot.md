# Knot

A Knot is the fundamental building block of the Bonsai tree structure. It serves as a node in the tree and can contain data or references to other Knots.

## Characteristics

- Each Knot has a unique identifier (`knotId`)
- Contains data (`KnotData`) which can be a value or references to other Knots
- Has an ordered list of Edges/Variations that define conditional paths to other Knots
- Maintains version information for tracking changes
- Can have associated properties for metadata

## Structure

A Knot typically consists of:

```java
public class Knot {
    private String id;
    private KnotData data;
    private List<Edge> variations;
    private long version;
    private Map<String, Object> properties;
    // ...
}
```

## Types of Knots

Based on the type of data they contain, Knots can be categorized as:

1. **Value Knots**: Contain primitive values (string, boolean, number, bytes, JSON)
2. **List Knots**: Contain references to multiple other Knots
3. **Map Knots**: Contain key-based references to other Knots

## Role in the Tree

Knots serve different roles in the Bonsai tree structure:

- **Root Knots**: Entry points to the tree, typically mapped to keys
- **Decision Knots**: Intermediate nodes with multiple outgoing Edges based on conditions
- **Leaf Knots**: Terminal nodes containing the actual data values
- **Reference Knots**: Nodes that point to other parts of the tree, enabling reuse and complex structures

## Knot Properties

Knots can have associated properties that provide metadata or additional information:

- **Description**: Human-readable description of the Knot's purpose
- **Tags**: Categorization or grouping information
- **Creation Time**: When the Knot was created
- **Last Modified Time**: When the Knot was last updated
- **Custom Properties**: Any application-specific metadata

## Example

Here's an example of creating a Knot with a boolean value:

```java
// Create a Knot with a boolean value
Knot eligibleKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of("description", "User is eligible")
);
```

## Knot Operations

Bonsai provides several operations for working with Knots:

- **Create**: Create a new Knot with specified data and properties
- **Read**: Retrieve a Knot by its ID
- **Update**: Modify a Knot's data or properties
- **Delete**: Remove a Knot from the tree
- **Add Variation**: Add a new Edge/Variation to a Knot
- **Update Variation**: Modify an existing Edge/Variation on a Knot
- **Delete Variation**: Remove an Edge/Variation from a Knot

For more details on these operations, see the [Knot Operations](../operations/knot-operations.md) page.

## Best Practices

- Use meaningful IDs or let Bonsai generate them for you
- Keep Knot data focused on a single concept or decision point
- Use properties to add metadata that helps with management and understanding
- Consider versioning implications when updating Knots
- Design your tree structure to minimize the depth and complexity
