package org.ants;

import java.io.IOException;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("[error] Please provide 3 arguments: <filePath> <srcNode> <dstNode>");
            System.exit(1);
        }

        String filePath = args[0];
        String srcNode = args[1];
        String dstNode = args[2];

        VeriBoost veriBoost = new VeriBoost();

        // Step 1: Load the network topology.
        veriBoost.readTopologyFromFile(filePath);
        
        // Step 2: Construct point biconnected components.
        veriBoost.buildComponent();

        // Step 3: Query link status for single property
        veriBoost.calculateLinkStatus(srcNode, dstNode);
        HashSet<?> symbolicLinks = veriBoost.getSymbolicLinks();
        HashSet<?> downLinks = veriBoost.getDownLinks();
        HashSet<?> upLinks = veriBoost.getUpLinks();

        // Step 4: Apply VeriBoost to verifiers.
        // Here, we use print the link status counts as a placeholder for actual verification logic.
        // Because the final result is counted as bidirectional links, the actual link count should be divided by 2
        System.out.println("property: " + srcNode + " -> " + dstNode
                + ", symbolicinks: " + (symbolicLinks == null ? 0 : symbolicLinks.size() / 2)
                + ", downLinks: " + (downLinks == null ? 0 : downLinks.size() / 2)
                + ", upLinks: " + (upLinks == null ? 0 : upLinks.size() / 2)) ;
    }
}
