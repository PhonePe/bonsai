# Nested Tree Structures

One of the most powerful features of Bonsai is the ability to create complex nested tree structures. This allows you to represent hierarchical data and complex decision logic in a structured and maintainable way.

## Understanding Nested Structures

Bonsai supports nested structures through two types of KnotData:

1. **MapKnotData**: Contains a map of string keys to other Knot references
2. **MultiKnotData**: Contains a list of keys that reference other Knots

These allow you to create trees where Knots can contain references to other Knots, enabling complex hierarchical structures.

## Creating Nested Map Structures

Here's an example of creating a nested map structure:

```java
// Create leaf knots with values
Knot nameKnot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("John Doe").build(),
    Map.of()
);

Knot ageKnot = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(30).build(),
    Map.of()
);

Knot activeKnot = bonsai.createKnot(
    ValuedKnotData.builder().booleanValue(true).build(),
    Map.of()
);

// Create a user profile knot that references the leaf knots
Knot userProfileKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "name", nameKnot.getId(),
            "age", ageKnot.getId(),
            "active", activeKnot.getId()
        ))
        .build(),
    Map.of("description", "User profile data")
);

// Map a key to the user profile knot
bonsai.createMapping("userProfile", userProfileKnot.getId());
```

This creates a structured user profile with name, age, and active status properties.

## Creating Nested List Structures

Here's an example of creating a nested list structure:

```java
// Create item knots with values
Knot item1Knot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("Item 1").build(),
    Map.of()
);

Knot item2Knot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("Item 2").build(),
    Map.of()
);

Knot item3Knot = bonsai.createKnot(
    ValuedKnotData.builder().stringValue("Item 3").build(),
    Map.of()
);

// Create a list knot that references the item knots
Knot listKnot = bonsai.createKnot(
    MultiKnotData.builder()
        .knotIds(List.of(item1Knot.getId(), item2Knot.getId(), item3Knot.getId()))
        .build(),
    Map.of("description", "List of items")
);

// Map a key to the list knot
bonsai.createMapping("itemList", listKnot.getId());
```

This creates a list of items that can be accessed as a collection.

## Combining Map and List Structures

You can combine map and list structures to create complex nested hierarchies:

```java
// Create user profile knots (as shown above)
Knot userProfile1Knot = createUserProfile("John", 30, true);
Knot userProfile2Knot = createUserProfile("Jane", 25, true);
Knot userProfile3Knot = createUserProfile("Bob", 40, false);

// Create a list of user profiles
Knot userListKnot = bonsai.createKnot(
    MultiKnotData.builder()
        .knotIds(List.of(userProfile1Knot.getId(), userProfile2Knot.getId(), userProfile3Knot.getId()))
        .build(),
    Map.of("description", "List of user profiles")
);

// Create department knots with user lists
Knot engineeringKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "name", bonsai.createKnot(ValuedKnotData.builder().stringValue("Engineering").build(), Map.of()).getId(),
            "users", userListKnot.getId()
        ))
        .build(),
    Map.of("description", "Engineering department")
);

// Create a company knot with departments
Knot companyKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "name", bonsai.createKnot(ValuedKnotData.builder().stringValue("Acme Inc.").build(), Map.of()).getId(),
            "departments", bonsai.createKnot(
                MultiKnotData.builder()
                    .knotIds(List.of(engineeringKnot.getId(), marketingKnot.getId()))
                    .build(),
                Map.of()
            ).getId()
        ))
        .build(),
    Map.of("description", "Company structure")
);

// Map a key to the company knot
bonsai.createMapping("company", companyKnot.getId());
```

This creates a complex nested structure representing a company with departments and users.

## Evaluating Nested Structures

When evaluating nested structures, the result is a KeyNode that reflects the structure of the KnotData:

```java
// Evaluate the company structure
KeyNode companyNode = bonsai.evaluate("company", context);

// Access the company name
String companyName = companyNode.getKeyNodeMap().get("name").getValue().getStringValue();

// Access the departments
List<KeyNode> departments = companyNode.getKeyNodeMap().get("departments").getKeyNodeList();

// Access the first department
KeyNode engineeringDept = departments.get(0);
String deptName = engineeringDept.getKeyNodeMap().get("name").getValue().getStringValue();

// Access the users in the department
List<KeyNode> users = engineeringDept.getKeyNodeMap().get("users").getKeyNodeList();

// Access the first user
KeyNode user = users.get(0);
String userName = user.getKeyNodeMap().get("name").getValue().getStringValue();
Double userAge = user.getKeyNodeMap().get("age").getValue().getNumberValue();
Boolean userActive = user.getKeyNodeMap().get("active").getValue().getBooleanValue();

System.out.println("Company: " + companyName);
System.out.println("Department: " + deptName);
System.out.println("User: " + userName + ", Age: " + userAge + ", Active: " + userActive);
```

## Conditional Nested Structures

You can combine nested structures with conditional logic to create dynamic hierarchies:

```java
// Create different configuration knots for different user types
Knot premiumConfigKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "theme", bonsai.createKnot(ValuedKnotData.builder().stringValue("premium").build(), Map.of()).getId(),
            "features", bonsai.createKnot(
                MultiKnotData.builder()
                    .knotIds(List.of(feature1Knot.getId(), feature2Knot.getId(), feature3Knot.getId()))
                    .build(),
                Map.of()
            ).getId()
        ))
        .build(),
    Map.of("description", "Premium user configuration")
);

Knot basicConfigKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "theme", bonsai.createKnot(ValuedKnotData.builder().stringValue("basic").build(), Map.of()).getId(),
            "features", bonsai.createKnot(
                MultiKnotData.builder()
                    .knotIds(List.of(feature1Knot.getId()))
                    .build(),
                Map.of()
            ).getId()
        ))
        .build(),
    Map.of("description", "Basic user configuration")
);

// Create a root knot with conditional edges
Knot rootKnot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "User configuration decision point")
);

// Add variations to the root knot
bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(premiumConfigKnot.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.type")
            .operator(Operator.EQUALS)
            .value("premium")
            .build()
    ))
    .build());

bonsai.addVariation(rootKnot.getId(), Variation.builder()
    .knotId(basicConfigKnot.getId())
    .filters(List.of())
    .build());

// Map a key to the root knot
bonsai.createMapping("userConfig", rootKnot.getId());
```

This creates a configuration structure that varies based on the user type.

## Best Practices

- **Plan your structure**: Design your nested structure before implementing it
- **Keep it shallow**: Minimize the depth of your nested structures for better performance
- **Reuse knots**: Use the same knot in multiple places to avoid duplication
- **Use meaningful keys**: Choose clear and descriptive keys for map entries
- **Consider evaluation performance**: Deep nested structures can be expensive to evaluate
- **Document your structure**: Document the expected structure to make it easier to understand and maintain
- **Validate your structure**: Use the validator to ensure your nested structure is valid
