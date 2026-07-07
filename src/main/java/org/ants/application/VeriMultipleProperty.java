package org.ants.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.ants.VeriBoost;
import org.ants.VeriBoostUtil.SimpleLink;

public class VeriMultipleProperty {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("[error] Please provide 2 arguments: <filePath> <propertyNumber>");
            System.exit(1);
        }

        String filePath = args[0];
        
        VeriBoost veriBoost = new VeriBoost();

        // Step 1: Load the network topology.
        readTopologyFile(filePath).forEach(link -> veriBoost.addLinks(link.dst_name, link.src_name));
        readTopologyFile(filePath).forEach(link -> veriBoost.addLinks(link.src_name, link.dst_name));

        // Step 2: Construct point biconnected components.
        veriBoost.buildComponent();

        // Read the properties from the file and limit to the specified number
        List<SimpleLink> properties = readPropertyFile(filePath, Integer.parseInt(args[1]));

        for (SimpleLink property : properties) {

            // Step 3: Query link status for single property
            veriBoost.calculateLinkStatus(property.src_name, property.dst_name);

            HashSet<?> symbolicLinks = veriBoost.getSymbolicLinks();
            HashSet<?> downLinks = veriBoost.getDownLinks();
            HashSet<?> upLinks = veriBoost.getUpLinks();

            // Step 4: Apply VeriBoost to verifiers.
            // Here, we use print the link status counts as a placeholder for actual verification logic.
            System.out.println("property: " + property.src_name + " -> " + property.dst_name
                    + ", symbolicinks: " + (symbolicLinks == null ? 0 : symbolicLinks.size())
                    + ", downLinks: " + (downLinks == null ? 0 : downLinks.size())
                    + ", upLinks: " + (upLinks == null ? 0 : upLinks.size()));
        }
    }
        
    static public List<String> getAllDirectoryNames(String directoryPath) throws IOException {
        Path basePath = Paths.get(directoryPath);
        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }
        try (java.util.stream.Stream<Path> stream = Files.list(basePath)) {
            return stream
                .filter(Files::isDirectory)  // Only directories, not files
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        }
    }
        
    static public List<SimpleLink> readPropertyFile(String datasetName, int propertyNumber) throws IOException {
        List<SimpleLink> properties = new ArrayList<>();
        Path filePath = Paths.get("dataset", datasetName, "reaches.txt");
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(i++ >= propertyNumber) break;
                String from = line.split("\t")[1];
                String to = line.split("\t")[2];
                SimpleLink link = new SimpleLink(from, to);
                properties.add(link);
            }
        }
        return properties;
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