package org.ants;

import org.ants.VeriBoostUtil.LinkType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

public class VeriBoostTest {

    private VeriBoost veriBoost;

    @Before
    public void setUp() throws Exception {
        // Step1. read topology and build

        veriBoost = new VeriBoost();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("topology.txt");
        if (inputStream == null) {
            throw new RuntimeException("topology.txt not found in resources");
        }
        
        veriBoost.readTopologyFromStream(inputStream);
        veriBoost.buildEdge();
        veriBoost.buildComponent();
    }

    @Test
    public void testVeriBoost1() {
        // Step2. calculate the constraint
        veriBoost.getMinesweeperConstraint("panamattcity", "raleigh");

        // Step3. query the corresponding constraint
        assertEquals(61, veriBoost.getMinesweeperConstraint(LinkType.free_link).size());
        assertEquals(65, veriBoost.getMinesweeperConstraint(LinkType.up_link).size());
        assertEquals(79, veriBoost.getMinesweeperConstraint(LinkType.down_link).size());
    }

        @Test
    public void testVeriBoost2() {
        veriBoost.getMinesweeperConstraint("sylva", "ellijay");
        assertEquals(6, veriBoost.getMinesweeperConstraint(LinkType.free_link).size());
        assertEquals(8, veriBoost.getMinesweeperConstraint(LinkType.up_link).size());
        assertEquals(191, veriBoost.getMinesweeperConstraint(LinkType.down_link).size());

    }
}