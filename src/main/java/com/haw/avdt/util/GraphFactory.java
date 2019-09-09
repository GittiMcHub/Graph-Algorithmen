package com.haw.avdt.util;

import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.gka.GKAGraphComponentDescripton;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Die GraphFactory erzeugt aus den Graphbeschreibungen die
 * Graphen fuer die gewuenschte Libary
 *
 *
 */
public class GraphFactory {


    public final static String RANDOM_NETWORK_DEFAULT_SOURCE_VERTEX_NAME = "s";
    public final static String RANDOM_NETWORK_DEFAULT_TARGET_VERTEX_NAME = "t";

    /**
     * Erzeugt aus der GKA Graphbeschreibung ein AbstractTypedGraph
     * aus der JUNG Libary
     *
     * @param graphDesc GKAFileGraphdescription
     * @return AbstractTypedGraph
     */
    public static AbstractTypedGraph<Vertex, Edge> createJUNGGraph(GKAGraphDescripton graphDesc) {

        AbstractTypedGraph<Vertex, Edge> graph;

        if (graphDesc.isDirected()) {
            graph = new DirectedSparseGraph<>();
        } else {
            graph = new UndirectedSparseGraph<>();
        }

        for (int i = 0; i < graphDesc.getComponents().size(); i++) {
            GKAGraphComponentDescripton comp = graphDesc.getComponents().get(i);

            // Fuer Einzelne Vertexe
            if (!comp.hasTarget()) {
                Vertex sourceVertex = new Vertex(comp.getSourceVertexName());
                // Falls noch nicht vorhanden, einfuegen
                if (!graph.containsVertex(sourceVertex)) {
                    graph.addVertex(sourceVertex);
                }
                continue;
            }

            Vertex source = new Vertex(comp.getSourceVertexName());
            Vertex target = new Vertex(comp.getTargetVertexName());

            Edge edge;
            //String edgeName = comp.doesEdgeHasName() ? comp.getEdgeName() : "e" + i;
            String edgeName = comp.getEdgeName();

            if (comp.doesEdgeHasWeight()) {
                edge = new Edge(edgeName, comp.getEdgeWeight());
            } else {
                edge = new Edge(edgeName);
            }

            if (!graph.containsVertex(source)) {
                graph.addVertex(source);
            } else {
                final String sourceName = source.getName();
                Optional<Vertex> opSrc = graph.getVertices().stream().filter(v -> v.getName().equals(sourceName)).findFirst();
                source = opSrc.orElse(null);
            }
            if (!graph.containsVertex(target)) {
                graph.addVertex(target);
            } else {
                final String targetName = target.getName();
                Optional<Vertex> opTarget = graph.getVertices().stream().filter(v -> v.getName().equals(targetName)).findFirst();
                target = opTarget.orElse(null);
            }
            if (!graph.containsEdge(edge)) {
                graph.addEdge(edge, source, target);
            }
        }

        return graph;
    }

