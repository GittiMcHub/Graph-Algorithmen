package com.haw.avdt.algorithm;

import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import edu.uci.ics.jung.graph.AbstractTypedGraph;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BruteForce {


    private final AbstractTypedGraph<Vertex, Edge> graph;

    private volatile ArrayList<BruteForceWorker> workers;
   // private volatile ArrayList<Future<?>> workerSubmits;

    private volatile ExecutorService executorService;

    public BruteForce(AbstractTypedGraph<Vertex, Edge> graph) {
        this.graph = graph;
        this.workers = new ArrayList<>();
        //this.workerSubmits = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*Runtime.getRuntime().availableProcessors());
    }

    private synchronized void registerWorker(BruteForceWorker worker){
        System.out.println("New Worker - " + this.workers.size());
        this.workers.add(worker);
        Thread t = new Thread(worker);
        this.executorService.submit(t);
        //System.out.println("New Worker - " + this.workers.size());
    }

    public List<List<Edge>> getAllPaths(Vertex source, Vertex target) throws UnreachableVertexException {
        BruteForceWorker worker = new BruteForceWorker(this, this.graph, source, target);
        //Thread t = new Thread(worker);
        //t.start();
        this.registerWorker(worker);
        try{
            while(this.workers.stream().anyMatch(w -> !w.isDone())){
                //System.out.println("\n\nBruteForce waiting for tasks to be done #");
                Thread.sleep(1000);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        List<List<Edge>> results = this.workers.stream().filter(w -> w.found).map(BruteForceWorker::getPath).collect(Collectors.toList());

        if(results.isEmpty())
            throw new UnreachableVertexException("Vertexes not conected");

        return results;
    }


    private class BruteForceWorker implements Runnable{

        private final BruteForce master;
        private final AbstractTypedGraph<Vertex, Edge> graph;
        private final Vertex source;
        private final Vertex target;

        private List<Edge> path;

        private Edge next;

        private boolean done;
        private boolean found;

        public BruteForceWorker(BruteForce master, AbstractTypedGraph<Vertex, Edge> graph, Vertex source, Vertex target) {
            this.master = master;
            this.master.registerWorker(this);
            this.graph = graph;
            this.source = source;
            this.target = target;
            this.path = new ArrayList<>();
            this.master.registerWorker(this);
            this.done = false;
            this.found = false;
            this.next = null;
        }

        public BruteForceWorker(BruteForce master, AbstractTypedGraph<Vertex, Edge> graph, Vertex source, Vertex target, List<Edge> path, Edge next) {
            this.master = master;
            this.master.registerWorker(this);
            this.graph = graph;
            this.source = source;
            this.target = target;
            this.path = path;
            this.next = next;
            this.master.registerWorker(this);
            this.done = false;
            this.found = false;
        }


        private synchronized  List<Edge> recursiveFork(List<Edge> currentPath, Edge nextEdge){
            //System.out.println(this + " Current path: " + currentPath);
            System.out.println(this + " going to " + nextEdge.getName());
            // Next edge gegeben, in einem Beliebigen Schritt
            currentPath.add(nextEdge);

            Vertex dest = this.graph.getDest(nextEdge);
            //System.out.println(this + " dest is " + dest.getName());
            if(dest == this.target){
                this.found = true;
                this.done = true;
                return currentPath;
            }

            // Alle Edges, die noch nicht im Pfad sind
            List<Edge> outEdges = this.graph.getOutEdges(dest)
                    .stream()
                    .filter(e -> !this.path.contains(e))
                    .collect(Collectors.toList());
            //System.out.println(this + " possible paths " + outEdges.size());
            // Sackgasse
            if(outEdges.size() == 0){
                this.done = true;
                this.found = false;
                return currentPath;
            }

            if(outEdges.size() > 1){
                // Fork new Threads
                for(int i = 1; i < outEdges.size(); i++){
                 //   System.out.println(this + " forking new thread for " + outEdges.get(i).getName());
                    new BruteForceWorker(this.master, this.graph, this.source, this.target, new ArrayList<Edge>(currentPath), outEdges.get(i));
                    //Thread t = new Thread(new BruteForceWorker(this.master, this.graph, this.source, this.target, new ArrayList<Edge>(currentPath), outEdges.get(i)));
                   // t.start();
                }
            }
            return recursiveFork(currentPath, outEdges.get(0));
        }

        public boolean isDone() {
            return done;
        }

        public boolean isFound() {
            return found;
        }

        public List<Edge> getPath(){
            return this.path;
        }


        @Override
        public void run() {
            //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            //System.out.println(this);
            // Am Anfang
            if(this.path.size() == 0 && this.next == null){
                //System.out.println(this + " initial");
                List<Edge> outEdges = this.graph.getOutEdges(this.source).stream().collect(Collectors.toList());

                if(outEdges.size() == 0){
                    this.done = true;
                    this.found = false;
                    return;
                }
                for (Edge outEdge : outEdges) {
                    new BruteForceWorker(this.master, this.graph, this.source, this.target, new ArrayList<Edge>(this.path), outEdge);
                    //Thread t = new Thread(new BruteForceWorker(this.master, this.graph, this.source, this.target, new ArrayList<Edge>(this.path), outEdge));
                    //t.start();
                }
                this.done = true;
                this.found = false;
                return;
            }
            this.path = this.recursiveFork(this.path, this.next);
            //System.out.println(this + " I'm done");
            this.done = true;
        }


    }


}
