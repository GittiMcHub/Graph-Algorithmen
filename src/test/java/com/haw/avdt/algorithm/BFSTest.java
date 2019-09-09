package com.haw.avdt.algorithm;

import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.util.gka.GKAFileReader;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BFSTest {


    private int getDistance(String nameOfTheFile, String source, String target) {
        int distance = -1;
        GKAGraphDescripton gd = null;
        try {
            gd = GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource(nameOfTheFile).toURI()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        AbstractTypedGraph<Vertex, Edge> g = GraphFactory.createJUNGGraph(gd);
        BFS bfs = new BFS(g);

        Vertex opSource = g.getVertices().stream().filter(v -> v.getName().equals(source)).findFirst().get();
        Vertex opTarget = g.getVertices().stream().filter(v -> v.getName().equals(target)).findFirst().get();
        try {
            distance = (int) bfs.getDistanceAndPath(opSource, opTarget).get("distance");
        } catch (UnreachableVertexException e) {
            e.printStackTrace();
        }
        return distance;
    }

    AbstractTypedGraph<Vertex, Edge> g;
    Vertex v1 = new Vertex("1");
    Vertex v2 = new Vertex("2");
    Vertex v3 = new Vertex("3");
    Vertex v4 = new Vertex("4");
    Vertex v5 = new Vertex("5");
    Edge e1 = new Edge();
    Edge e2 = new Edge();
    Edge e3 = new Edge();
    Edge e4 = new Edge();
    Edge e5 = new Edge();
    Edge e6 = new Edge();

    @BeforeEach
    void setUp() {
        g = new DirectedSparseGraph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        g.addEdge(e1, v1, v2);
        g.addEdge(e2, v2, v3);
        g.addEdge(e3, v3, v2);
        g.addEdge(e4, v1, v1);
        g.addEdge(e5, v2, v4);
        g.addEdge(e6, v3, v4);
    }

    @Test
    void getDistanceAndPath() {
        BFS bfs = new BFS(g);
        try {
            assertEquals(2, bfs.getDistanceAndPath(v1, v4).get("distance"));
            assertEquals(0, bfs.getDistanceAndPath(v1, v1).get("distance"));
            assertEquals("[1, 2, 4]", bfs.getDistanceAndPath(v1, v4).get("path").toString());
            assertEquals(2, getDistance("graph01.gka", "a", "l"));
            assertEquals(2, getDistance("graph02.gka", "g", "j"));
            assertEquals(3, getDistance("graph03.gka", "Hamburg", "Norderstedt"));
            assertEquals(2, getDistance("graph06.gka", "4", "7"));
        } catch (UnreachableVertexException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testBFSExceptions() {
        BFS bfs = new BFS(g);
        assertThrows(UnreachableVertexException.class, () -> {
            bfs.getDistanceAndPath(v4, v1);
        });
        assertThrows(UnreachableVertexException.class, () -> {
            bfs.getDistanceAndPath(v4, v5);
        });
    }
}