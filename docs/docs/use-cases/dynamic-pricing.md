# Dynamic Pricing Rules

Dynamic pricing is a strategy where prices are adjusted based on various factors such as demand, supply, time, customer segments, and more. Bonsai's tree-based rule engine is an excellent fit for implementing complex dynamic pricing rules.

## Why Use Bonsai for Dynamic Pricing?

Bonsai offers several advantages for dynamic pricing:

- **Complex Conditions**: Pricing rules can be based on various factors
- **Hierarchical Structure**: Pricing rules can be organized in a hierarchical structure
- **Dynamic Updates**: Pricing rules can be updated without code changes
- **Versioning**: Changes to pricing rules can be tracked and potentially reverted
- **Performance**: Efficient evaluation for high-throughput scenarios

## Basic Pricing Rule Implementation

Here's a simple example of implementing dynamic pricing with Bonsai:

```java
// Create a Bonsai instance
Bonsai<Context> bonsai = BonsaiBuilder.builder()
    .withBonsaiProperties(BonsaiProperties.builder().build())
    .withEdgeStore(new InMemoryEdgeStore())
    .withKeyTreeStore(new InMemoryKeyTreeStore())
    .withKnotStore(new InMemoryKnotStore())
    .build();

// Create pricing knots
Knot standardPricing = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(10.0).build(),
    Map.of("description", "Standard pricing")
);

Knot discountedPricing = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(8.5).build(),
    Map.of("description", "Discounted pricing")
);

Knot premiumPricing = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(12.0).build(),
    Map.of("description", "Premium pricing")
);

// Create pricing decision tree
Knot pricingRoot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "Pricing decision root")
);

// Premium pricing for high-demand times
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(premiumPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.time")
            .operator(Operator.IN)
            .value(List.of("PEAK_MORNING", "PEAK_EVENING"))
            .build()
    ))
    .build());

// Discounted pricing for loyal customers
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(discountedPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.loyaltyTier")
            .operator(Operator.GREATER_THAN_EQUAL)
            .value(3)
            .build()
    ))
    .build());

// Standard pricing as default
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(standardPricing.getId())
    .filters(List.of())
    .build());

// Map to a key
bonsai.createMapping("pricing.standard", pricingRoot.getId());

// Evaluate the pricing
Context context = Context.builder()
    .documentContext(JsonPath.parse("{\"user\": {\"loyaltyTier\": 4}, \"request\": {\"time\": \"REGULAR\"}}"))
    .build();
KeyNode result = bonsai.evaluate("pricing.standard", context);
double price = result.getValue().getNumberValue(); // 8.5
```

## Advanced Pricing Rule Implementation

For a more sophisticated dynamic pricing system, you can implement:

### Multiple Factors

Adjust prices based on multiple factors:

```java
// Premium pricing for high-demand times and premium users
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(premiumPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.time")
            .operator(Operator.IN)
            .value(List.of("PEAK_MORNING", "PEAK_EVENING"))
            .build(),
        Filter.builder()
            .path("$.user.type")
            .operator(Operator.EQUALS)
            .value("premium")
            .build()
    ))
    .build());
```

### Time-Based Pricing

Adjust prices based on time:

```java
// Weekend pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(weekendPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.dayOfWeek")
            .operator(Operator.IN)
            .value(List.of("SATURDAY", "SUNDAY"))
            .build()
    ))
    .build());

// Holiday pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(holidayPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.isHoliday")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());
```

### Location-Based Pricing

Adjust prices based on location:

```java
// Regional pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(highCostRegionPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.region")
            .operator(Operator.IN)
            .value(List.of("NYC", "SF", "LA"))
            .build()
    ))
    .build());
```

### Demand-Based Pricing

Adjust prices based on demand:

```java
// High demand pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(highDemandPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.demandLevel")
            .operator(Operator.GREATER_THAN)
            .value(0.8)
            .build()
    ))
    .build());

// Low demand pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(lowDemandPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.request.demandLevel")
            .operator(Operator.LESS_THAN)
            .value(0.2)
            .build()
    ))
    .build());
```

### Customer Segment Pricing

Adjust prices based on customer segments:

