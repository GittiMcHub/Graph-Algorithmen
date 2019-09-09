package com.haw.avdt.algorithm;

import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import edu.uci.ics.jung.graph.AbstractTypedGraph;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BFS<T extends AbstractTypedGraph<Vertex, Edge>> {
    private AbstractTypedGraph<Vertex, Edge> sourceGraph;
    private int distance;
    private List<Vertex> path;

    public static String MAPKEY_PATH = "path";
    public static String MAPKEY_DISTANCE = "distance";

    public BFS(T sourceGraph) {
        this.sourceGraph = sourceGraph;
    }

    public HashMap<String, Object> getDistanceAndPath(Vertex source, Vertex target) throws UnreachableVertexException {
        HashMap<String, Object> map = new HashMap<>();

        mark(source, target);
        map.put(MAPKEY_PATH, path);
        map.put(MAPKEY_DISTANCE, distance);
        reset();
        return map;
    }

    private void reset() {
        for (Vertex vertex : sourceGraph.getVertices()) {
            vertex.setMark(-1);
        }
    }


    private void mark(Vertex source, Vertex target) throws UnreachableVertexException {
        source.setMark(0);
        int step = 0;
        while (target.getMark() == -1) {
            int newlyMarked = 0;
            final int stepForLambda = step;
            for (Vertex v : sourceGraph.getVertices().stream().filter(v -> v.getMark() == stepForLambda).collect(Collectors.toSet())) {
                for (Edge e : sourceGraph.getOutEdges(v)) {
                    for (Vertex vertex : sourceGraph.getIncidentVertices(e)) {
                        if (vertex.getMark() == -1) {
                            vertex.setMark(step + 1);
                            newlyMarked++;
                        }
                    }
                }
            }
            if (newlyMarked == 0) {
                reset();
                throw new UnreachableVertexException("There is no connection between the source Vertex and the target Vertex.");
            }
            step++;
        }
        distance = target.getMark();
        path = calculatePath(source, target);

    }

    private List<Vertex> calculatePath(Vertex source, Vertex target) {
        int mark = target.getMark();
        boolean found;
        Vertex previousVertex = target;
        List<Vertex> path = new ArrayList<>();
        path.add(target);
        while (mark != 0) {
            found = false;
            for (Vertex vertex : sourceGraph.getVertices()) {
                if (vertex.getMark() == mark - 1 && !found
                        && sourceGraph.findEdge(vertex, previousVertex)!=null) {
                    path.add(0, vertex);
                    found = true;
                    previousVertex = vertex;
                }
            }
            mark--;
        }
        return path;
    }


}
