package com.haw.avdt.util;

import com.haw.avdt.algorithm.Dijkstra;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.gka.GKAGraphComponentDescripton;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import com.haw.avdt.util.gka.ex.GKAGraphIncompabilityException;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphFactoryTest {

    @Test
    void createJUNGGraphUndirected() {
        
        GKAGraphComponentDescripton comp1 = new GKAGraphComponentDescripton("a", "b", "e1", false, 1);
        GKAGraphComponentDescripton comp2 = new GKAGraphComponentDescripton("a", "c", "e2", false, 1);
        GKAGraphComponentDescripton comp3 = new GKAGraphComponentDescripton("d", null, null, false, null);
        GKAGraphDescripton graphDesc = new GKAGraphDescripton(comp1);
        try {
            graphDesc.addComponent(comp1);
            graphDesc.addComponent(comp2);
            graphDesc.addComponent(comp3);

            AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createJUNGGraph(graphDesc);

            assertEquals(graph.getVertices().size(), 4);
            assertEquals(graph.getEdges().size(), 2);
            // Vertex
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("a")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("b")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("c")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("d")).findFirst().orElse(null));
            // Edges
            assertDoesNotThrow(() -> graph.getEdges().stream().filter(x -> x.getName().equals("e1")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getEdges().stream().filter(x -> x.getName().equals("e2")).findFirst().orElse(null));


            Vertex v = graph.getVertices().stream().filter(x -> x.getName().equals("a")).findFirst().orElse(null);

            assertEquals(graph.getOutEdges(v).size(), 2);

        } catch (GKAGraphIncompabilityException e) {
            fail();
        }
    }

    @Test
    void createJUNGGraphDirected() {

        GKAGraphComponentDescripton comp1 = new GKAGraphComponentDescripton("a", "b", "e1", true, 1);
        GKAGraphComponentDescripton comp2 = new GKAGraphComponentDescripton("c", "a", "e2", true, 1);
        GKAGraphComponentDescripton comp3 = new GKAGraphComponentDescripton("d", null, null, true, null);
        GKAGraphDescripton graphDesc = new GKAGraphDescripton(comp1);
        try {
            graphDesc.addComponent(comp1);
            graphDesc.addComponent(comp2);
            graphDesc.addComponent(comp3);

            AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createJUNGGraph(graphDesc);

            assertEquals(graph.getVertices().size(), 4);
            assertEquals(graph.getEdges().size(), 2);
            // Vertex
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("a")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("b")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("c")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getVertices().stream().filter(x -> x.getName().equals("d")).findFirst().orElse(null));
            // Edges
            assertDoesNotThrow(() -> graph.getEdges().stream().filter(x -> x.getName().equals("e1")).findFirst().orElse(null));
            assertDoesNotThrow(() -> graph.getEdges().stream().filter(x -> x.getName().equals("e2")).findFirst().orElse(null));


            Vertex vA = graph.getVertices().stream().filter(x -> x.getName().equals("a")).findFirst().orElse(null);
            Vertex vC = graph.getVertices().stream().filter(x -> x.getName().equals("c")).findFirst().orElse(null);

            assertEquals(graph.getOutEdges(vA).size(), 1);
            assertEquals(graph.getOutEdges(vC).size(), 1);
            assertEquals(graph.getIncidentEdges(vA).size(), 2);
            assertEquals(graph.getIncidentEdges(vC).size(), 1);

        } catch (GKAGraphIncompabilityException e) {
            fail();
        }
    }



    @Test
    void createRandomDirectedJUNGGraph(){

        int givenVertexes = 3;
        int givenEdges = 9;
        AbstractTypedGraph<Vertex, Edge> g = GraphFactory.createRandomDirectedJUNGGraph(givenVertexes,givenEdges);

        // Pruefen, dass Endlosschleife Verhindert wird
        assertThrows(IllegalArgumentException.class,() -> GraphFactory.createRandomDirectedJUNGGraph(3,10));

        assertEquals(g.getVertexCount(), givenVertexes);
        assertEquals(g.getEdgeCount(), givenEdges);

        // Bei 3 Vertexen und 9 Edges, muss jeder Vertex jeden Vertex als direkten Nachbar haben
        g.getVertices().forEach(v -> assertEquals(3, g.getNeighbors(v).size()));
    }


    /**
     * Prüft, ob Netzwerke mit zu wenig Kanten Ordnungsgemäß erkannt werden, sodass definitiv schwach zusammenhängende Graphen entstehen können
     */
    @Test
    void createRandomNetworkTestToLessEdgesGiven(){
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(4,2));
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(10,8));
    }

    /**
     * Prüft, dass die Parameter so kontrolliert werden, dass keine Mehrfachkanten und keine ausgehenden bei Target und keine eingehenden bei Source entstehen können
     */
    @Test
    void createRandomNetworkTestToManyEdgesGiven(){
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(3,4));
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(4,10));
    }

    /**
     * Prüft, dass keine Negativen Werte zum erzeugen der Graphen genutzt werden können
     */
    @Test
    void createRandomNetworkTestNegativeValues(){
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(-10,-9));
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(-10,9));
        assertThrows(IllegalArgumentException.class, () -> GraphFactory.createRandomNetwork(10,-9));
    }

    /**
     * Prüft, dass Source keine eingehende Kanten hat,
     * Prüft, dass Target keine ausgehende Kanten hat,
     * Und, dass es ein Weg von Source zu Target gibt (Info: nutzt den Dijkstra Algorithmus)
     */
    @Test
    void createRandomNetworkTestSourceTargetEdges(){
        AbstractTypedGraph<Vertex, Edge> bigNetJUNGGraph = GraphFactory.createBigNetJUNGGraph();

        Vertex sourceVertex = bigNetJUNGGraph.getVertices().stream().filter(v -> v.getName().equals(GraphFactory.RANDOM_NETWORK_DEFAULT_SOURCE_VERTEX_NAME)).findFirst().orElse(null);
        Vertex targetVertex = bigNetJUNGGraph.getVertices().stream().filter(v -> v.getName().equals(GraphFactory.RANDOM_NETWORK_DEFAULT_TARGET_VERTEX_NAME)).findFirst().orElse(null);

        // source sollte keine eingehenden haben
        assertEquals(0, bigNetJUNGGraph.getInEdges(sourceVertex).size());
        // target sollte keine ausgehenden haben
        assertEquals(0,bigNetJUNGGraph.getOutEdges(targetVertex).size());

        Dijkstra<AbstractTypedGraph<Vertex, Edge>> dijkstra = new Dijkstra<>(bigNetJUNGGraph);

        assertDoesNotThrow(() -> dijkstra.getPath(sourceVertex,targetVertex));
    }
}