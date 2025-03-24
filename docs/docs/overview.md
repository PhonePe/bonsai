---
hide:
  - navigation
---

# Overview

Bonsai facilitates data selection over on conditions. Conditions are represented as tree-based rule engines that
enables developers to create complex decision trees with conditional branching and nested, hierarchical rule structures.

## Key Features

- **Tree-Based Structure**: Represent your rules as a tree of knots and edges
- **Recursive Nesting**: Create complex, nested rule structures
- **Conditional Branching**: Define conditions for traversing different paths in the tree
- **Context-Based Evaluation**: Evaluate rules against a context to select the appropriate data
- **Dynamic Modifications**: Modify trees at runtime with delta operations
- **Versioning Support**: Track changes to tree components
- **Pluggable Storage**: Use any storage implementation for persistence
- **Performance Optimized**: Designed for high-performance rule evaluation

## Architecture

Bonsai is built around a tree data structure where:

- **Knots** are the nodes in the tree, containing data or references to other knots
- **Edges** (or Variations) connect knots and define conditional paths through the tree
- **KnotData** represents the content stored within a knot (values, lists, or maps)
- **Context** is the evaluation entity against which the tree is traversed

This architecture allows for a flexible and powerful rule engine that can represent complex decision logic in a structured and maintainable way.

## Use Cases

Bonsai is particularly well-suited for scenarios where:

- You need to select different data based on complex conditions
- Rules need to be dynamically updated without code changes
- Decision logic is hierarchical or nested
- You want to maintain versioning of rule changes
- Performance is critical for rule evaluation

Some common use cases include:

- Feature flagging systems
- Dynamic pricing rules
- Content personalization
- Configuration management
- Eligibility determination
- Workflow routing

## Benefits

- **Flexibility**: Represent complex rules in a structured way
- **Maintainability**: Separate rule logic from application code
- **Scalability**: Handle large rule sets efficiently
- **Versioning**: Track changes to rules over time
- **Performance**: Optimize rule evaluation for high-throughput scenarios
- **Extensibility**: Plug in custom storage implementations
