package org.ants;

import java.util.HashMap;
import java.util.HashSet;

import org.ants.VeriBoostUtil.Link;
import org.ants.VeriBoostUtil.LinkType;
import org.ants.VeriBoostUtil.NetworkGraph;
import org.ants.VeriBoostUtil.SimpleLink;

//
// Created by Charlie on 2024/3/18.
// XJTU | 916267142@qq.com
//

/**
 * VeriBoost currently does not detect ACLs (Access Control Lists) or static routes configured
 * in the network devices. However, adding support for detecting these configurations is
 * straightforward and can be implemented with minimal changes to the existing framework.
 */

public class VeriBoost extends VeriBoostParser {
    TrimmingCore mincutCore;                    // Prune algorithm of min-cut
    SimplePathCore simplePathCore;              // Prune algorithm of component
    TrivialPathCore trivialPathCore;          // Prune algorithm of degree
    NetworkGraph networkGraph;

    // The following structure represent the query answer, the link has three type of status
    // Type = 0 means free link,
    // Type = 1 means the link should be down,
    // Type = 2 means the link should be up.
    private HashMap<Link, LinkType> minesweeper_link_types;

    public double prune_time = 0;

    public double compression_time = 0;

    public VeriBoost() {
        simplePathCore = new SimplePathCore();
        trivialPathCore = new TrivialPathCore();
        networkGraph = new NetworkGraph();
        mincutCore = new TrimmingCore();
    }

    Link getLink(String src, String dst) {
        return this.networkGraph.graph.get(src).get(dst);
    }

    public void buildEdge() {
        this.links.forEach(this::addBidirectionalEdge);
    }

    public void buildComponent() {
        simplePathCore.constructBCTree();
    }

    void addBidirectionalEdge(Link link) {
        this.networkGraph.addBidirectionalEdge(link);
        this.simplePathCore.addEdge(link.from_interface.device_name, link.to_interface.device_name);
        this.simplePathCore.addEdge(link.to_interface.device_name, link.from_interface.device_name);
        this.mincutCore.addEdge(link.from_interface.device_name, link.to_interface.device_name);
        this.mincutCore.addEdge(link.to_interface.device_name, link.from_interface.device_name);
    }

    public boolean getMinCutTestResults(String src_node, String dst_node, Integer tolerance) {
        return this.mincutCore.getCut(src_node, dst_node) <= tolerance;
    }

    HashMap<String, HashMap<String, Link>> getGraph() {
        return this.networkGraph.graph;
    }

    public NetworkGraph getGraphByComponentPrune(String src, String dst) {
        NetworkGraph network_graph = new NetworkGraph();
        if (!this.networkGraph.containEdge(src, dst)) {
            return network_graph;
        }
        this.simplePathCore.getRelevantLinks(src, dst).forEach(link -> {
            Link temp_link = getLink(link.src_name, link.dst_name);
            network_graph.addBidirectionalEdge(temp_link);
        });
        return network_graph;
    }

    public NetworkGraph getGraphByDegreePrune(NetworkGraph graph, String src, String dst) {
        if (!graph.containEdge(src, dst)) {
            return new NetworkGraph();
        }
        return this.trivialPathCore.getGraphByDegreePrune(graph, src, dst);
    }

    public void getMinesweeperConstraint(String src, String dst) {
        minesweeper_link_types = new HashMap<>();
        getGraph().forEach((key, value) -> value.forEach(
            (key1, link) -> minesweeper_link_types.put(link, LinkType.down_link)));


        double start = System.nanoTime();
        // The first step is pruned by component
        NetworkGraph graph1 = this.getGraphByComponentPrune(src, dst);
        graph1.getGraph().forEach((key, value) -> value.forEach(
            (key1, link) -> minesweeper_link_types.put(link, LinkType.up_link)));
        this.prune_time = System.nanoTime() - start;

        start = System.nanoTime();
        // The second step is pruned by degree
        NetworkGraph graph2 = this.getGraphByDegreePrune(graph1, src, dst);
        graph2.getGraph().forEach((key, value) -> value.forEach(
            (key1, link) -> minesweeper_link_types.put(link, LinkType.free_link)));
        this.compression_time = System.nanoTime() - start;

    }

    public HashSet<SimpleLink> getMinesweeperConstraint(LinkType link_type) {
        HashSet<SimpleLink> links = new HashSet<>();
        minesweeper_link_types.forEach((link, type) -> {
            if (link_type.equals(type)) {
                links.add(new SimpleLink(link));
            }
        });
        return links;
    }

    public void displayMinesweeperConstraint(HashSet<LinkType> link_type) {
        minesweeper_link_types.forEach((link, type) -> {
            if (link_type.contains(type)) {
                System.out.println(link);
            }
        });
        System.out.println("the size of link is " + minesweeper_link_types.entrySet().stream()
            .filter(n -> link_type.contains(n.getValue())).count());
    }


}
