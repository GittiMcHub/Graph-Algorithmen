package com.haw.avdt.algorithm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import edu.uci.ics.jung.graph.AbstractTypedGraph;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Gibt Distanz und Pfad mithilfe des Dijkstra Algorithmus für einen gewichteten Graphen zurück.
 *
 * getPath(Vertex, Vertex) → List<Vertex> ! UnreachableVertexException
 * getDistance(Vertex, Vertex) → Integer ! UnreachableVertexException
 *
 * @param <T>
 */
public class Dijkstra<T extends AbstractTypedGraph<Vertex, Edge>> {

    private AbstractTypedGraph<Vertex, Edge> sourceGraph;
    private int amountOfVertexes;

    private int[][] information;                            // die Dijkstra Tabelle
    private BiMap<Integer, Vertex> numbersToVertecies;      // HilfsMap fuer Index->Vertex oder Vertex->Index


    private Map<Vertex, int[][]> vertexArrayMap;            // Cache fuer Dijkstra Tabellen
    private Map<Vertex, BiMap> vertexNumeration;            // Cache fuer HilfsMap

    // Tabellen Konstanten
    private static int DISTANCE = 0;
    private static int PREVIOUS = 1;
    private static int OK = 2;
    private static int TRUE = 1;
    private static int FALSE = 0;

    private int counter;

    /**
     * Konstruktor fuer Dijsktra Klasse
     * Initialisiert die Klassenvariablen und den Cache
     * @param sourceGraph JUNG Graph
     */
    public Dijkstra(T sourceGraph) {
        vertexNumeration = new HashMap<>();
        numbersToVertecies = HashBiMap.create();
        vertexArrayMap = new HashMap<>();
        this.sourceGraph = sourceGraph;
        this.amountOfVertexes = sourceGraph.getVertexCount();
        this.information = new int[3][amountOfVertexes];

        counter = 0;
    }


    /**
     *  Liefert den kuerzesten Weg zwischen Source und Target Vertex
     *
     * @param source Vertex
     * @param target Vertex
     * @return List mit den Vertexen als Pfad
     * @throws UnreachableVertexException wenn keine Verbindung vorhanden
     */
    public ArrayList<Vertex> getPath(Vertex source, Vertex target) throws UnreachableVertexException {
        if (!sourceGraph.containsVertex(source) || !sourceGraph.containsVertex(target))
            throw new UnreachableVertexException("Diese Vertices gehoeren nicht zum Graph.");
        checkIfInformationAlreadyExists(source);
        Map<Vertex, Integer> vertexToNumbers = numbersToVertecies.inverse();
        int indexOfSource = vertexToNumbers.get(source);
        int searchIndex = vertexToNumbers.get(target);
        ArrayList<Vertex> path = new ArrayList<>();
        path.add(target);
        while (searchIndex != indexOfSource) {
            if (searchIndex==-1)
                throw new UnreachableVertexException("Kein Weg zwischen "+source+" und "+target);
            searchIndex = information[PREVIOUS][searchIndex];
            path.add(0, numbersToVertecies.get(searchIndex));
        }
        return path;
    }

    /**
     * Gibt zu einem gegeben Source Vertex die Distanz zum gegeben Target Vertex
     *
     * @param source Vertex
     * @param target Vertex
     * @return int distanz
     * @throws UnreachableVertexException wenn keine Verbindung gefunden
     */
    public int getDistance(Vertex source, Vertex target) throws UnreachableVertexException {
        if (!sourceGraph.containsVertex(source) || !sourceGraph.containsVertex(target))
            throw new UnreachableVertexException("Diese Vertices gehoeren nicht zum Graph.");
        checkIfInformationAlreadyExists(source);
        int targetIndex = numbersToVertecies.inverse().get(target);
        if (information[DISTANCE][targetIndex] == Integer.MAX_VALUE)
            throw new UnreachableVertexException("Kein Weg zwischen "+source+" und "+target);
        return information[DISTANCE][targetIndex];
    }

    /**
     * Interne pruefmethode, ob die Dijkstra Tabelle fuer den gegebenen Source Vertex
     * schon mal berechnet wurde
     * @param source Vertex
     */
    private void checkIfInformationAlreadyExists(Vertex source) {
        if (this.vertexArrayMap.containsKey(source)) {
            information = vertexArrayMap.get(source);
            numbersToVertecies = vertexNumeration.get(source);
        } else {
            createNewTable(source);
        }
    }


