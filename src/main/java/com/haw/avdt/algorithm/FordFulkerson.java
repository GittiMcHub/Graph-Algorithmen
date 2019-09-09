package com.haw.avdt.algorithm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Gibt den Maximalen Fluss innerhalb des übergebenen Netzwerkes aus.
 */
public class FordFulkerson {
    private DirectedSparseGraph<Vertex, Edge> network;
    private Map<Edge, Integer> edgeToFlow;
    private BiMap<Integer, Vertex> numbersToVertices;
    private Vertex source;
    private Vertex target;
    private int[][] vertexMatrix;
    private int amountOfVertecis;
    private static int IN_VERTEX = 0;
    private static int OUT_VERTEX = 1;
    private static int INCREMENT = 2;
    private static int MARKED = 3;
    private static int INSPECTED = 4;
    private int maxFlow;

    private int TARGET_INDEX;

    public FordFulkerson(DirectedSparseGraph<Vertex, Edge> network, Vertex source, Vertex target) throws UnreachableVertexException {
        this.network = network;
        if (!network.containsVertex(source) || !network.containsVertex(target))
            throw new UnreachableVertexException("One/Both of your Vertices doesn't belong to this network(graph)");
        this.source = source;
        this.target = target;
        numbersToVertices = getNumeration();
        edgeToFlow = new HashMap<>();
        this.network.getEdges().forEach(e -> edgeToFlow.put(e, 0));
        amountOfVertecis = network.getVertexCount();
        vertexMatrix = new int[amountOfVertecis][5];
        //setting source (quelle) to (undef, INFINITY)
        vertexMatrix[0][INCREMENT] = Integer.MAX_VALUE;
        vertexMatrix[0][MARKED] = 1;
        vertexMatrix[0][IN_VERTEX] = -2;
        vertexMatrix[0][OUT_VERTEX] = -2;
        vertexMatrix[0][INSPECTED] = 0;
        setINandOUTVertecis();
        maxFlow = 0;
        calculateMaxFlow();
        if (maxFlow == 0)
            throw new UnreachableVertexException("Target is unreachable. Could be that one of capacities on the way to target is 0.");
    }

    private void setINandOUTVertecis() {
        for (int i = 1; i < vertexMatrix.length; i++) {
            vertexMatrix[i][IN_VERTEX] = -1;
            vertexMatrix[i][OUT_VERTEX] = -1;
        }
    }
    private void reset() {
        for (int i = 1; i < vertexMatrix.length; i++) {
            vertexMatrix[i][IN_VERTEX] = -1;
            vertexMatrix[i][OUT_VERTEX] = -1;
            vertexMatrix[i][MARKED] = 0;
            vertexMatrix[i][INCREMENT] = 0;
            vertexMatrix[i][INSPECTED] = 0;
        }
        vertexMatrix[0][INSPECTED] = 0;
    }

    private void calculateMaxFlow() {
        Vertex baseVertex;
        while (true) {
            for (int i = 0; i < vertexMatrix.length; i++) {
                if (vertexMatrix[i][MARKED] == 1 && vertexMatrix[i][INSPECTED] == 0) {
                    baseVertex = numbersToVertices.get(i);
                    int baseVertexIndex = i;
                    network.getOutEdges(baseVertex).forEach(e -> {
                        Vertex dest = network.getDest(e);
                        int destIndex = numbersToVertices.inverse().get(dest);
                        if (vertexMatrix[destIndex][MARKED] == 0 && edgeToFlow.get(e) < e.getWeight()) {
                            vertexMatrix[destIndex][IN_VERTEX] = baseVertexIndex;
                            vertexMatrix[destIndex][MARKED] = 1;
                            vertexMatrix[destIndex][INCREMENT] = Math.min(e.getWeight() - edgeToFlow.get(e), vertexMatrix[baseVertexIndex][INCREMENT]);
                        }
                    });
                    network.getInEdges(baseVertex).forEach(e -> {
                        Vertex source = network.getSource(e);
                        int sourceIndex = numbersToVertices.inverse().get(source);
                        if (vertexMatrix[sourceIndex][MARKED] == 0 && edgeToFlow.get(e) > 0) {
                            vertexMatrix[sourceIndex][OUT_VERTEX] = baseVertexIndex;
                            vertexMatrix[sourceIndex][MARKED] = 1;
                            vertexMatrix[sourceIndex][INCREMENT] = Math.min(edgeToFlow.get(e), vertexMatrix[baseVertexIndex][INCREMENT]);
                        }
                    });
                    vertexMatrix[baseVertexIndex][INSPECTED] = 1;

                    if (isAllInspected())
                        return;

                    if (vertexMatrix[TARGET_INDEX][MARKED] == 1) {
                        int previosIndex = TARGET_INDEX;
                        int flowToAdd = vertexMatrix[TARGET_INDEX][INCREMENT];
                        maxFlow+=flowToAdd;
                        while(vertexMatrix[previosIndex][IN_VERTEX] != -2) {
                            if (vertexMatrix[previosIndex][IN_VERTEX] != -1 && vertexMatrix[previosIndex][OUT_VERTEX] == -1 ){
                                Vertex vertexDest = numbersToVertices.get(previosIndex);
                                Vertex vertexSource = numbersToVertices.get(vertexMatrix[previosIndex][IN_VERTEX]);
                                Edge edge = network.findEdge(vertexSource, vertexDest);
                                edgeToFlow.replace(edge, edgeToFlow.get(edge)+flowToAdd);
                                previosIndex = vertexMatrix[previosIndex][IN_VERTEX];
                            }
                            else if (vertexMatrix[previosIndex][IN_VERTEX] == -1 && vertexMatrix[previosIndex][OUT_VERTEX] != -1 ) {
                                Vertex vertexSource = numbersToVertices.get(previosIndex);
                                Vertex vertexDest = numbersToVertices.get(vertexMatrix[previosIndex][OUT_VERTEX]);
                                Edge edge = network.findEdge(vertexSource, vertexDest);
                                edgeToFlow.replace(edge, edgeToFlow.get(edge)-flowToAdd);
                                previosIndex = vertexMatrix[previosIndex][OUT_VERTEX];
                            }
                            else{
                                throw new IllegalArgumentException("Bad incrementing");
                            }
                        }
                        reset();
                    }
                    break;
                }


            }
        }

    }

    private boolean isAllInspected() {
        boolean allInspected = true;
        for (int j = 0; j < vertexMatrix.length; j++) {
            if (vertexMatrix[j][MARKED] == 1 && vertexMatrix[j][INSPECTED] != 1)
                allInspected = false;
        }
        return allInspected;
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
        TARGET_INDEX = number;
        return numeration;
    }


    public int getMaxFlow() {
        return maxFlow;
    }

}
