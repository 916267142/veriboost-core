package org.ants;

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

import org.ants.VeriBoostUtil.SimpleLink;

public class CalculateSpace {

    static int number = 0;

    public static void main(String[] args) throws IOException {


        if (args.length < 2) {
            System.err.println("[error] Please provide 2 arguments: <topologyFilePath> <propertyNumber>");
            System.exit(1);
        }

        String filename = args[0];
        number = Integer.parseInt(args[1]);

        HashSet<SimpleLink> links = readTopologyFile(filename);
        // System.out.println(links.size() + " links read from topology file.");
        VeriBoost veriBoost = new VeriBoost();
        links.forEach(link -> {
            veriBoost.addLinks(link.dst_name, link.src_name);
        });

        veriBoost.buildComponent();
        List<SimpleLink> properties = readPropertyFile(filename);

        for (SimpleLink property : properties) {
            veriBoost.getMinesweeperConstraint(property.src_name, property.dst_name);
            HashSet<SimpleLink> upLinks = veriBoost.getUpLinks();
            HashSet<SimpleLink> downLinks = veriBoost.getDownLinks();
            HashSet<SimpleLink> freeLinks = veriBoost.getSymbolicLinks();
            System.out.print(property.src_name + " -> " + property.dst_name + ": ");
            System.out.print("Up Links: " + upLinks.size());
            System.out.print("Down Links: " + downLinks.size());
            System.out.print("Free Links: " + freeLinks.size());
            System.out.println();
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
        
    static public List<SimpleLink> readPropertyFile(String datasetName) throws IOException {
        List<SimpleLink> properties = new ArrayList<>();
        Path filePath = Paths.get("dataset", datasetName, "reaches.txt");
    
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(i++ >= number) break;
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
