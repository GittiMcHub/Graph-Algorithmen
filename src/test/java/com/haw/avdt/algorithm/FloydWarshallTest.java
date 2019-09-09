package com.haw.avdt.algorithm;

import com.haw.avdt.algorithm.ex.FoundCycleWithNegativeSum;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.util.gka.GKAFileReader;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FloydWarshallTest {


    /** FloydWarshall
     * Testen Sie für graph3 in graph3.gka
     */
    @Test
    void shortestPathFromGKAFile(){

        String gkaFile = "graph03.gka";

        try {

            // Kuerzester Pfad: [Vertex{Husum}, Vertex{Kiel}, Vertex{Uelzen}, Vertex{Rotenburg}]
            GKAGraphDescripton testGKA = GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource(gkaFile).toURI()).toString());
            AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createJUNGGraph(testGKA);

            // Test Vertexe
            Vertex srcHusum = graph.getVertices().stream().filter(v -> v.getName().equals("Husum")).findFirst().get();
            Vertex targetRotenburg = graph.getVertices().stream().filter(v -> v.getName().equals("Rotenburg")).findFirst().get();

            FloydWarshall fw = new FloydWarshall(graph);
            Dijkstra dijkstra = new Dijkstra(graph);
            dijkstraVersusFloydWarshall(srcHusum, targetRotenburg, dijkstra, fw);
            System.out.println("Zugriffe: Floyed-Warshall = "+fw.getAccessCount() + " | Dijkstra = "+dijkstra.getCounter());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testNegativeEdges() throws FoundCycleWithNegativeSum, UnreachableVertexException {
        AbstractTypedGraph<Vertex, Edge> graphWithNegativeEdges = new DirectedSparseGraph<>();
        Vertex v1 = new Vertex("v1");
        Vertex v2 = new Vertex("v2");
        Vertex v3 = new Vertex("v3");
        Vertex v4 = new Vertex("v4");
        Edge e1 = new Edge("e1", 1);
        Edge e2 = new Edge("e2", 1);
        Edge e3 = new Edge("e3", 4);
        Edge e4 = new Edge("e4", -3);
        graphWithNegativeEdges.addVertex(v1);
        graphWithNegativeEdges.addVertex(v2);
        graphWithNegativeEdges.addVertex(v3);
        graphWithNegativeEdges.addVertex(v4);
        graphWithNegativeEdges.addEdge(e1, v1, v2);
        graphWithNegativeEdges.addEdge(e2, v2, v3);
        graphWithNegativeEdges.addEdge(e3, v1, v4);
        graphWithNegativeEdges.addEdge(e4, v4, v3);
        FloydWarshall fw = new FloydWarshall(graphWithNegativeEdges);
        assertEquals(1, fw.getDistance(v1, v3));

    }

    @Test
    void testFoundCycleWithNegativeSumException() {
        AbstractTypedGraph<Vertex, Edge> graphWithNegativeCircle = new DirectedSparseGraph<>();
        Vertex v1 = new Vertex("v1");
        Vertex v2 = new Vertex("v2");
        Vertex v3 = new Vertex("v3");
        Vertex v4 = new Vertex("v4");
        Edge e1 = new Edge("e1", 1);
        Edge e2 = new Edge("e2", -1);
        Edge e3 = new Edge("e3", -1);
        Edge e4 = new Edge("e4", -1);
        graphWithNegativeCircle.addVertex(v1);
        graphWithNegativeCircle.addVertex(v2);
        graphWithNegativeCircle.addVertex(v3);
        graphWithNegativeCircle.addVertex(v4);
        graphWithNegativeCircle.addEdge(e1, v1, v2);
        graphWithNegativeCircle.addEdge(e2, v2, v3);
        graphWithNegativeCircle.addEdge(e3, v3, v4);
        graphWithNegativeCircle.addEdge(e4, v4, v1);
        assertThrows(FoundCycleWithNegativeSum.class, () -> new FloydWarshall(graphWithNegativeCircle));
    }

    @Test
    void testUnreachableException() throws FoundCycleWithNegativeSum{
        AbstractTypedGraph<Vertex, Edge> graphWithUnreachableVertex = new DirectedSparseGraph<>();
        Vertex v1 = new Vertex("v1");
        Vertex v2 = new Vertex("v2");
        Vertex v3 = new Vertex("v3");
        Vertex v4 = new Vertex("v4");
        Edge e1 = new Edge("e1", 1);
        Edge e2 = new Edge("e2", 2);
        Edge e3 = new Edge("e3", 4);
        graphWithUnreachableVertex.addVertex(v1);
        graphWithUnreachableVertex.addVertex(v2);
        graphWithUnreachableVertex.addVertex(v3);
        graphWithUnreachableVertex.addVertex(v4);
        graphWithUnreachableVertex.addEdge(e1, v1, v2);
        graphWithUnreachableVertex.addEdge(e2, v2, v3);
        graphWithUnreachableVertex.addEdge(e3, v3, v1);
        FloydWarshall fw = new FloydWarshall(graphWithUnreachableVertex);
        assertThrows(UnreachableVertexException.class, () -> fw.getDistance(v1, v4));
    }
    @Test
    void shortestPathFromBIGWithAllVertecies() throws FoundCycleWithNegativeSum, InterruptedException {
        AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createBIGJUNGGraph();
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        Dijkstra dijkstra = new Dijkstra(graph);
        List<Vertex> vertices = new ArrayList<>(graph.getVertices());
        for(int i = 0; i < vertices.size(); i++){
            int sourceIndex = i;

            for(int y = 0; y < vertices.size(); y++){
                int targetIndex = y;
                if(sourceIndex != targetIndex){
                    try {dijkstraVersusFloydWarshall(vertices.get(sourceIndex), vertices.get(targetIndex), dijkstra, floydWarshall);}
                    catch (UnreachableVertexException e){
                        continue;
                    }
                }
            }
        }
    }

    private void dijkstraVersusFloydWarshall(Vertex source, Vertex target, Dijkstra dijkstra, FloydWarshall floydWarshall) throws UnreachableVertexException, FoundCycleWithNegativeSum, InterruptedException {

        List<Vertex> floydWarshallPath = floydWarshall.getPath(source, target);

        List<Vertex> dijkstraPath = dijkstra.getPath(source, target);

        if (floydWarshallPath.size() != dijkstraPath.size()) {
            System.err.println("Source: " + source.getName() + " and target:" + target.getName()
                    + " dijkstra path = " + dijkstraPath + " and fw path = " + floydWarshallPath);
        } else {
            for (int i = 0; i < floydWarshallPath.size(); i++) {
                //System.out.println("JUNG: " + jungDijsktraPath.get(i).getName() + "\t Own: " + dijkstraPath.get(i).getName());
                if (floydWarshallPath.get(i) != dijkstraPath.get(i)) {
                    System.err.println("Source: " + source.getName() + " and target:" + target.getName()
                            + " dijkstra path = " + dijkstraPath + " and fw path = " + floydWarshallPath);
                }
            }
        }
        int floydWarshallDistance = floydWarshall.getDistance(source, target);
        int dijkstraDistance = dijkstra.getDistance(source, target);
        System.out.println("Distance: " + floydWarshallDistance + " vs. " + dijkstraDistance);
        assertEquals(floydWarshallDistance,dijkstraDistance );

    }

}