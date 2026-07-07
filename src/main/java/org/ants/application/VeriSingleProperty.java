package org.ants.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.ants.VeriBoost;
import org.ants.VeriBoostUtil.SimpleLink;

public class VeriSingleProperty {
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
        readTopologyFile(filePath).forEach(link -> veriBoost.addLinks(link.dst_name, link.src_name));
        readTopologyFile(filePath).forEach(link -> veriBoost.addLinks(link.src_name, link.dst_name));

        // Step 2: Construct point biconnected components.
        veriBoost.buildComponent();

        // Step 3: Query link status for single property
        veriBoost.calculateLinkStatus(srcNode, dstNode);

        HashSet<?> symbolicLinks = veriBoost.getSymbolicLinks();
        HashSet<?> downLinks = veriBoost.getDownLinks();
        HashSet<?> upLinks = veriBoost.getUpLinks();

        // Step 4: Apply VeriBoost to verifiers.
        // Here, we use print the link status counts as a placeholder for actual verification logic.
        System.out.println("property: " + srcNode + " -> " + dstNode
                + ", symbolicinks: " + (symbolicLinks == null ? 0 : symbolicLinks.size())
                + ", downLinks: " + (downLinks == null ? 0 : downLinks.size())
                + ", upLinks: " + (upLinks == null ? 0 : upLinks.size()));
        System.out.println(symbolicLinks);
    }

    static public HashSet<SimpleLink> readTopologyFile(String datasetName) throws IOException {
        HashSet<SimpleLink> links = new HashSet<>();
        Path filePath = Paths.get("dataset", datasetName, "topology.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String from = line.split("\t")[0];
                String to = line.split("\t")[1];
                SimpleLink link = new SimpleLink(from, to);
                links.add(link);
            }
        }
        return links;
    }
}
