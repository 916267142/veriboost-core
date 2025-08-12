package org.ants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.ants.VeriBoostUtil.Link;

//
// Created by Charlie on 2024/3/18.
// XJTU | 916267142@qq.com
//

public class VeriBoostParser {
    public HashSet<Link> links;

    public VeriBoostParser() {
        links = new HashSet<>();
    }

    public HashSet<Link> getLinks() {
        return links;
    }

    public void setLinks(HashSet<Link> links) {
        this.links = links;
    }

    // read the topology from file, the topology format are as the following
    // from_device:from_interface to_device:to_interface
    // zurich:FastEthernet0/0 frankfurt:FastEthernet4/0
    void readTopologyFromFile(String file_path) {
        try {
            File file = new File(file_path);
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    // delete some unmeaning char, such as '<' '>' ' '
                    line = line.replace("<", "");
                    line = line.replace(">", "");
                    line = line.replace(" ", "");

                    // read from device and to device, namely, the link of topology
                    StringTokenizer str = new StringTokenizer(line, ",", false);
                    if (str.countTokens() != 2) {
                        System.out.println("exception of reading lines");
                    }
                    String from = str.nextToken();
                    String to = str.nextToken();
                    StringTokenizer from_str = new StringTokenizer(from, ":", false);
                    StringTokenizer to_str = new StringTokenizer(to, ":", false);
                    VeriBoostUtil.Interface from_Interface = new VeriBoostUtil.Interface(from_str.nextToken(),
                            from_str.nextToken());
                    VeriBoostUtil.Interface to_Interface = new VeriBoostUtil.Interface(to_str.nextToken(), to_str.nextToken());
                    VeriBoostUtil.Link link = new VeriBoostUtil.Link(from_Interface, to_Interface);
                    this.links.add(link);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads topology from an InputStream with the same format as readTopologyFromFile
     * @param inputStream The input stream containing topology data
     */
    public void readTopologyFromStream(InputStream inputStream) {
        try (Scanner sc = new Scanner(inputStream)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                // Remove special characters and whitespace
                line = line.replace("<", "")
                        .replace(">", "")
                        .replace(" ", "");

                // Parse the line into from and to interfaces
                StringTokenizer str = new StringTokenizer(line, ",", false);
                if (str.countTokens() != 2) {
                    System.out.println("exception of reading lines");
                    continue;
                }
                
                String from = str.nextToken();
                String to = str.nextToken();
                
                StringTokenizer from_str = new StringTokenizer(from, ":", false);
                StringTokenizer to_str = new StringTokenizer(to, ":", false);
                
                VeriBoostUtil.Interface from_Interface = new VeriBoostUtil.Interface(
                    from_str.nextToken(),
                    from_str.nextToken()
                );
                
                VeriBoostUtil.Interface to_Interface = new VeriBoostUtil.Interface(
                    to_str.nextToken(),
                    to_str.nextToken()
                );
                
                VeriBoostUtil.Link link = new VeriBoostUtil.Link(from_Interface, to_Interface);
                this.links.add(link);
            }
        }
    }
    
    public void addLinks(String srcDevice, String dstDevice) {
        this.links.add(new VeriBoostUtil.Link(new VeriBoostUtil.Interface(srcDevice, "none"), new VeriBoostUtil.Interface(dstDevice, "none")));
    }
}
