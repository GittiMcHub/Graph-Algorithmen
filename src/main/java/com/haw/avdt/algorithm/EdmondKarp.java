
package com.haw.avdt.algorithm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.stream.Collectors;

/**
 * Gibt den Maximalen Fluss innerhalb des übergebenen Netzwerkes aus.
 * Dieser Algorithmus verwendet im Gegensatz zum Ford-Fulkerson Algorithmus den BFS Algortihmus zum finden eines Pfades von der Quelle zur Senke.
 */
public class EdmondKarp {
    private DirectedSparseGraph<Vertex, Edge> network;
    private Vertex source;
    private Vertex target;
    private int maxFlow;
    private int[][] restCapacity;
    private BiMap<Integer, Vertex> numbersToVertices;
    private int amountOfVertices;

    public EdmondKarp(DirectedSparseGraph<Vertex, Edge> network, Vertex source, Vertex target) throws UnreachableVertexException {
        this.network = getCopyOfNetwork(network);
        if (!network.containsVertex(source) || !network.containsVertex(target))
            throw new UnreachableVertexException("One/Both of your Vertices doesn't belong to this network(graph)");
        this.source = source;
        this.target = target;

        amountOfVertices = network.getVertexCount();
        maxFlow = 0;
        numbersToVertices = getNumeration();
        restCapacity = new int[amountOfVertices][amountOfVertices];
        initRestCapacity();
        calculateMaxFlow();
        if (maxFlow == 0)
            throw new UnreachableVertexException("Target is unreachable. Could be that one of capacities on the way to target is 0.");
    }

    private void initRestCapacity() {
        for (Edge edge : network.getEdges()) {
            int sourceIndex = numbersToVertices.inverse().get(network.getSource(edge));
            int destIndex = numbersToVertices.inverse().get(network.getDest(edge));
            if (edge.getWeight() < 0)
                throw new IllegalArgumentException("This implementation does not accept negative capacity.");
            restCapacity[sourceIndex][destIndex] = edge.getWeight();
        }
    }

    private DirectedSparseGraph<Vertex,Edge> getCopyOfNetwork(DirectedSparseGraph<Vertex,Edge> original) {
        DirectedSparseGraph<Vertex,Edge> copy = new DirectedSparseGraph();
        original.getVertices().forEach(v -> copy.addVertex(v));
        original.getEdges().forEach(e -> copy.addEdge(e, original.getEndpoints(e)));
        return copy;
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    private void calculateMaxFlow(){
        ArrayList<Edge> shortestWay;
        while (true) {
            try {
                shortestWay = getShortestWay();
            } catch (UnreachableVertexException e) {
                return;
            }
            int minCapacity = getMinCapacity(shortestWay);
            maxFlow += minCapacity;
            for (Edge edge : shortestWay) {
                Vertex edgeSource = network.getSource(edge);
                Vertex edgeDest = network.getDest(edge);
                int outIndex = numbersToVertices.inverse().get(edgeSource);
                int inIndex = numbersToVertices.inverse().get(edgeDest);
                restCapacity[outIndex][inIndex] -= minCapacity;
                if (restCapacity[outIndex][inIndex] == 0)
                    network.removeEdge(edge);

                Edge backEdge = network.findEdge(edgeDest, edgeSource);
                if (backEdge == null) {
                    backEdge = new Edge(edge.getName() + "reverse", edge.getWeight());
                    network.addEdge(backEdge, edgeDest, edgeSource);
                    restCapacity[inIndex][outIndex] = minCapacity;
                } else {
                restCapacity[inIndex][outIndex] += minCapacity;}
                if (restCapacity[inIndex][outIndex] == 0)
                    network.removeEdge(backEdge);
            }
        }
    }
    private BiMap<Integer, Vertex> getNumeration(){
        BiMap<Integer, Vertex> numeration = HashBiMap.create();
        int number = 0;
        numeration.put(number, source);
        ArrayList<Vertex> restVertices = (ArrayList<Vertex>) network.getVertices().stream().filter(v -> v!=source && v!=target).collect(Collectors.toList());
        for (Vertex restVertex : restVertices) {
            numeration.put(++number, restVertex);
        }
        numeration.put(++number, target);
        return numeration;
    }

    private ArrayList<Edge> getShortestWay() throws UnreachableVertexException {
        BFS bfs = new BFS(network);
        ArrayList<Edge> edgeWay = new ArrayList<>();
        HashMap<String, Object> results = bfs.getDistanceAndPath(source, target);
        ArrayList<Vertex> verteciesWay = (ArrayList<Vertex>)results.get("path");
        for (int i = 0; i < verteciesWay.size()-1; i++){
            edgeWay.add(network.findEdge(verteciesWay.get(i), verteciesWay.get(i+1)));
        }
        return edgeWay;
    }

    private int getMinCapacity(ArrayList<Edge> shortestWay){
        Edge firstEdge = shortestWay.get(0);
        int outIndex = numbersToVertices.inverse().get(network.getSource(firstEdge));
        int inIndex = numbersToVertices.inverse().get(network.getDest(firstEdge));
        int minCapacity = restCapacity[outIndex][inIndex];
        for (Edge edge : shortestWay) {
            outIndex = numbersToVertices.inverse().get(network.getSource(edge));
            inIndex = numbersToVertices.inverse().get(network.getDest(edge));
            if (restCapacity[outIndex][inIndex] < minCapacity) {
                minCapacity = restCapacity[outIndex][inIndex];
            }
        }
        return minCapacity;
    }

}

