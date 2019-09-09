package com.haw.avdt.util.gka;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GKAFileWriterTest {


    private boolean compareFiles(String file1, String file2) {
        try {
            List<String> file1Content = Files.readAllLines(Paths.get(file1));
            List<String> file2Content = Files.readAllLines(Paths.get(file2));
            for (String line : file1Content) {
                // Wenn Zeile nicht leer und nicht in anderer Datei existiert
                if (!line.isEmpty() && !file2Content.contains(line)) {
                    return false;
                }
            }
            for (String line : file2Content) {
                // Wenn Zeile nicht leer und nicht in anderer Datei existiert
                if (!line.isEmpty() && !file1Content.contains(line)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean readWriteAndCompare(String filename) {
        try {
            final String testFile = Paths.get(ClassLoader.getSystemClassLoader().getResource(filename).toURI()).toString();
            final String tmpFile = System.getProperty("java.io.tmpdir") + File.separator + filename;

            GKAGraphDescripton graphDesc = GKAFileReader.read(testFile);

            GKAFileWriter.write(tmpFile, graphDesc);
            return compareFiles(testFile, tmpFile);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Test
    void writeUndirectedWeightedAndNamedGraph() {
        assertTrue(readWriteAndCompare("UndirectedWeightedAndNamedGraph.gka"));
    }

    @Test
    void writeUndirectedUnweightedAndUnnamedGraph() {
        assertTrue(readWriteAndCompare("UndirectedUnweightedAndUnnamedGraph.gka"));
    }

    @Test
    void writeUndirectedUnweightedAndNamedGraph() {
        assertTrue(readWriteAndCompare("UndirectedUnweightedAndNamedGraph.gka"));
    }

    @Test
    void writeUndirectedWeightedAndUnnamenGraph() {
        assertTrue(readWriteAndCompare("UndirectedWeightedAndUnnamedGraph.gka"));
    }

    @Test
    void writeDirectedWeightedAndNamedGraph() {
        assertTrue(readWriteAndCompare("DirectedWeightedAndNamedGraph.gka"));
    }

    @Test
    void writeDirectedUnweightedAndUnnamedGraph() {
        assertTrue(readWriteAndCompare("DirectedUnweightedAndUnnamedGraph.gka"));
    }

    @Test
    void writeDirectedUnweightedAndNamedGraph() {
        assertTrue(readWriteAndCompare("DirectedUnweightedAndNamedGraph.gka"));
    }

    @Test
    void writeDirectedWeightedAndUnnamedGraph() {
        assertTrue(readWriteAndCompare("DirectedWeightedAndUnnamedGraph.gka"));
    }
}