package com.haw.avdt.algorithm;

import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.util.gka.GKAFileReader;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {



    @Test
    void testNegativeEdges() throws UnreachableVertexException {
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
        Dijkstra dijkstra = new Dijkstra(graphWithNegativeEdges);
        assertNotEquals(1, dijkstra.getDistance(v1, v3));
    }
        /** Dijkstra
         * Testen Sie für graph3 in graph3.gka
         */
    @Test
    void shortestPathFromGKAFile(){

        String gkaFile = "graph03.gka";

        try {

            // Kuerzester Pfad: [Vertex{Husum}, Vertex{Kiel}, Vertex{Uelzen}, Vertex{Rotenburg}]
            GKAGraphDescripton testGKA = GKAFileReader.read(Paths.get(ClassLoader.getSystemClassLoader().getResource(gkaFile).toURI()).toString());
            AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createJUNGGraph(testGKA);
            Dijkstra ownDijkstra = new Dijkstra(graph);
            DijkstraShortestPath<Vertex, Edge> jungDijkstra = new DijkstraShortestPath<>(graph, (e) -> e.getWeight());

            // Test Vertexe
            Vertex srcHusum = graph.getVertices().stream().filter(v -> v.getName().equals("Husum")).findFirst().get();
            Vertex targetRotenburg = graph.getVertices().stream().filter(v -> v.getName().equals("Rotenburg")).findFirst().get();

            this.oursVersusJUNGDijkstra(graph, srcHusum, targetRotenburg, ownDijkstra, jungDijkstra);
        } catch (Exception e) {
           fail();
        }

    }

    /**
     * Testet, ob unreachable Vertexe auch erkannt werden
     */
    @Test
    void testUnreachableException(){

        AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createRandomDirectedJUNGGraph(10,1);
        Dijkstra ownDijkstra = new Dijkstra(graph);
        DijkstraShortestPath<Vertex, Edge> jungDijkstra = new DijkstraShortestPath<>(graph, (e) -> e.getWeight());

        List<Vertex> vertices = new ArrayList<>(graph.getVertices());
        // Hier muss mindestens einer rauskommen, der die Exception wirft
        // Da jeder Vertex mit jedem Vertex geprueft wird, aber nur eine Edge vorhanden ist
        assertThrows(UnreachableVertexException.class, () -> {
            for(int i = 0; i < vertices.size(); i++){
                int sourceIndex = i;

                for(int y = 0; y < vertices.size(); y++){
                    int targetIndex = y;
                    if(sourceIndex != targetIndex){
                        oursVersusJUNGDijkstra(graph,vertices.get(sourceIndex), vertices.get(targetIndex), ownDijkstra, jungDijkstra);
                    }
                }
            }
        });
    }

    /**
     * Implementierung eines gerichteten Graphen BIG mit 100 Knoten und etwa 3500 Kanten. Beschreiben
     * Sie die Konstruktion von BIG und weisen Sie für zwei nicht-triviale Knotenpaare die kürzesten Wege
     * nach.
     */
    @Test
    void shortestPathFromBIGWithAllVertecies() throws UnreachableVertexException {
        AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createBIGJUNGGraph();
        Dijkstra ownDijkstra = new Dijkstra(graph);
        DijkstraShortestPath<Vertex, Edge> jungDijkstra = new DijkstraShortestPath<>(graph, (e) -> e.getWeight());

        List<Vertex> vertices = new ArrayList<>(graph.getVertices());
        for(int i = 0; i < vertices.size(); i++){
            int sourceIndex = i;

            for(int y = 0; y < vertices.size(); y++){
                int targetIndex = y;
                if(sourceIndex != targetIndex){
                    oursVersusJUNGDijkstra(graph, vertices.get(sourceIndex), vertices.get(targetIndex), ownDijkstra, jungDijkstra);
                }
            }
        }
    }


    /**
     * Implementierung eines gerichteten Graphen BIG mit 100 Knoten und etwa 3500 Kanten. Beschreiben
     * Sie die Konstruktion von BIG und weisen Sie für zwei nicht-triviale Knotenpaare die kürzesten Wege
     * nach.
     */
    @Test
    void shortestPathFromBIGWithRandomVertecies() throws UnreachableVertexException {
        AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createBIGJUNGGraph();
        Dijkstra ownDijkstra = new Dijkstra(graph);
        DijkstraShortestPath<Vertex, Edge> jungDijkstra = new DijkstraShortestPath<>(graph, (e) -> e.getWeight());

        List<Vertex> vertices = new ArrayList<>(graph.getVertices());

        int sourceIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        int targetIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());

        // im Falle, dass zufaellig zwei mal der gleiche wert erzeugt wird...
        while(sourceIndex == targetIndex){
            targetIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        }

        oursVersusJUNGDijkstra(graph,vertices.get(sourceIndex), vertices.get(targetIndex), ownDijkstra, jungDijkstra);
    }

    /**
     * Dieser Testprueft, ob die Klasse auch bei mehrmaliger Nutzung noch das korrekte
     * Ergebnis produziert.
     *
     * Die Klasse ist Mutable Designed, um aus Performancegruenden einen Cache zu fuellen
     * Dieser Cache soll dafuer sorgen, dass die Tabelle fuer ein Source Vertex nicht jedes mal
     * neu aufgebaut wird, sondern die Ergebnisse aus einer vorherigen Abfgrage genutzt werden koennen
     *
     * Nur bei noch nicht abgefragten source Vertexen wird eine weitere Durchfuerhung des Algorithmus benoetigt
     *
     * @throws UnreachableVertexException
     */
    @Test
    void testCacheForAlreadyAnalyzedSourceVertecies() throws UnreachableVertexException{
        // GIVEN - Graph, eine Pfad Abfrage von Quelle -> Ziel
        AbstractTypedGraph<Vertex, Edge> graph = GraphFactory.createBIGJUNGGraph();
        List<Vertex> vertices = new ArrayList<>(graph.getVertices());

        int firstSourceIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        int firstTargetIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        // im Falle, dass zufaellig zwei mal der gleiche wert erzeugt wird..., und index soll mindestens um 3 stellen verschoben sein, damit unten nicht nochmal zufall benutzt werden muss
        while(firstSourceIndex == firstTargetIndex && (firstSourceIndex - firstTargetIndex > -3 && firstSourceIndex - firstTargetIndex < 3)){
            firstTargetIndex = ThreadLocalRandom.current().nextInt(0,vertices.size());
        }

        Dijkstra dijkstra = new Dijkstra(graph);

        ArrayList<Vertex> firstResult = dijkstra.getPath(vertices.get(firstSourceIndex), vertices.get(firstTargetIndex));
        int firstDistance = dijkstra.getDistance(vertices.get(firstSourceIndex), vertices.get(firstTargetIndex));

        int secondSourceIndex = firstSourceIndex == 0 ? 1 : firstSourceIndex -1;
        int secondTargetIndex = firstTargetIndex == vertices.size()-1 ? vertices.size()-1 : firstTargetIndex + 1;

        // WHEN - Irgendein anderer Pfad berechnet werden soll
        dijkstra.getPath(vertices.get(secondSourceIndex), vertices.get(secondTargetIndex));
        dijkstra.getDistance(vertices.get(secondSourceIndex), vertices.get(secondTargetIndex));


        // THEN - sollte das Ergebnis noch das gleiche sein
        ArrayList<Vertex> secondResult = dijkstra.getPath(vertices.get(firstSourceIndex), vertices.get(firstTargetIndex));
        int secondDistance = dijkstra.getDistance(vertices.get(firstSourceIndex), vertices.get(firstTargetIndex));

        assertEquals(firstResult,secondResult);

    }


    /**
     * Fuehrt den Dijkstra Algorithmus von unserer Immplementierung,
     * sowie die Implentierung aus der JUNG Bibliothek aus und vergleicht,
     * ob diese den gleichen Weg finden
     * @param graph
     * @param source
     * @param target
     */
    private void oursVersusJUNGDijkstra(AbstractTypedGraph<Vertex, Edge> graph, Vertex source, Vertex target, Dijkstra ownDijkstra, DijkstraShortestPath<Vertex, Edge> jungDijkstra) throws UnreachableVertexException {

        // Test mit built-in disjkstra
        List<Edge> jungDijkstraPathEdges = jungDijkstra.getPath(source, target);
        List<Vertex> jungDijsktraPath = this.edgeListsToVertexList(graph, jungDijkstraPathEdges);

        // Eigenen Dijkstra mit dem vom JUNG vergleichen
        List<Vertex> ownDijkstraPath = ownDijkstra.getPath(source, target);

        // Gleich viele Vertexe
        assertEquals(jungDijsktraPath.size(),ownDijkstraPath.size());
        // Gleiche Reihenfolge
        for(int i = 0; i < jungDijsktraPath.size(); i++){
            //System.out.println("JUNG: " + jungDijsktraPath.get(i).getName() + "\t Own: " + ownDijkstraPath.get(i).getName());
            assertEquals(jungDijsktraPath.get(i),ownDijkstraPath.get(i));
        }
        int distanceJungDijkstra = jungDijkstra.getDistance(source, target).intValue();
        int distanceOurDijkstra = ownDijkstra.getDistance(source, target);
        System.out.println("Distance: " + distanceJungDijkstra + " vs. " + distanceOurDijkstra);
        assertEquals(distanceJungDijkstra,distanceOurDijkstra );
    }


    /**
     * Hilfsmethode um den Rueckgabewert des in JUNG implementierten Dijkstra
     * mit unserem Rueckgabewerten vergleichen zu koennen
     * @param graph
     * @param input
     * @return
     */
    private List<Vertex> edgeListsToVertexList(Graph graph, List<Edge> input){
        List<Vertex> vList = new ArrayList<>();

        for(int i = 0; i < input.size(); i++){

            ArrayList<Vertex> vertices = new ArrayList(graph.getIncidentVertices(input.get(i)));
            Vertex v1 = vertices.get(0);
            Vertex v2 = vertices.get(1);

            if (i+1 < input.size()){
                ArrayList<Vertex> nextVertices = new ArrayList(graph.getIncidentVertices(input.get(i+1)));

                Vertex nextV1 = nextVertices.get(0);
                Vertex nextV2 = nextVertices.get(1);

                // Wenn der ein Vertex bei der naechsten Edge dabei ist, kommt der andere zuerst
                if(v1 == nextV1 || v1 == nextV2){
                    if(!vList.contains(v2)){
                        vList.add(v2);
                    }
                    if(!vList.contains(v1)){
                        vList.add(v1);
                    }
                } else {
                    if(!vList.contains(v1)){
                        vList.add(v1);
                    }
                    if(!vList.contains(v2)){
                        vList.add(v2);
                    }
                }
            } else {
                // Wir sind jetzt bei der letzten Edge. Einer der beiden Vertexe muss ja schon drin
                // sind, daher einfach beide veruschen einzufuegen und nur den speichern
                // der noch nicht drin ist. Das muss der Endvertex seind
                if(!vList.contains(v1)){
                    vList.add(v1);
                }
                if(!vList.contains(v2)){
                    vList.add(v2);
                }
            }
        }
        return vList;
    }



}