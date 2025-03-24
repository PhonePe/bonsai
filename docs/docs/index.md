---
hide:
  - navigation
---

# Bonsai

Each of us would have encountered decision trees and rule engines of various kinds in our software journeys.
Rule engines are typically structured as a set of rules that are evaluated against a **Context**.
Bonsai is a data structure that models such engines as a tree of knots and edges. The
kicker here is that the tree can be recursively nested, allowing you to represent really complex rules as simple nested
forest of trees.

## What is Bonsai?

Bonsai is a decision forest. It is **Java Library** for data selection based on conditions. It enables you to:

1. Create a forest of trees with key-to-data mappings
2. Build complex decision trees with conditional branching
3. Represent nested, hierarchical rule structures
4. Evaluate rules against a context to traverse the tree and select the right data element. The Context and Rules are
   represented by a combination of [JsonPath](https://github.com/json-path/JsonPath)
   and [Query-DSL](https://github.com/PhonePe/query-dsl)
5. Modify trees dynamically with delta operations
6. Maintain versioning of tree components
7. Plug any storage implementations

## Why Bonsai?

While there are several rule engine options out there, none bridge the gap between data selection and rule definition in
a nice way. They are either too heavy or are workflow systems being termed as rule engines.

Consider a scenario where data elements are configurations, and different variations of these configurations are to be
selected based on a set of conditions. What started off as a small rule engine for Frontend App configurations, Bonsai
as a library powers a large number of use-cases internally in [PhonePe](https://www.phonepe.com/). It is either used 
directly as a light-weight library, or wrapped in a service that provides a UI to create and manage trees.
One such prominent service is an internal feature flagging and configuration system, which powers the page structure,
and launch / placement of most widgets on the PhonePe app.

## Visual Representation

![Bonsai Representation](assets/bonsai_representation.png)

## License

Bonsai is licensed under the Apache License 2.0. See
the [LICENSE](https://github.com/PhonePe/bonsai/blob/master/LICENSE) file for details.
