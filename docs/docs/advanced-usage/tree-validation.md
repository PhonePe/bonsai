# Tree Validation

Validating tree structures is an important part of ensuring the integrity and correctness of your Bonsai trees. This guide explains how to use Bonsai's validation capabilities to validate tree structures.

## Why Validate Trees?

There are several reasons to validate tree structures:

- **Ensure correctness**: Verify that the tree structure is valid and follows the expected format
- **Prevent errors**: Catch errors before they cause problems at runtime
- **Maintain consistency**: Ensure that the tree structure is consistent with your application's expectations
- **Document expectations**: Use validation to document the expected structure of your trees
- **Improve reliability**: Make your application more reliable by validating inputs

## Using the BonsaiTreeValidator

Bonsai provides a `BonsaiTreeValidator` interface and a default implementation called `ComponentBonsaiTreeValidator`:

```java
// Create a validator
BonsaiTreeValidator validator = new ComponentBonsaiTreeValidator();

// Get a tree structure
TreeKnot tree = bonsai.getCompleteTree("userEligibility");

// Validate the tree structure
ValidationResult result = validator.validate(tree);

if (!result.isValid()) {
    // Handle validation errors
    List<ValidationError> errors = result.getErrors();
    for (ValidationError error : errors) {
        System.err.println(error.getMessage());
    }
}
```

The `validate` method returns a `ValidationResult` object that contains information about whether the tree is valid and any validation errors that were found.

## Validation Checks

The `ComponentBonsaiTreeValidator` performs several checks on the tree structure:

- **Cycle detection**: Ensures that the tree does not contain cycles
- **Knot existence**: Ensures that all referenced Knots exist
- **Edge validity**: Ensures that all Edges are valid
- **Filter validity**: Ensures that all Filters are valid
- **Property validity**: Ensures that all properties are valid

## Custom Validation

You can create a custom validator by implementing the `BonsaiTreeValidator` interface:

```java
public class CustomValidator implements BonsaiTreeValidator {
    @Override
    public ValidationResult validate(TreeKnot tree) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Perform custom validation checks
        if (tree.getData() == null) {
            errors.add(new ValidationError("Tree data cannot be null"));
        }
        
        // Check that the tree has at least one edge
        if (tree.getEdges() == null || tree.getEdges().isEmpty()) {
            errors.add(new ValidationError("Tree must have at least one edge"));
        }
        
        // Check that all edges have valid filters
        if (tree.getEdges() != null) {
            for (TreeEdge edge : tree.getEdges()) {
                if (edge.getFilters() == null) {
                    errors.add(new ValidationError("Edge filters cannot be null"));
                }
            }
        }
        
        // Return the validation result
        return new ValidationResult(errors.isEmpty(), errors);
    }
}
```

This custom validator performs additional checks beyond what the default validator does.

## Combining Validators

You can combine multiple validators to perform a more comprehensive validation:

```java
public class CompositeValidator implements BonsaiTreeValidator {
    private List<BonsaiTreeValidator> validators;
    
    public CompositeValidator(List<BonsaiTreeValidator> validators) {
        this.validators = validators;
    }
    
    @Override
    public ValidationResult validate(TreeKnot tree) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Run all validators
        for (BonsaiTreeValidator validator : validators) {
            ValidationResult result = validator.validate(tree);
            if (!result.isValid()) {
                errors.addAll(result.getErrors());
            }
        }
        
        // Return the combined validation result
        return new ValidationResult(errors.isEmpty(), errors);
    }
}

// Create a composite validator
List<BonsaiTreeValidator> validators = List.of(
    new ComponentBonsaiTreeValidator(),
    new CustomValidator(),
    new AnotherCustomValidator()
);
BonsaiTreeValidator compositeValidator = new CompositeValidator(validators);

// Validate the tree structure
ValidationResult result = compositeValidator.validate(tree);
```

This allows you to combine the default validator with custom validators to perform a more comprehensive validation.

## Validating During Tree Creation

