package com.haw.avdt.util.gka;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GKAFileReaderTest {

    @Test
    void readExceptions() {
        assertThrows(IOException.class, () -> GKAFileReader.read("FileNotFoundExceptionTest.gka"));
        assertThrows(IOException.class, () -> GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource("not_a_graph.gka").toURI()).toString()));
        assertThrows(IOException.class, () -> GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource("an_empty_file.gka").toURI()).toString()));
    }

    @Test
    void testResultsAfterRead() {

        readGraphAndCompare("graph01.gka", true, false, false, 41);
        readGraphAndCompare("graph02.gka", false, false, false, 38);
        readGraphAndCompare("graph03.gka", false, true, false, 39);
        readGraphAndCompare("graph04.gka", false, true, false, 23);
        readGraphAndCompare("graph05.gka", false, true, false, 19);
        readGraphAndCompare("graph06.gka", true, false, true, 9);
        readGraphAndCompare("graph07.gka", false, true, false, 22);
        readGraphAndCompare("graph08.gka", false, true, false, 15);
        readGraphAndCompare("graph09.gka", false, false, false, 36);
        readGraphAndCompare("graph10.gka", false, true, false, 26);
        readGraphAndCompare("graph11.gka", false, true, false, 23);

    }

    void readGraphAndCompare(String gkaFile, boolean expIsDirected, boolean expIsWeighted, boolean expHasEdgeNames, int expAmtOfComponents) {
        try {

            GKAGraphDescripton testGraph = GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource(gkaFile).toURI()).toString());
            assertEquals(testGraph.isDirected(), expIsDirected);
            assertEquals(testGraph.isWeighted(), expIsWeighted);
            assertEquals(testGraph.doesEdgesHaveNames(), expHasEdgeNames);
            assertEquals(testGraph.getComponents().size(), expAmtOfComponents);

            for (GKAGraphComponentDescripton comp : testGraph.getComponents()) {
                assertTrue(!comp.hasTarget() || comp.isEdgeDirected() == testGraph.isDirected());
                assertTrue(!comp.hasTarget() || comp.doesEdgeHasWeight() == testGraph.isWeighted());
                assertTrue(!comp.hasTarget() || comp.doesEdgeHasName() == testGraph.doesEdgesHaveNames());
            }

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


}