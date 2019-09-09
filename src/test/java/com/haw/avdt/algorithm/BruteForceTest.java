package com.haw.avdt.algorithm;

import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.view.GraphViz;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class BruteForceTest {

    @Test
    void getAllPaths() throws UnreachableVertexException {

        DirectedSparseGraph<Vertex, Edge> simpleGraph = this.getSimpleGraph();
        new GraphViz(simpleGraph);

        BruteForce bf = new BruteForce(simpleGraph);

        Vertex source = simpleGraph.getVertices().stream().filter(v -> v.getName().equals("s")).findFirst().get();
        Vertex target = simpleGraph.getVertices().stream().filter(v -> v.getName().equals("t")).findFirst().get();

        List<List<Edge>> allPaths = bf.getAllPaths(source, target);

        System.out.println("Got " + allPaths.size());

        //Thread.sleep(100000);
    }


    @Test
    void getAllPathsFromBigGraph() throws UnreachableVertexException {
        AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createBIGJUNGGraph();
        List<Vertex> vertices = new ArrayList<>(graph.getVertices());

        int sourceIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        int targetIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());

        // im Falle, dass zufaellig zwei mal der gleiche wert erzeugt wird...
        while(sourceIndex == targetIndex){
            targetIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        }

        BruteForce bf = new BruteForce(graph);
        List<List<Edge>> allPaths = bf.getAllPaths(vertices.get(sourceIndex), vertices.get(targetIndex));

        System.out.println("Paths: " + allPaths.size());
    }


    private DirectedSparseGraph<Vertex, Edge> getSimpleGraph() {
        Vertex s = new Vertex("s");
        Vertex t = new Vertex("t");
        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");
        Vertex c = new Vertex("c");
        Vertex d = new Vertex("d");

        //Edge sa = new Edge("s->a", 10);
        Edge sc = new Edge("s->c", 5);
        Edge ca = new Edge("c->a", 6);
        Edge ab = new Edge("a->b", 4);
        Edge ad = new Edge("a->d", 5);
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

        //graph.addEdge(sa,s,a);
        graph.addEdge(sc,s,c);
        graph.addEdge(ca,c,a);
        graph.addEdge(ab,a,b);
        graph.addEdge(ad,a,d);
        graph.addEdge(cd,c,d);
        graph.addEdge(bd,b,d);
        graph.addEdge(bt,b,t);
        graph.addEdge(dt,d,t);

        return graph;
    }
}