You can validate trees during creation to ensure that they are valid before they are used:

```java
// Create a tree structure
TreeKnot tree = TreeKnot.builder()
    .id("rootKnot")
    .data(ValuedKnotData.builder().build())
    .properties(Map.of("description", "Root knot"))
    .edges(List.of(
        TreeEdge.builder()
            .id("edge1")
            .filters(List.of(
                Filter.builder()
                    .path("$.user.age")
                    .operator(Operator.GREATER_THAN_EQUAL)
                    .value(18)
                    .build()
            ))
            .targetKnot(
                TreeKnot.builder()
                    .id("eligibleKnot")
                    .data(ValuedKnotData.builder().booleanValue(true).build())
                    .properties(Map.of("description", "User is eligible"))
                    .build()
            )
            .build()
    ))
    .build();

// Validate the tree structure
BonsaiTreeValidator validator = new ComponentBonsaiTreeValidator();
ValidationResult result = validator.validate(tree);

if (result.isValid()) {
    // Create the tree
    Knot rootKnot = bonsai.createCompleteTree(tree);
    bonsai.createMapping("userEligibility", rootKnot.getId());
} else {
    // Handle validation errors
    List<ValidationError> errors = result.getErrors();
    for (ValidationError error : errors) {
        System.err.println(error.getMessage());
    }
}
```

This ensures that only valid trees are created.

## Validating During Tree Updates

You can also validate trees during updates to ensure that they remain valid:

```java
// Get the current tree
TreeKnot tree = bonsai.getCompleteTree("userEligibility");

// Make changes to the tree
// ...

// Validate the updated tree
BonsaiTreeValidator validator = new ComponentBonsaiTreeValidator();
ValidationResult result = validator.validate(tree);

if (result.isValid()) {
    // Apply the changes
    List<DeltaOperation> operations = createDeltaOperations(tree);
    bonsai.applyDeltaOperations("userEligibility", operations);
} else {
    // Handle validation errors
    List<ValidationError> errors = result.getErrors();
    for (ValidationError error : errors) {
        System.err.println(error.getMessage());
    }
}
```

This ensures that only valid updates are applied.

## Validation in a Service Layer

In a typical application, you might implement validation in a service layer:

```java
@Service
public class BonsaiService {
    private final Bonsai<Context> bonsai;
    private final BonsaiTreeValidator validator;
    
    @Autowired
    public BonsaiService(Bonsai<Context> bonsai, BonsaiTreeValidator validator) {
        this.bonsai = bonsai;
        this.validator = validator;
    }
    
    public Knot createTree(TreeKnot tree, String key) {
        // Validate the tree
        ValidationResult result = validator.validate(tree);
        if (!result.isValid()) {
            throw new InvalidTreeException("Invalid tree structure", result.getErrors());
        }
        
        // Create the tree
        Knot rootKnot = bonsai.createCompleteTree(tree);
        bonsai.createMapping(key, rootKnot.getId());
        return rootKnot;
    }
    
    public void updateTree(TreeKnot tree, String key) {
        // Validate the tree
        ValidationResult result = validator.validate(tree);
        if (!result.isValid()) {
            throw new InvalidTreeException("Invalid tree structure", result.getErrors());
        }
        
        // Update the tree
        List<DeltaOperation> operations = createDeltaOperations(tree);
        bonsai.applyDeltaOperations(key, operations);
    }
}
```

This ensures that all trees created or updated through the service are valid.

## Best Practices

- **Validate early**: Validate trees as early as possible to catch errors before they cause problems
- **Validate often**: Validate trees at key points in your application to ensure they remain valid
- **Use custom validators**: Create custom validators for your specific application needs
- **Combine validators**: Use multiple validators to perform a more comprehensive validation
- **Document validation rules**: Document the validation rules to make them easier to understand and maintain
- **Handle validation errors gracefully**: Provide clear error messages and handle validation errors appropriately
- **Test validation**: Test your validation logic with various inputs, including edge cases
