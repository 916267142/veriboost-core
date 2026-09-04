package org.ants;
import org.ants.VeriBoostUtil.Link;
import javafx.util.Pair;
import java.util.HashSet;

public class Example {
    public static void main(String[] args) {
        VeriBoost veriBoost = new VeriBoost();

        // Step 1: Load the network topology from a file or use the addLinks method.
        veriBoost.readTopologyFromFile("dataset/uscarrier/topology.txt");

        // Step 2: Build internal structures
        veriBoost.buildComponent();

        // Mimics three properties.
        HashSet<Pair<String, String>> properties = new HashSet<>();
        properties.add(new Pair<String,String>("montgomery", "rocktthill"));
        properties.add(new Pair<String,String>("hawkinsville", "yemassee"));
        properties.add(new Pair<String,String>("danville", "staunton"));
	
        for(Pair<String, String> property : properties) {
            // Step 3: Calculate constraints between source node and desination node.
            String srcNode = property.getKey();
            String dstNode = property.getValue();
            System.out.print(srcNode + " " + dstNode);
            veriBoost.calculateLinkStatus(srcNode, dstNode);
     
            // Step 4: Query constraints by link type
            HashSet<Link> upLinks = veriBoost.getUpLinks();
            HashSet<Link> downLinks = veriBoost.getDownLinks();
            HashSet<Link> symbolicLinks = veriBoost.getSymbolicLinks();

            // Step 5: According to the type of verifiers, apply VeriBoost. For example, SMT-based, simulation-based, hybrid-based, or graph-based verifiers.
            System.out.println("property: " + srcNode + " -> " + dstNode
                + ", symbolicinks: " + (symbolicLinks == null ? 0 : symbolicLinks.size() / 2)
                + ", downLinks: " + (downLinks == null ? 0 : downLinks.size() / 2)
                + ", upLinks: " + (upLinks == null ? 0 : upLinks.size() / 2)) ;
        }
    }
}