    /**
     * Erstellt fuer den gegeben Source Vertex die Dijkstra Tabelle und speichert diese im Cache
     *
     * @param source Vertex
     */
    private void createNewTable(Vertex source) {
        this.counter = 0;

        int[][] informationNEW = new int[3][amountOfVertexes];
        BiMap<Integer, Vertex> numbersToVerteciesNEW = HashBiMap.create();
        //Vorbereitung
        //Durchnummerieren. Source Vertex soll immer an Index 0 gespeichert sein.
        numbersToVerteciesNEW.put(0, source);
        List<Vertex> restOfVerticies = sourceGraph.getVertices().stream().filter(vertex -> vertex != source).collect(Collectors.toList());
        this.counter += 1;

        for (int i = 0; i < restOfVerticies.size(); i++) {
            numbersToVerteciesNEW.put(i + 1, restOfVerticies.get(i));
        }
        //Der erste Vertex hat die Entfernung 0 und der Vorgaenger ist er selbst.
        informationNEW[DISTANCE][0] = 0;
        informationNEW[PREVIOUS][0] = 0;
        informationNEW[OK][0] = FALSE;
        //Alle andere haben die Entfernung "unendlich" (Hier Max Value von Integer). Vorgaenger sind noch nicht bestimmt.
        //Alle Vertexe haben den Wert False in der OK-Spalte, weil die noch nicht geprueft wurden.
        for (int i = 1; i < amountOfVertexes; i++) {
            informationNEW[DISTANCE][i] = Integer.MAX_VALUE;
            informationNEW[PREVIOUS][i] = -1;
            informationNEW[OK][i] = FALSE;
        }

        // Liste der Vertexe, die in der OK-Spalte False haben. Am Anfang alle.
        List<Integer> leftToCheckVertecies = new ArrayList<>();
        for (int i = 0; i < amountOfVertexes; i++) {
            leftToCheckVertecies.add(i);
        }

        //Iteration
        while (leftToCheckVertecies.size() > 0) {
            //Bestimmung des Vertexes(Index), dessen Entfernung am kleinsten ist.
            //(OK Wert muss False sein. Kein Ueberpruefung, weil nur solche Vertexe im leftToCheckVertecies Liste sind)
            int indexOfSmallest = leftToCheckVertecies.get(0);
            int smallestDistance = informationNEW[DISTANCE][indexOfSmallest];
            for (Integer newCandidate : leftToCheckVertecies) {
                if (informationNEW[DISTANCE][newCandidate] < smallestDistance) {
                    smallestDistance = informationNEW[DISTANCE][newCandidate];
                    indexOfSmallest = newCandidate;
                }
            }

            //Dieser Vertex(Index) ist jetzt gecheckt, deswegen OK-Spalten Wert auf TRUE und Entfernen aus der leftToCheckVertecies Liste
            informationNEW[OK][indexOfSmallest] = TRUE;
            leftToCheckVertecies.remove(Integer.valueOf(indexOfSmallest));
            //Vertex Objekt, der dem Index entspricht
            Vertex baseVertex = numbersToVerteciesNEW.get(indexOfSmallest);

            //Hier werden alle Vertecies(Index), die Nachbarn des pefrüften Vertex sind, ueberpruft.
            // Wenn die neue Entfernung (Entfernung vom Gecheckten + Distanz zwischen beiden) kleiner als die ist, die der Nachbar schon hat,
            // dann wird die Enfernung vom Nachbarn ueberschrieben.
            Collection<Edge> outEdges = sourceGraph.getOutEdges(baseVertex);
            this.counter += 1;

            for (Edge outEdge : outEdges) {
                int distanceBetweenTwo = outEdge.getWeight();
                Optional<Vertex> optionalVertex = sourceGraph.getIncidentVertices(outEdge).stream().filter(v -> v != baseVertex).findFirst();
                this.counter += 1;

                if(optionalVertex.isPresent()) {
                    Vertex neighborVertex = optionalVertex.get();
                    int indexOfNeighbor = numbersToVerteciesNEW.inverse().get(neighborVertex);
                    if (informationNEW[OK][indexOfNeighbor] == FALSE && informationNEW[DISTANCE][indexOfSmallest] + distanceBetweenTwo < informationNEW[DISTANCE][indexOfNeighbor]) {
                        informationNEW[DISTANCE][indexOfNeighbor] = informationNEW[DISTANCE][indexOfSmallest] + distanceBetweenTwo;
                        informationNEW[PREVIOUS][indexOfNeighbor] = indexOfSmallest;
                    }
                }
            }
        }
        //Zuordne zu diesem Source Vertex entastande Tabelle.
        vertexArrayMap.put(source, informationNEW);
        vertexNumeration.put(source, numbersToVerteciesNEW);
        information = informationNEW;
        numbersToVertecies = numbersToVerteciesNEW;
    }


    public int getCounter() {
        return counter;
    }
}
