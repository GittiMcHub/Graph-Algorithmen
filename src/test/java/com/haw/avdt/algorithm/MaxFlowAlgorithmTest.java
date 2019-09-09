package com.haw.avdt.algorithm;

import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.util.gka.GKAFileReader;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

class MaxFlowAlgorithmTest {


    @Test
    @Disabled
    void bothAlgorithmUnderMinute() throws UnreachableVertexException{
        int i = 100;
        int time = 0;
        while (time < 60000){
            time = (int) testIt(i, ((i-2)*(i-2))+(i-1));
            i+=100;
        }
        //testIt(8,10, 15000);

    }

    private long testIt(int vertexe, int edges) throws UnreachableVertexException{

            DirectedSparseGraph<Vertex, Edge> graph = GraphFactory.createRandomNetwork(vertexe, edges);
            Vertex s = graph.getVertices().stream().filter(v -> v.getName().equals(GraphFactory.RANDOM_NETWORK_DEFAULT_SOURCE_VERTEX_NAME)).findFirst().get();
            Vertex t = graph.getVertices().stream().filter(v -> v.getName().equals(GraphFactory.RANDOM_NETWORK_DEFAULT_TARGET_VERTEX_NAME)).findFirst().get();
            final long timeStartEK = System.currentTimeMillis();
            EdmondKarp edmondKarp = new EdmondKarp(graph, s, t);
            final long timeEndEK = System.currentTimeMillis();
            final long timeDiffEK = (timeEndEK - timeStartEK);

            final long timeStartFF = System.currentTimeMillis();
            FordFulkerson fordFulkerson = new FordFulkerson(graph, s, t);
            final long timeEndFF = System.currentTimeMillis();
            final long timeDiffFF = (timeEndFF - timeStartFF);

            System.out.println("Amount of vertices: "+ vertexe+ ". Amount of Edges: "+edges+".");
            System.out.println("EK took " + timeDiffEK + " milliseconds or "+timeDiffEK/1000+" seconds.");
            System.out.println("FF took " + timeDiffFF + " milliseconds or "+timeDiffFF/1000+" seconds.");


            assertEquals(fordFulkerson.getMaxFlow(), edmondKarp.getMaxFlow());

            if (timeDiffEK > timeDiffFF)
                return timeDiffEK;
            else return timeDiffFF;
    }




    @Test
    void getMaxFlowFromOurAndJungEdmondKarp() throws UnreachableVertexException {

        for (int i = 0; i < 100; i++) {
            DirectedSparseGraph<Vertex, Edge> graph = GraphFactory.createRandomNetwork(ThreadLocalRandom.current().nextInt(50, 100), ThreadLocalRandom.current().nextInt(800, 1500));
            Vertex s = graph.getVertices().stream().filter(v -> v.getName().equals(GraphFactory.RANDOM_NETWORK_DEFAULT_SOURCE_VERTEX_NAME)).findFirst().get();
            Vertex t = graph.getVertices().stream().filter(v -> v.getName().equals(GraphFactory.RANDOM_NETWORK_DEFAULT_TARGET_VERTEX_NAME)).findFirst().get();
            FordFulkerson fordFulkerson = new FordFulkerson(graph, s ,t);
            Function<Edge, Integer> capTransformer =
                    new Function<Edge, Integer>() {
                        public Integer apply(Edge edge) {
                            return edge.getWeight();
                        }
                    };
            Map<Edge, Integer> edgeFlowMap = new HashMap();
            Supplier<Edge> edgeFactory = new Supplier<Edge>() {
                public Edge get() {
                    return new Edge();
                }
            };
            EdmondsKarpMaxFlow<Vertex, Edge> alg =
                    new EdmondsKarpMaxFlow(graph, s, t, capTransformer, edgeFlowMap,
                            edgeFactory);
            alg.evaluate();
            EdmondKarp edmondKarp = new EdmondKarp(graph, s ,t);



                assertEquals(alg.getMaxFlow(), fordFulkerson.getMaxFlow());
                assertEquals(alg.getMaxFlow(), edmondKarp.getMaxFlow());
        }
    }

