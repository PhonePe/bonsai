<pre> 
▄▄▄▄    ▒█████   ███▄    █   ██████  ▄▄▄       ██▓
▓█████▄ ▒██▒  ██▒ ██ ▀█   █ ▒██    ▒ ▒████▄    ▓██▒
▒██▒ ▄██▒██░  ██▒▓██  ▀█ ██▒░ ▓██▄   ▒██  ▀█▄  ▒██▒
▒██░█▀  ▒██   ██░▓██▒  ▐▌██▒  ▒   ██▒░██▄▄▄▄██ ░██░
░▓█  ▀█▓░ ████▓▒░▒██░   ▓██░▒██████▒▒ ▓█   ▓██▒░██░
░▒▓███▀▒░ ▒░▒░▒░ ░ ▒░   ▒ ▒ ▒ ▒▓▒ ▒ ░ ▒▒   ▓▒█░░▓  
▒░▒   ░   ░ ▒ ▒░ ░ ░░   ░ ▒░░ ░▒  ░ ░  ▒   ▒▒ ░ ▒ ░
 ░    ░ ░ ░ ░ ▒     ░   ░ ░ ░  ░  ░    ░   ▒    ▒ ░
 ░          ░ ░           ░       ░        ░  ░ ░  
      ░                                            
</pre>
      


# BONSAI
[![pipeline status](https://gitlab.phonepe.com/platform/bonsai/badges/develop/pipeline.svg)](https://gitlab.phonepe.com/platform/bonsai/commits/develop)
[![coverage report](https://gitlab.phonepe.com/platform/bonsai/badges/develop/coverage.svg)](https://gitlab.phonepe.com/platform/bonsai/commits/develop)


### Tree
Bonsai is a tree data structure
1. Allows you to create key to Knot mappings
2. Knots are full fledged trees (Knots with Variations)
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