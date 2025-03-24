<p align="center">
  <h1 align="center">Bonsai</h1>
  <p align="center">Java Rule Engine library over a tree data-structure<p>
  <p align="center">
    <a href="https://github.com/PhonePe/bonsai/actions">
    	<img src="https://github.com/PhonePe/bonsai/actions/workflows/build.yml/badge.svg"/>
    </a>
    <a href="https://central.sonatype.com/artifact/com.phonepe.commons/bonsai">
    	<img src="https://img.shields.io/maven-central/v/com.phonepe.commons/bonsai"/>
    </a>
    <a href="https://github.com/PhonePe/bonsai/blob/master/LICENSE">
    	<img src="https://img.shields.io/github/license/PhonePe/bonsai" alt="license" />
    </a>
    <a href="https://javadoc.io/doc/com.phonepe.platform/bonsai">
    	<img src="https://javadoc.io/badge2/com.phonepe.platform/bonsai/javadoc.svg" alt="javadoc" />
    </a>
  </p>
  <p align="center">
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=alert_status"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://img.shields.io/sonar/coverage/PhonePe_bonsai?server=https%3A%2F%2Fsonarcloud.io"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=bugs"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=vulnerabilities"/>
    </a>
  </p>
</p>


Each of us would have encountered decision trees and rule engines of various kinds in our software journeys.
Rule engines are typically structured as a set of rules that are evaluated against a **Context**.
Bonsai is a data structure that models such engines as a tree of knots and edges. The
kicker here is that the tree can be recursively nested, allowing you to represent really complex rules as simple nested
forest of trees.


[Bonsai Documentation](http://phonepe.github.io/bonsai) 

## Overview

Bonsai is a **Java Library** for data selection based on conditions. It is a powerful tree-based rule engine that enables you to:

1. Create a forest of trees with key-to-data mappings
2. Build complex decision trees with conditional branching
3. Represent nested, hierarchical rule structures
4. Evaluate rules against a context to traverse the tree and select the right data element. The Context and Rules are
   represented by a combination of [JsonPath](https://github.com/json-path/JsonPath) and [Query-DSL](https://github.com/PhonePe/query-dsl)
5. Modify trees dynamically with delta operations
6. Maintain versioning of tree components
7. Plug any storage implementations


## Installation

Add the Bonsai dependency to your Maven project:

```xml
<dependency>
    <groupId>com.phonepe.commons</groupId>
    <artifactId>bonsai-core</artifactId>
    <version>${bonsai.version}</version>
</dependency>
```

For Gradle:

```groovy
implementation 'com.phonepe.commons:bonsai-core:${bonsai.version}'
```

## Contributing

Contributions to Bonsai are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please ensure your code follows the project's coding standards and includes appropriate tests.

## License

Bonsai is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