    @Test
    void getMaxFlow() throws UnreachableVertexException {
            Vertex s = new Vertex("s");
            Vertex t = new Vertex("t");
            Vertex a = new Vertex("a");
            Vertex b = new Vertex("b");
            Vertex c = new Vertex("c");
            Vertex d = new Vertex("d");

            Edge sa = new Edge("s->a", 10);
            Edge sc = new Edge("s->c", 5);
            Edge ca = new Edge("c->a", 6);
            Edge ab = new Edge("a->b", 4);
            Edge ad = new Edge("s->a", 5);
            Edge cd = new Edge("c->d", 5);
            Edge bd = new Edge("b->d", 6);
            Edge bt = new Edge("b->t", 4);
            Edge dt = new Edge("d->t", 12);

            DirectedSparseGraph<Vertex, Edge> graph = new DirectedSparseGraph<>();

            graph.addVertex(s);
            graph.addVertex(t);
            graph.addVertex(a);
            graph.addVertex(b);
            graph.addVertex(c);
            graph.addVertex(d);

            graph.addEdge(sa,s,a);
            graph.addEdge(sc,s,c);
            graph.addEdge(ca,c,a);
            graph.addEdge(ab,a,b);
            graph.addEdge(ad,a,d);
            graph.addEdge(cd,c,d);
            graph.addEdge(bd,b,d);
            graph.addEdge(bt,b,t);
            graph.addEdge(dt,d,t);

        Function<Edge, Integer> capTransformer =
                new Function<Edge, Integer>(){
                    public Integer apply(Edge edge)  {
                        return edge.getWeight();
                    }
                };
        Map<Edge, Integer> edgeFlowMap = new HashMap();
        // This Factory produces new edges for use by the algorithm

        Supplier<Edge> edgeFactory = new Supplier<Edge>() {
            public Edge get() {
                return new Edge();
            } };
        EdmondsKarpMaxFlow<Vertex, Edge> alg =
                new EdmondsKarpMaxFlow(graph,s, t, capTransformer, edgeFlowMap,
                        edgeFactory);
        alg.evaluate();
        //System.out.println("The max flow is: " + alg.getMaxFlow());
           FordFulkerson ff = new FordFulkerson(graph, s ,t);
           EdmondKarp edmondKarp = new EdmondKarp(graph, s, t);
           assertEquals(alg.getMaxFlow(), ff.getMaxFlow());
           assertEquals(alg.getMaxFlow(), edmondKarp.getMaxFlow());

        }

    @Test
    void wouldFailWithoutBackEdges() throws UnreachableVertexException {

        Vertex v0 = new Vertex("s");
        Vertex v7 = new Vertex("t");
        Vertex v1 = new Vertex("a");
        Vertex v2 = new Vertex("b");
        Vertex v3 = new Vertex("c");
        Vertex v4 = new Vertex("d");
        Vertex v5 = new Vertex("e");
        Vertex v6 = new Vertex("f");

        Edge sa = new Edge("s->a", 1);
        Edge sc = new Edge("s->c", 1);
        Edge ca = new Edge("c->a", 1);
        Edge ab = new Edge("a->b", 1);
        Edge ad = new Edge("s->a", 1);
        Edge cd = new Edge("c->d", 1);
        Edge bd = new Edge("b->d", 1);
        Edge bt = new Edge("b->t", 1);
        Edge dt = new Edge("d->t", 1);

        DirectedSparseGraph<Vertex, Edge> graph = new DirectedSparseGraph<>();

        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addVertex(v5);
        graph.addVertex(v6);
        graph.addVertex(v7);
        graph.addVertex(v0);

        graph.addEdge(sa,v0,v1);
        graph.addEdge(sc,v1,v2);
        graph.addEdge(ca,v2,v4);
        graph.addEdge(ab,v0,v3);
        graph.addEdge(ad,v3,v4);
        graph.addEdge(cd,v4,v7);
        graph.addEdge(bd,v3,v5);
        graph.addEdge(bt,v5,v6);
        graph.addEdge(dt,v6,v7);

        EdmondKarp edmondKarp = new EdmondKarp(graph, v0, v7);
        FordFulkerson ff = new FordFulkerson(graph, v0, v7);
        assertEquals(edmondKarp.getMaxFlow(),ff.getMaxFlow());
    }


