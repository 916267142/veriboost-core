package org.ants.application;

import java.util.HashSet;
import org.ants.VeriBoost;

public class VeriSingleProperty {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("[error] Please provide 3 arguments: <topologyFilePath> <srcNode> <dstNode>");
            System.exit(1);
        }

        String topologyFilePath = args[0];
        String srcNode = args[1];
        String dstNode = args[2];

        VeriBoost veriBoost = new VeriBoost();

        // Step 1: Load the network topology.
        veriBoost.readTopologyFromFile(topologyFilePath);

        // Step 2: Construct point biconnected components.
        veriBoost.buildComponent();

        // Step 3: Query link status for single property
        veriBoost.calculateLinkStatus(srcNode, dstNode);

        HashSet<?> symbolicLinks = veriBoost.getSymbolicLinks();
        HashSet<?> downLinks = veriBoost.getDownLinks();
        HashSet<?> upLinks = veriBoost.getUpLinks();

        System.out.println("symbolic_link count: " + (symbolicLinks == null ? 0 : symbolicLinks.size()));
        System.out.println("down_link count: " + (downLinks == null ? 0 : downLinks.size()));
        System.out.println("up_link count: " + (upLinks == null ? 0 : upLinks.size()));
    }
}
