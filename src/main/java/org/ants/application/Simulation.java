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
import org.ants.VeriBoostUtil.LinkType;
import org.ants.VeriBoostUtil.SimpleLink;

public class Simulation {

    static int tolerance = 3;
    static int number = 1000;

    public static class Scenario {
        HashSet<SimpleLink> links;

        public Scenario() {
            this.links = new HashSet<>();
        }

        public void addLink(SimpleLink link) {
            this.links.add(link);
        }

        public void removeLink(SimpleLink link) {
            this.links.remove(link);
        }

        public HashSet<SimpleLink> getLinks() {
            return this.links;
        }

        public void clearLinks() {
            this.links.clear();
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            if (links != null) {
                // Hash code based on all elements in the set
                for (SimpleLink link : links) {
                    result = prime * result + (link == null ? 0 : link.hashCode());
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Scenario other = (Scenario) obj;
            
            // Check if both have the same number of links
            if (links == null) {
                return other.links == null;
            }
            
            if (other.links == null) {
                return false;
            }
            
            if (links.size() != other.links.size()) {
                return false;
            }
            
            // Check each element exists in both sets (deep comparison)
            for (SimpleLink link : links) {
                if (!other.links.contains(link)) {
                    return false;
                }
            }
            
            return true;
        }

    }

    public static void main(String[] args) throws IOException {
        tolerance = 1;
        System.out.println("Tolerance: " + tolerance);

        getAllDirectoryNames("dataset/").forEach(Simulation::run);
    
        tolerance = 2;
        System.out.println("Tolerance: " + tolerance);
        getAllDirectoryNames("dataset/").forEach(Simulation::run);

        tolerance = 3;
        System.out.println("Tolerance: " + tolerance);
        getAllDirectoryNames("dataset/").forEach(Simulation::run);
    }

    static public void run(String filename) {
        try {
            HashSet<SimpleLink> links = readTopologyFile(filename);
            // System.out.println(links.size() + " links read from topology file.");
            VeriBoost veriBoost = new VeriBoost();
            links.forEach(link -> {
                veriBoost.addLinks(link.dst_name, link.src_name);
            });

            veriBoost.buildComponent();
            List<SimpleLink> properties = readPropertyFile(filename);
            // System.out.println("Properties size: " + properties.size());
            HashSet<Scenario> scenarios = new HashSet<>();
            int noOptimizationCount = 0;
            for (SimpleLink property : properties) {
                veriBoost.getMinesweeperConstraint(property.src_name, property.dst_name);

                HashSet<SimpleLink> upLinks = veriBoost.getUpLinks();
                 HashSet<SimpleLink> downLinks = veriBoost.getDownLinks();
                HashSet<SimpleLink> freeLinks = veriBoost.getSymbolicLinks();
                // System.out.println("-------------------");
                // System.out.println("Up Links: " + upLinks.size());
                // System.out.println("Down Links: " + downLinks.size());
                // System.out.println("Free Links: " + freeLinks.size());
                
                HashSet<HashSet<SimpleLink>> combinations = getCombinationLinks(tolerance, freeLinks);
                // System.out.println("Combinations of size 2: " + combinations.size());

                for (HashSet<SimpleLink> comb : combinations) { 
                    Scenario scenario = new Scenario();
                    for (SimpleLink link : comb) {
                        scenario.addLink(link);
                    }
                    // downLinks.forEach(link -> scenario.addLink(link));
                    scenarios.add(scenario);
                }
                noOptimizationCount += combinations.size();
                // break;
            }
            System.out.println(filename + "\t" + scenarios.size() + "\t" + noOptimizationCount);         
        } catch (IOException e) {
            System.err.println("Error reading topology file: " + e.getMessage());
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

    static public HashSet<HashSet<SimpleLink>> getCombinationLinks(int k, HashSet<SimpleLink> links) {
        HashSet<HashSet<SimpleLink>> combinations = new HashSet<>();
        List<SimpleLink> linkList = new ArrayList<>(links);
        int n = linkList.size();
        
        if (k > n || k <= 0) {
            return combinations; // Return empty set if k is invalid
        }
        
        generateCombinations(linkList, k, 0, new HashSet<>(), combinations);
        
        return combinations;
    }

    static private void generateCombinations(List<SimpleLink> linkList, int k, int start, 
                                            HashSet<SimpleLink> current, HashSet<HashSet<SimpleLink>> result) {
        if (current.size() == k) {
            result.add(new HashSet<>(current));
            return;
        }
        
        if (start >= linkList.size() || (linkList.size() - start + current.size()) < k) {
            return;
        }
        
        current.add(linkList.get(start));
        generateCombinations(linkList, k, start + 1, current, result);
        
        current.remove(linkList.get(start));
        generateCombinations(linkList, k, start + 1, current, result);
    }

}