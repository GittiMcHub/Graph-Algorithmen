package com.haw.avdt.algorithm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haw.avdt.algorithm.ex.FoundCycleWithNegativeSum;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.*;

/**
 * Gibt Distanz und Pfad mithilfe des Floyd-Warshall Algorithmus für einen gewichteten Graphen zurück.
 * @param <T>
 */
public class FloydWarshall<T extends AbstractTypedGraph<Vertex, Edge>> {
    private AbstractTypedGraph<Vertex, Edge> sourceGraph;
    private int[][] distance;
    private int[][] transit;
    private int amountOfVertices;
    private BiMap<Integer, Vertex> numbersToVertices;
    private Map<String, Edge> verticesIndexToEdge;
    private int accessCount;

    public int getAccessCount() {
        return accessCount;
    }

    public FloydWarshall(T sourceGraph) throws FoundCycleWithNegativeSum {
        this.sourceGraph = sourceGraph;
        amountOfVertices = sourceGraph.getVertexCount();
        accessCount = 1;
        distance = new int[amountOfVertices][amountOfVertices];
        transit = new int[amountOfVertices][amountOfVertices];
        numbersToVertices = getNumeration();
        verticesIndexToEdge = getVerticesIndexToEdgeMap();
        setTables();
    }

    public int getDistance(Vertex source, Vertex target) throws UnreachableVertexException {
        if (!sourceGraph.containsVertex(source) || !sourceGraph.containsVertex(target))
            throw new UnreachableVertexException("Diese Vertices gehoeren nicht zum Graph.");
        int sourceIndex = numbersToVertices.inverse().get(source);
        int targetIndex = numbersToVertices.inverse().get(target);
        if (distance[sourceIndex][targetIndex] >= Integer.MAX_VALUE / 2)
            throw new UnreachableVertexException("Kein Weg zwischen " + source + " und " + target);
        return distance[sourceIndex][targetIndex];
    }

    public List<Vertex> getPath(Vertex source, Vertex target) throws UnreachableVertexException {
        if (!sourceGraph.containsVertex(source) || !sourceGraph.containsVertex(target))
            throw new UnreachableVertexException("Diese Vertices gehoeren nicht zum Graph.");
        int sourceIndex = numbersToVertices.inverse().get(source);
        if (sourceIndex == -1)
            throw new UnreachableVertexException("Kein Weg zwischen " + source + " und " + target);
        List<Vertex> path = new ArrayList<>();
        path.add(source);
        path.addAll(rekursivPath(source, target));
        return path;
    }

    private List<Vertex> rekursivPath(Vertex source, Vertex target) {
        int sourceIndex = numbersToVertices.inverse().get(source);
        int targetIndex = numbersToVertices.inverse().get(target);
        List<Vertex> path = new ArrayList<>();
        if (transit[sourceIndex][targetIndex] != -1) {
            path.addAll(rekursivPath(source, numbersToVertices.get(transit[sourceIndex][targetIndex])));
            path.addAll(rekursivPath(numbersToVertices.get(transit[sourceIndex][targetIndex]), target));
        } else
            path.add(target);
        return path;

    }

    private void setTables() throws FoundCycleWithNegativeSum {
        for (int i = 0; i < amountOfVertices; i++) {
            for (int j = 0; j < amountOfVertices; j++) {
                if (i == j)
                    distance[i][j] = 0;
                else if (verticesIndexToEdge.containsKey(i + " " + j))
                    distance[i][j] = verticesIndexToEdge.get(i + " " + j).getWeight();
                else
                    distance[i][j] = Integer.MAX_VALUE / 2;

                transit[i][j] = -1;
            }
        }
        for (int j = 0; j < amountOfVertices; j++) {
            for (int i = 0; i < amountOfVertices; i++) {
                if (i != j) {
                    for (int k = 0; k < amountOfVertices; k++) {
                        if ((distance[i][k] > distance[i][j] + distance[j][k]) && (k != j)) {
                            distance[i][k] = distance[i][j] + distance[j][k];
                            transit[i][k] = j;

                        }
                    }
                } else {
                    if (distance[i][i] < 0)
                        throw new FoundCycleWithNegativeSum("Kreis bei dem Vertex " + numbersToVertices.get(i));
                }
            }
        }
    }

    private BiMap<Integer, Vertex> getNumeration() {
        BiMap<Integer, Vertex> map = HashBiMap.create();
        List<Vertex> vertices = new ArrayList<>(sourceGraph.getVertices());
        accessCount++;
        for (int i = 0; i < amountOfVertices; i++) {
            map.put(i, vertices.get(i));
        }
        return map;
    }

    private Map<String, Edge> getVerticesIndexToEdgeMap() {
        Map<String, Edge> map = new HashMap<>();
        List<Edge> edges = new ArrayList<>(sourceGraph.getEdges());
        accessCount++;
        for (Edge edge : edges) {
            List<Vertex> vertices = new ArrayList<>(sourceGraph.getIncidentVertices(edge));
            accessCount++;
            Vertex sourceVertex = vertices.get(0);
            Vertex targetVertex = vertices.get(1);
            int sourceIndex = numbersToVertices.inverse().get(sourceVertex);
            int targetIndex = numbersToVertices.inverse().get(targetVertex);
            map.put(sourceIndex + " " + targetIndex, edge);
            if (sourceGraph.getDefaultEdgeType() == EdgeType.UNDIRECTED)
                map.put(targetIndex + " " + sourceIndex, edge);
        }
        return map;
    }
}
