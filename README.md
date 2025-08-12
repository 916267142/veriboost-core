# VeriBoost

## 1. Overview

VeriBoost is an acceleration tool for network verifiers.  
Its core idea is to verify network properties by classifying network links into three categories based on the verification property: **up links**, **down links**, and **free links**.  
When solving the **k-failure** problem (i.e., verifying network behavior under up to k failures), VeriBoost only needs to consider the links in the **free** links. This optimization significantly reduces the verification complexity and improves performance.

---

## 2. Packaging and Installing VeriBoost Locally

To build the VeriBoost project and install the generated JAR file into your local Maven repository, follow these steps:

1. Package the project:

```bash
   mvn clean package
```

This will generate a shaded (fat) JAR in the target/ directory, for example: veriboost-core-1.0.jar.

2. Test whether the jar package is generated correctly.
```bash
java -jar target/veriboost-core-1.0.jar src/test/resources/topology.txt panamattcity raleigh
```

3. Install the JAR into your local Maven repository:
```
mvn install:install-file \
    -Dfile=target/veriboost-core-1.0.jar \
    -DgroupId=org.ants \
    -DartifactId=veriboost-core \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -DgeneratePom=true
```

Add the following dependency into the pom.xml of verifiers.
```
<dependency>
    <groupId>org.ants</groupId>
    <artifactId>veriboost-core</artifactId>
    <version>1.0</version>
</dependency>
```


4. Use VeriBoost. Below is a basic example to demonstrate the usage of VeriBoost in your Java application:

```Java
import org.ants.VeriBoost;
import org.ants.VeriBoostUtil.LinkType;
import java.util.HashSet;

public class Example {
    public static void main(String[] args) {
        VeriBoost veriBoost = new VeriBoost();

        // Step 1: Load the network topology from a file or use the addLinks method.
        veriBoost.readTopologyFromFile("/path/to/topology.txt");

        // Step 2: Build internal structures
        veriBoost.buildEdge();
        veriBoost.buildComponent();

        // Step 3: Calculate constraints between two nodes
        veriBoost.getMinesweeperConstraint("sourceNode", "destinationNode");

        // Step 4: Query constraints by link type
        HashSet<SimpleLink> upLinks = linkTypes.add(LinkType.free_link);
        HashSet<SimpleLink> downLinks = linkTypes.add(LinkType.down_link);
        HashSet<SimpleLink> freeLinks = linkTypes.add(LinkType.free_link);

        // Step 5: According to the type of verifiers, apply VeriBoost. For example, SMT-based, simulation-based, hybrid-based, or graph-based verifiers.
    }
}
```