    /**
     * Erzeugt einen gerichteten Graphen mit 100 Vertexen und 3500 Kanten
     * @return AbstractTypedGraph
     */
    public static AbstractTypedGraph<Vertex, Edge> createBIGJUNGGraph(){
        try{
            return GraphFactory.createRandomDirectedJUNGGraph(100,3500);
        }catch (IllegalArgumentException e){
            System.err.println("\n\nDAS KANN EIGENTLICH(tm) NICHT PASSIEREN!\n\n");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Erzeugt ein zufälligen Graphen mit einer gegeben Anzahl an Knoten und Kanten
     * @param amountOfVertexes Anzahhl Knoten
     * @param amountOfEdges Anzahl Kanten
     * @return AbstractTypedGraph
     * @throws IllegalArgumentException bei zu vielen Kanten
     */
    public static AbstractTypedGraph<Vertex, Edge> createRandomDirectedJUNGGraph(int amountOfVertexes, int amountOfEdges) throws IllegalArgumentException{

        if(amountOfEdges > (amountOfVertexes*amountOfVertexes)){
            throw new IllegalArgumentException("amountOfEdges must be less than amountOfVertexes² ");
        }

        // Vertexliste erzeugen
        List<Vertex> vertexList = new ArrayList<>();
        for(int i = 0; i < amountOfVertexes; i++){
            Vertex v = new Vertex("v"+(i+1));
            vertexList.add(v);
        }

        // Edges erzeugen.
        List<Edge> edges = new ArrayList<>();
        for(int i = 1; i <= amountOfEdges; i++){
            Edge e = new Edge("e" + i, i);
            edges.add(e);
        }

        // Blacklist fuer bereits verbundene Knotenpaare
        // So darf a -> b nicht zweimal aufkreuzen
        Map<String,Boolean> blackList = new HashMap<>();

        // Der graph der zrucueckgegeben wird
        AbstractTypedGraph<Vertex,Edge> graph = new DirectedSparseGraph<>();

        // Jede Edge durchgehen und ein zufaelligen source und target Knoten zuweisen
        // Anschliessend diese Kombi in die Blacklist eintragen
        // ist ein Blacklisted Eintrag entstanden, einfach den naechsten erzeugen
        edges.forEach( e -> {
            int srcVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());
            int targetVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());

            Vertex src = vertexList.get(srcVertexIndex);
            Vertex target = vertexList.get(targetVertexIndex);
            String blackListEntry = src.getName() + "|" + target.getName();

            while(blackList.containsKey(blackListEntry)){
                //System.out.println("Blacklisthit: " + src.getName() + "\t->\t" + target.getName() );
                srcVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());
                targetVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());

                src = vertexList.get(srcVertexIndex);
                target = vertexList.get(targetVertexIndex);
                blackListEntry = src.getName() + "|" + target.getName();
            }
            //System.out.println("Adding: " + src.getName() + " -" + e.getName() + "-> " + target.getName());
            graph.addEdge(e, src, target);
            blackList.put(blackListEntry, Boolean.TRUE);

        });

        return graph;
    }


    /**
     * Erzeugt ein Netzwerk mit 50 Knoten und 800 Kanten
     * @return AbstractTypedGraph
     */
    public static AbstractTypedGraph<Vertex, Edge> createBigNetJUNGGraph(){
        try{
            return GraphFactory.createRandomNetwork(50,800);
        }catch (IllegalArgumentException e){
            System.err.println("\n\nDAS KANN EIGENTLICH(tm) NICHT PASSIEREN!\n\n");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Erzeugt einen schwach zusammenhangenden, schlichten gerichteten Graphen
     * Alias: Netzwerk
     *  n = Anzahl der Knoten
     *  - minimale Kantenanzahl:     n -1
     *  - maximale Kantenanzahl:    (n-2)²+(n-2)
     *  - keine Schleifen
     *  - keine Mehrfachkanten
     *
     * Ein einfacher Graph (auch schlichter Graph) ist in der Graphentheorie ein ungerichteter Graph ohne Mehrfachkanten und ohne Schleifen.
     *      https://de.wikipedia.org/wiki/Einfacher_Graph
     *
     * Ein gerichteter Graph heißt (schwach) zusammenhängend, falls der zugehörige ungerichtete Graph (also der Graph, der entsteht, wenn man jede gerichtete Kante durch eine ungerichtete Kante ersetzt) zusammenhängend ist.
     *      https://de.wikipedia.org/wiki/Zusammenhang_(Graphentheorie)
     *
     * @param amountOfVertexes Anzahl Knoten
     * @param amountOfEdges Anzahl Kanten
     * @return NetzwerkGraph
     * @throws IllegalArgumentException bei zu vielen oder wenigen Kanten
     */
    public static DirectedSparseGraph<Vertex, Edge> createRandomNetwork(int amountOfVertexes, int amountOfEdges) throws IllegalArgumentException{
        // Ensure: Mindestens Quelle + Senke
        if(amountOfVertexes < 2 || amountOfEdges < 1){
            throw new IllegalArgumentException("amountOfVertexes must be greater than 2 with at least 1 Edge");
        }

        // Ensure: mindestens genug kanten für einen schwach zusammenhängenden Graphen
        int min = (amountOfVertexes -1 );
        if(amountOfEdges < min){
            throw new IllegalArgumentException("amountOfEdges must be greater than amountOfVertexes -1 ");
        }

        // Ensure: maximal so viele, dass keine Schleifen und mehrfachkanten auftreten können
        // Also: max = ((amountOfVertexes-1)*(amountOfVertexes-1))+(amountOfVertexes-1);
        // Quelle darf nur ausgehende, Senke nur eingehende Kanten haben, also wird q und s für die Berechnung auf 1 Vertex reduziert
        //
        int max = ((amountOfVertexes-2)*(amountOfVertexes-2))+(amountOfVertexes-1);
        if(amountOfEdges > max ){
            throw new IllegalArgumentException("amountOfEdges must be less than: amountOfVertexes = n => (n-2)²+(n-2) ");
        }

        DirectedSparseGraph<Vertex,Edge> graph = new DirectedSparseGraph<>();

        // Erstmal alles erzeugen
        List<Vertex> vertexList = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        // Quelle (source) und senke (target) erstellen
        Vertex s = new Vertex(RANDOM_NETWORK_DEFAULT_SOURCE_VERTEX_NAME);
        Vertex t = new Vertex(RANDOM_NETWORK_DEFAULT_TARGET_VERTEX_NAME);
        vertexList.add(s);
        // Restliche Vertexe
        for(int i = 2; i < amountOfVertexes; i++){
            Vertex v = new Vertex("v"+(i+1));
            vertexList.add(v);
        }
        vertexList.add(t);

        // Edges erzeugen
        for(int i = 1; i <= amountOfEdges; i++){
            Edge e = new Edge("e" + i, i);
            edges.add(e);
        }

        // Blacklist fuer bereits verbundene Knotenpaare oder verbotene Knotenpaare
        // Ensure: dafür, dass keine mehrfachkanten auftreten
        Map<String, Boolean> blackList = new HashMap<>();

        // Ensure: keine Schleifen
        // Beipsielsweise darf jetzt A|A nicht mehr aufkreuzen
        vertexList.forEach(v -> blackList.put(v.getName() + "|" + v.getName(), Boolean.TRUE));
        // Ensure: Quelle keine Eingehenden
        vertexList.forEach(v -> blackList.put(v.getName() + "|" + s.getName(), Boolean.TRUE));
        // Ensure: Senke keine ausgehenden
        vertexList.forEach(v -> blackList.put(t.getName() + "|" + v.getName(), Boolean.TRUE));

        // Ensure: Schwach zusammenhängend, d.h. wäre der ungerichtet, hingen alle zusammen
        for(int i = 0; i < (amountOfVertexes -1); i++){
            // Irgendeine Ege
            int anyIndex = ThreadLocalRandom.current().nextInt(0,edges.size());

            graph.addEdge(edges.get(anyIndex), vertexList.get(i), vertexList.get(i+1));
            // Kombination in Blacklist aufnehmen
            blackList.put(vertexList.get(i).getName() + "|" + vertexList.get(i+1).getName(), Boolean.TRUE);
            // Die Edge schon mal entfernen
            edges.remove(anyIndex);
        }

        // Jetzt ist der Graph schwach zusammenhaengend

        // Durch die übrigen Edges durchgehen und ein zufaelligen source und target Knoten zuweisen
        // Anschliessend diese Kombi in die Blacklist eintragen
        // ist ein Blacklisted Eintrag entstanden, einfach den naechsten erzeugen
        edges.forEach( e -> {

            int srcVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());
            int targetVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());

            Vertex src = vertexList.get(srcVertexIndex);
            Vertex target = vertexList.get(targetVertexIndex);
            String blackListEntry = src.getName() + "|" + target.getName();

            while(blackList.containsKey(blackListEntry)){
                srcVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());
                targetVertexIndex = ThreadLocalRandom.current().nextInt(0,vertexList.size());

                src = vertexList.get(srcVertexIndex);
                target = vertexList.get(targetVertexIndex);
                blackListEntry = src.getName() + "|" + target.getName();
            }
            graph.addEdge(e, src, target);
            blackList.put(blackListEntry, Boolean.TRUE);
        });
        return graph;
    }


    /**
     * Erzeugt ein neues Graph Objekt mit den selben Vertexen und Kanten
     * @param original Graph
     * @return DirectedSparseGraph
     */
    public static DirectedSparseGraph<Vertex,Edge> cloneNetwork(DirectedSparseGraph<Vertex,Edge> original) {
        DirectedSparseGraph<Vertex,Edge> copy = new DirectedSparseGraph<>();
        original.getVertices().forEach(copy::addVertex);
        original.getEdges().forEach(e -> copy.addEdge(e, original.getEndpoints(e)));
        return copy;
    }


}
