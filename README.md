<p align="center">
  <h1 align="center">Bonsai</h1>
  <p align="center">Rule Engine over a tree data-structure<p>
  <p align="center">
    <a href="https://github.com/PhonePe/bonsai/actions">
    	<img src="https://github.com/PhonePe/bonsai/actions/workflows/actions.yml/badge.svg"/>
    </a>
    <a href="https://s01.oss.sonatype.org/content/repositories/releases/com/phonepe/bonsai/">
    	<img src="https://img.shields.io/maven-central/v/com.phonepe.commons/bonsai"/>
    </a>
    <a href="https://github.com/PhonePe/bonsai/blob/master/LICENSE">
    	<img src="https://img.shields.io/github/license/PhonePe/bonsai" alt="license" />
    </a></p>
  <p align="center">
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=alert_status"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=coverage"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=bugs"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=PhonePe_bonsai">
    	<img src="https://sonarcloud.io/api/project_badges/measure?project=PhonePe_bonsai&metric=vulnerabilities"/>
    </a>
  </p>
</p>


Each one of us would have come across rule engines of all kinds in our software development journey. Rule engines are
typically structured as a set of rules that are evaluated against a context. Bonsai is one such data-structure, that
allows you to represent the rule engine as a tree of knots and edges. The kicker here is that the tree can be
recursively nested, allowing you to represent really complex rules as simple nested forest of trees.

### Tree

Bonsai is a tree data structure

1. Allows you to create key to Knot mappings
2. Knots are full-fledged trees (Knots with Variations)
3. Knots may recursively contain keys (which in-turn maps to other Knots)
4. Evaluate Keys on a Context

###### Key Terms

1. Knot
2. Edge/Variation
3. KnotData
4. Context

##### Tree Operations

##### Key Evaluation

### Details

There are 2 main concepts:<br>
Key (String) -> Knot <br>
A Knot has:

- Id
- KnotData
- List of Variations
- Version

KnotData are of 3 types

- MapKnot: Map of String to Keys
- MultiKnot: List of Keys
- ValueKnot:
    - String
    - Boolean
    - Number
    - Bytes
    - Json

Variations contain:

- Edge Id
- Id of Knot (to which the variation points to)
- List of Filters on Context
- Version

### Maven dependency

```
        <dependency>
            <groupId>com.phonepe.platform</groupId>
            <artifactId>bonsai-core</artifactId>
            <version>${bonsai.version}</version>
        </dependency>
```

### Usage

###### Creating the bonsai using the builder

```java
    Bonsai bonsai = BonsaiBuilder.builder()
        .withBonsaiProperties(
                BonsaiProperties.builder()
                        .maxAllowedVariationsPerKnot(10)
                        .maxAllowedConditionsPerEdge(22)
                        .mutualExclusivitySettingTurnedOn(false)
                        .build())
        .withBonsaiIdGenerator()    // some id gen
        .withEdgeStore()            // storage impl for edges
        .withKeyTreeStore()         // storage impl for key tree
        .withKnotStore()            // storage impl for knots
        .build();

```