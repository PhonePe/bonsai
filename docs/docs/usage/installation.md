# Installation

Adding Bonsai to your Java project is straightforward. This guide covers the different ways to include Bonsai as a dependency in your project.

## Maven

To add Bonsai to your Maven project, include the following dependency in your `pom.xml` file:

```xml
<dependency>
    <groupId>com.phonepe.commons</groupId>
    <artifactId>bonsai-core</artifactId>
    <version>${bonsai.version}</version>
</dependency>
```

Replace `${bonsai.version}` with the latest version of Bonsai. You can find the latest version on [Maven Central](https://central.sonatype.com/artifact/com.phonepe.commons/bonsai).

If you need additional modules, you can include them as separate dependencies:

```xml
<!-- For JSON evaluation support -->
<dependency>
    <groupId>com.phonepe.commons</groupId>
    <artifactId>bonsai-json-eval</artifactId>
    <version>${bonsai.version}</version>
</dependency>

<!-- For condition support -->
<dependency>
    <groupId>com.phonepe.commons</groupId>
    <artifactId>bonsai-conditions</artifactId>
    <version>${bonsai.version}</version>
</dependency>

<!-- For model classes -->
<dependency>
    <groupId>com.phonepe.commons</groupId>
    <artifactId>bonsai-models</artifactId>
    <version>${bonsai.version}</version>
</dependency>
```

## Gradle

To add Bonsai to your Gradle project, include the following dependency in your `build.gradle` file:

```groovy
implementation 'com.phonepe.commons:bonsai-core:${bonsai.version}'
```

Replace `${bonsai.version}` with the latest version of Bonsai.

For additional modules:

```groovy
// For JSON evaluation support
implementation 'com.phonepe.commons:bonsai-json-eval:${bonsai.version}'

// For condition support
implementation 'com.phonepe.commons:bonsai-conditions:${bonsai.version}'

// For model classes
implementation 'com.phonepe.commons:bonsai-models:${bonsai.version}'
```

## Building from Source

If you prefer to build Bonsai from source:

1. Clone the repository:
   ```bash
   git clone https://github.com/PhonePe/bonsai.git
   ```

2. Navigate to the project directory:
   ```bash
   cd bonsai
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. The built artifacts will be available in your local Maven repository.

## Dependencies

Bonsai has the following key dependencies:

- Java 17 or higher
- [JsonPath](https://github.com/json-path/JsonPath) for JSON path evaluation
- [Query-DSL](https://github.com/PhonePe/query-dsl) for condition evaluation

These dependencies will be automatically included when you add Bonsai to your project.

## Verifying Installation

To verify that Bonsai is correctly installed, you can create a simple test class:

```java
import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.core.vital.BonsaiBuilder;
import com.phonepe.commons.bonsai.core.vital.BonsaiProperties;

public class BonsaiTest {
    public static void main(String[] args) {
        // Create a Bonsai instance
        Bonsai<?> bonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(BonsaiProperties.builder().build())
            .build();
        
        System.out.println("Bonsai instance created successfully: " + bonsai);
    }
}
```

If the code compiles and runs without errors, Bonsai is correctly installed.