    @Test
    void testGraph4WithFFAndEK() throws URISyntaxException, IOException, UnreachableVertexException {
        GKAGraphDescripton testGKA = GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource("graph04_Di.gka").toURI()).toString());
        DirectedSparseGraph<Vertex, Edge> network = (DirectedSparseGraph<Vertex, Edge>) GraphFactory.createJUNGGraph(testGKA);

        Vertex source = network.getVertices().stream().filter(v -> v.getName().equals("q")).findFirst().get();
        Vertex target = network.getVertices().stream().filter(v -> v.getName().equals("s")).findFirst().get();
        final long timeStart = System.currentTimeMillis();
        EdmondKarp edmondKarp = new EdmondKarp(network, source, target);
        final long timeEnd = System.currentTimeMillis();
        System.out.println("Verlaufszeit von Edmond Karp Algorithmus: " + (timeEnd - timeStart) + " Millisek.");

        final long timeStartFF = System.currentTimeMillis();
        FordFulkerson ff = new FordFulkerson(network, source, target);
        final long timeEndFF = System.currentTimeMillis();
        System.out.println("Verlaufszeit von Ford Folkerson Algorithmus: " + (timeEndFF - timeStartFF) + " Millisek.");


        assertEquals(ff.getMaxFlow(), edmondKarp.getMaxFlow());
        System.out.println("FF says: " + ff.getMaxFlow()+" and EK says: "+edmondKarp.getMaxFlow());

    }

    @Test
    void testUnreachableExceptions(){
        Vertex v0 = new Vertex("s");
        Vertex v7 = new Vertex("t");
        Vertex v1 = new Vertex("a");
        Vertex v2 = new Vertex("b");
        Vertex v3 = new Vertex("c");

        Edge sa = new Edge("s->a", 9);
        Edge sc = new Edge("s->c", 1);


        DirectedSparseGraph<Vertex, Edge> graph = new DirectedSparseGraph<>();

        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v7);
        graph.addVertex(v0);

        graph.addEdge(sa,v0,v1);
        graph.addEdge(sc,v1,v2);
        assertThrows(UnreachableVertexException.class, () -> {
            EdmondKarp edmondKarp = new EdmondKarp(graph, v0, v7);
        });
        assertThrows(UnreachableVertexException.class, () -> {
            FordFulkerson ff = new FordFulkerson(graph, v0 ,v7);
        });

    }

    @Test
    void testNegativeCapacity() throws UnreachableVertexException {
        Vertex v0 = new Vertex("s");
        Vertex v7 = new Vertex("t");
        Vertex v1 = new Vertex("a");
        Vertex v2 = new Vertex("b");
        Vertex v3 = new Vertex("c");
        Vertex v4 = new Vertex("d");
        Vertex v5 = new Vertex("e");
        Vertex v6 = new Vertex("f");

        Edge sa = new Edge("s->a", 9);
        Edge sc = new Edge("s->c", 1);
        Edge ca = new Edge("c->a", 2);
        Edge ab = new Edge("a->b", 1);
        Edge ad = new Edge("s->a", -3);
        Edge cd = new Edge("c->d", 16);
        Edge bd = new Edge("b->d", -3);
        Edge bt = new Edge("b->t", 8);
        Edge dt = new Edge("d->t", 12);

        DirectedSparseGraph<Vertex, Edge> graph = new DirectedSparseGraph<>();

        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addVertex(v5);
        graph.addVertex(v6);
        graph.addVertex(v7);
        graph.addVertex(v0);

        graph.addEdge(sa,v0,v1);
        graph.addEdge(sc,v1,v2);
        graph.addEdge(ca,v2,v4);
        graph.addEdge(ab,v0,v3);
        graph.addEdge(ad,v3,v4);
        graph.addEdge(cd,v4,v7);
        graph.addEdge(bd,v3,v5);
        graph.addEdge(bt,v5,v6);
        graph.addEdge(dt,v6,v7);
        Map<Edge, Integer> edgeFlowMap = new HashMap();
        Function<Edge, Integer> capTransformer =
                new Function<Edge, Integer>(){
                    public Integer apply(Edge edge)  {
                        return edge.getWeight();
                    }
                };
        // This Factory produces new edges for use by the algorithm

        Supplier<Edge> edgeFactory = new Supplier<Edge>() {
            public Edge get() {
                return new Edge();
            } };
        EdmondsKarpMaxFlow<Vertex, Edge> alg =
                new EdmondsKarpMaxFlow(graph,v0, v7, capTransformer, edgeFlowMap,
                        edgeFactory);
        alg.evaluate();
        //System.out.println("The max flow is: " + alg.getMaxFlow());
        assertThrows(IllegalArgumentException.class, ()->{
            EdmondKarp edmondKarp = new EdmondKarp(graph, v0, v7);
        });
        FordFulkerson ff = new FordFulkerson(graph, v0 ,v7);

        assertEquals(alg.getMaxFlow(), ff.getMaxFlow());
    }


    @Test
    void testWithNotFlowNetwork() throws UnreachableVertexException{

        for (int i = 0; i< 100; i++) {
            DirectedSparseGraph<Vertex, Edge> graph = (DirectedSparseGraph<Vertex, Edge>) GraphFactory.createBIGJUNGGraph();

            List<Vertex> vertices = new ArrayList<>(graph.getVertices());

            int sourceIndex = ThreadLocalRandom.current().nextInt(0, vertices.size());
            int targetIndex = ThreadLocalRandom.current().nextInt(0, vertices.size());
            while (sourceIndex == targetIndex) {
                targetIndex = ThreadLocalRandom.current().nextInt(0, vertices.size());
            }

            Vertex s = vertices.get(sourceIndex);
            Vertex t = vertices.get(targetIndex);
            FordFulkerson fordFulkerson = new FordFulkerson(graph, s, t);
            Function<Edge, Integer> capTransformer =
                    new Function<Edge, Integer>() {
                        public Integer apply(Edge edge) {
                            return edge.getWeight();
                        }
                    };
            Map<Edge, Integer> edgeFlowMap = new HashMap();
            Supplier<Edge> edgeFactory = new Supplier<Edge>() {
                public Edge get() {
                    return new Edge();
                }
            };
            EdmondsKarpMaxFlow<Vertex, Edge> alg =
                    new EdmondsKarpMaxFlow(graph, s, t, capTransformer, edgeFlowMap,
                            edgeFactory);
            alg.evaluate();
            EdmondKarp edmondKarp = new EdmondKarp(graph, s, t);


            assertEquals(alg.getMaxFlow(), fordFulkerson.getMaxFlow());
            assertEquals(alg.getMaxFlow(), edmondKarp.getMaxFlow());
        }

    }
}