```java
// New customer pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(newCustomerPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.isNew")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());

// VIP customer pricing
bonsai.addVariation(pricingRoot.getId(), Variation.builder()
    .knotId(vipCustomerPricing.getId())
    .filters(List.of(
        Filter.builder()
            .path("$.user.isVIP")
            .operator(Operator.EQUALS)
            .value(true)
            .build()
    ))
    .build());
```

### Product-Specific Pricing

Implement different pricing rules for different products:

```java
// Create product-specific pricing knots
Knot product1PricingKnot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "Product 1 Pricing")
);

Knot product2PricingKnot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "Product 2 Pricing")
);

// Add variations to product-specific pricing knots
// ...

// Create a map of product pricing
Knot productPricingKnot = bonsai.createKnot(
    MapKnotData.builder()
        .keyMapping(Map.of(
            "product1", product1PricingKnot.getId(),
            "product2", product2PricingKnot.getId()
        ))
        .build(),
    Map.of("description", "Product Pricing")
);

// Map to a key
bonsai.createMapping("pricing.products", productPricingKnot.getId());

// Evaluate product-specific pricing
KeyNode result = bonsai.evaluate("pricing.products.product1", context);
double product1Price = result.getValue().getNumberValue();
```

### Price Modifiers

Implement price modifiers that adjust a base price:

```java
// Create price modifier knots
Knot noModifier = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(0.0).build(),
    Map.of("description", "No price modifier")
);

Knot discountModifier = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(-1.5).build(),
    Map.of("description", "Discount modifier")
);

Knot premiumModifier = bonsai.createKnot(
    ValuedKnotData.builder().numberValue(2.0).build(),
    Map.of("description", "Premium modifier")
);

// Create a price modifier decision tree
Knot modifierRoot = bonsai.createKnot(
    ValuedKnotData.builder().build(),
    Map.of("description", "Price modifier decision root")
);

// Add variations to the modifier root
// ...

// Map to a key
bonsai.createMapping("pricing.modifier", modifierRoot.getId());

// Evaluate the base price and modifier
KeyNode baseResult = bonsai.evaluate("pricing.standard", context);
KeyNode modifierResult = bonsai.evaluate("pricing.modifier", context);
double basePrice = baseResult.getValue().getNumberValue();
double modifier = modifierResult.getValue().getNumberValue();
double finalPrice = basePrice + modifier;
```

## Pricing Service

In a real application, you would typically implement a service layer for pricing:

```java
@Service
public class PricingService {
    private final Bonsai<Context> bonsai;
    
    @Autowired
    public PricingService(Bonsai<Context> bonsai) {
        this.bonsai = bonsai;
    }
    
    public double getPrice(String productId, User user, RequestContext requestContext) {
        // Create a context with user and request data
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("user", user);
        contextData.put("request", requestContext);
        contextData.put("product", productRepository.findById(productId));
        
        Context context = Context.builder()
            .documentContext(JsonPath.parse(contextData))
            .build();
        
        // Evaluate the pricing
        try {
            KeyNode result = bonsai.evaluate("pricing.products." + productId, context);
            return result.getValue().getNumberValue();
        } catch (BonsaiError e) {
            // Handle errors (e.g., pricing rule not found)
            return getDefaultPrice(productId);
        }
    }
    
    private double getDefaultPrice(String productId) {
        // Return a default price for the product
        return productRepository.findById(productId).getDefaultPrice();
    }
}
```

## Pricing Rule Management UI

For a complete dynamic pricing system, you would typically implement a management UI that allows business users to:

- Create and manage pricing rules
- Define conditions for different prices
- Monitor pricing rule usage
- A/B test pricing rules
- Analyze the impact of pricing rules on revenue

While implementing a UI is beyond the scope of this guide, Bonsai's API provides all the necessary operations to support such a UI.

## Best Practices

- **Use a consistent naming convention**: Use a consistent naming convention for pricing rule keys
- **Document pricing rules**: Document the purpose and conditions of each pricing rule
- **Test pricing rules**: Test pricing rules with various inputs to ensure they work as expected
- **Monitor pricing rule usage**: Track which prices are applied in which scenarios
- **Consider performance**: Optimize pricing rule evaluation for high-throughput scenarios
- **Implement caching**: Cache pricing results to improve performance
- **Handle errors gracefully**: Provide sensible defaults when pricing rules cannot be evaluated
- **Consider legal and ethical implications**: Ensure that your dynamic pricing strategy complies with relevant laws and regulations
