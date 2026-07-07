package org.ants.application;

import java.util.HashSet;

import org.ants.VeriBoost;
import org.ants.VeriBoostUtil.LinkType;

public class Example {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("[error] Please provide 3 arguments: <topologyFilePath> <srcNode> <dstNode>");
            System.exit(1);
        }

        String topologyFilePath = args[0];
        String srcNode = args[1];
        String dstNode = args[2];

        VeriBoost veriBoost = new VeriBoost();
        veriBoost.readTopologyFromFile(topologyFilePath);

        veriBoost.buildEdge();
        veriBoost.buildComponent();

        veriBoost.getMinesweeperConstraint(srcNode, dstNode);

        HashSet<?> freeLinks = veriBoost.getMinesweeperConstraint(LinkType.free_link);
        HashSet<?> downLinks = veriBoost.getMinesweeperConstraint(LinkType.down_link);
        HashSet<?> upLinks = veriBoost.getMinesweeperConstraint(LinkType.up_link);

        System.out.println("free_link count: " + (freeLinks == null ? 0 : freeLinks.size()));
        System.out.println("down_link count: " + (downLinks == null ? 0 : downLinks.size()));
        System.out.println("up_link count: " + (upLinks == null ? 0 : upLinks.size()));
    }
}
