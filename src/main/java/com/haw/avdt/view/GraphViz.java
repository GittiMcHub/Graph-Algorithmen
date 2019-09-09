package com.haw.avdt.view;

import com.haw.avdt.algorithm.BFS;
import com.haw.avdt.algorithm.Dijkstra;
import com.haw.avdt.algorithm.FloydWarshall;
import com.haw.avdt.algorithm.ex.FoundCycleWithNegativeSum;
import com.haw.avdt.algorithm.ex.UnreachableVertexException;
import com.haw.avdt.model.Edge;
import com.haw.avdt.model.Vertex;
import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Optional;

public class GraphViz extends JFrame {

    private GKAGraphDescripton graphDesc;
    // Fuer Graph Only, ohne Beschreibung
    private AbstractTypedGraph graph;

    private JPanel infoPanel;
    private Component graphPanel;

    private JTextArea lblGraphDescHeader;
    private JTextField tfBFSSource;
    private JTextField tfBFSTarget;
    private JButton btBFS;
    private JButton btDijkstra;
    private JButton btFloydWarshall;

    private JTextArea lblPath;
    private JLabel lblDistanceHeader;
    private JLabel lblDistance;

    private static final int INITIAL_WINDOW_SIZE_WIDTH = 800;
    private static final int INITIAL_WINDOW_SIZE_HEIGHT = 400;

    private Dijkstra dijkstra;

    public GraphViz(GKAGraphDescripton graphDesc) {
        this.graphDesc = graphDesc;
        this.graph = GraphFactory.createJUNGGraph(graphDesc);
        this.initContentPane();

        this.setSize(new Dimension(GraphViz.INITIAL_WINDOW_SIZE_WIDTH, GraphViz.INITIAL_WINDOW_SIZE_HEIGHT));
        this.setVisible(true);
    }

    public GraphViz(AbstractTypedGraph graph){
        this.graph = graph;

        this.initContentPane();

        this.setSize(new Dimension(GraphViz.INITIAL_WINDOW_SIZE_WIDTH, GraphViz.INITIAL_WINDOW_SIZE_HEIGHT));
        this.setVisible(true);
    }


    /**
     * Initialisiert das Hauptfenster
     */
    private void initContentPane() {
        GridLayout contentPaneLayout = new GridLayout(1, 2);
        this.getContentPane().setLayout(contentPaneLayout);
        this.getContentPane().add(this.getInfoPanel());
        this.getContentPane().add(this.getGraphPanel());
    }

    /**
     * Initialisiert das Info Panel
     *
     * @return
     */
    private Component getInfoPanel() {
        if (this.infoPanel == null) {

            this.infoPanel = new JPanel();

            BoxLayout infoPaneLayout = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);
            infoPanel.setLayout(infoPaneLayout);

            infoPanel.add(this.getLblGraphDescHeader());
            infoPanel.add(new JLabel("Source"));
            infoPanel.add(this.getTfBFSSource());
            infoPanel.add(new JLabel("Target"));
            infoPanel.add(this.getTfBFSTarget());
            infoPanel.add(this.getBtBFS());
            infoPanel.add(this.getBtDijkstra());
            infoPanel.add(this.getBtFloydWarshall());
            infoPanel.add(new JLabel("Path"));
            infoPanel.add(this.getLblPath());
            infoPanel.add(this.getLblDistanceHeader());
            infoPanel.add(this.getLblDistance());
        }
        return this.infoPanel;
    }

    /**
     * Initialisiert die GraphVisualisierung
     *
     * @return
     */
    private Component getGraphPanel() {
        if (this.graphPanel == null) {

            Layout l = new FRLayout(this.graph);
            VisualizationViewer vv = new VisualizationViewer(l);
            vv.setVertexToolTipTransformer(x -> x.toString());
            vv.setEdgeToolTipTransformer(x -> x.toString());

            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());


            GraphZoomScrollPane gzp = new GraphZoomScrollPane(vv);
            this.graphPanel = gzp;
        }
        return this.graphPanel;
    }

    /**
     * Fuehrt den BFS Algorithmus aus und gibt die Ergebnisse an die TextAreas weiter
     */
    private void readAndUpdateBFS() {

        AbstractTypedGraph<Vertex, Edge> g = this.graph;
        BFS algo = new BFS(g);

        Optional<Vertex> opSource = g.getVertices().stream().filter(v -> v.getName().equals(tfBFSSource.getText())).findFirst();
        Optional<Vertex> opTarget = g.getVertices().stream().filter(v -> v.getName().equals(tfBFSTarget.getText())).findFirst();

        if (!opSource.isPresent() || !opTarget.isPresent()) {
            this.getLblPath().setText("! Could not find Vertexes !");
            this.getLblDistance().setText("---");
        }

        Vertex source = opSource.get();
        Vertex target = opTarget.get();

        HashMap<String, Object> result = null;
        try {
            result = algo.getDistanceAndPath(source, target);
            this.getLblPath().setText(String.valueOf(result.get("path")));
            this.getLblDistanceHeader().setText("<html>Distance by Edges </br>(BFS)</html>");
            this.getLblDistance().setText(String.valueOf((Integer) result.get("distance")));
        } catch (UnreachableVertexException e1) {
            //e1.printStackTrace();
            this.getLblPath().setText("! Unreachable !");
            this.getLblDistance().setText("! Unreachable !");
        }
    }

    /**
     * Fuehrt den Dijsktra Algorithmus aus und gibt die Ergebnisse an die TextAreas weiter
     */
    private void readAndUpdateDijkstra() {

        AbstractTypedGraph<Vertex, Edge> g = this.graph;

        Optional<Vertex> opSource = g.getVertices().stream().filter(v -> v.getName().equals(tfBFSSource.getText())).findFirst();
        Optional<Vertex> opTarget = g.getVertices().stream().filter(v -> v.getName().equals(tfBFSTarget.getText())).findFirst();

        if (!opSource.isPresent() || !opTarget.isPresent()) {
            this.getLblPath().setText("! Could not find Vertexes !");
            this.getLblDistance().setText("---");
        }

        Vertex source = opSource.get();
        Vertex target = opTarget.get();


        try {
            this.getLblPath().setText(this.getDijkstra().getPath(source,target).toString());
            this.getLblDistanceHeader().setText("<html>Distance by weight </br>(Dijkstra)</html>");
            this.getLblDistance().setText(String.valueOf(this.getDijkstra().getDistance(source,target)));
        } catch (UnreachableVertexException e1) {
            //e1.printStackTrace();
            this.getLblPath().setText("! Unreachable !");
            this.getLblDistance().setText("! Unreachable !");
        }
    }

    /**
     * Fuehrt den Floyd-Warshall Algorithmus aus und gibt die Ergebnisse an die TextAreas weiter
     */
    private void readAndUpdateFloyedWarshall() {

        AbstractTypedGraph<Vertex, Edge> g = this.graph;

        try {
            FloydWarshall algo = new FloydWarshall(g);

            Optional<Vertex> opSource = g.getVertices().stream().filter(v -> v.getName().equals(tfBFSSource.getText())).findFirst();
            Optional<Vertex> opTarget = g.getVertices().stream().filter(v -> v.getName().equals(tfBFSTarget.getText())).findFirst();

            if (!opSource.isPresent() || !opTarget.isPresent()) {
                this.getLblPath().setText("! Could not find Vertexes !");
                this.getLblDistance().setText("---");
            }

            Vertex source = opSource.get();
            Vertex target = opTarget.get();


            try {
                this.getLblPath().setText(algo.getPath(source,target).toString());
                this.getLblDistanceHeader().setText("<html>Distance by weight </br>(Floyd-Warshall)</html>");
                this.getLblDistance().setText(String.valueOf(algo.getDistance(source,target)));
            } catch (UnreachableVertexException e1) {
                //e1.printStackTrace();
                this.getLblPath().setText("! Unreachable !");
                this.getLblDistance().setText("! Unreachable !");
            }
        } catch (FoundCycleWithNegativeSum ex ) {
            this.getLblPath().setText("! Cycle with negatve Sum !");
            this.getLblDistance().setText("! Cycle with negatve Sum !");
        }

    }



    public Dijkstra getDijkstra() {
        if(this.dijkstra == null){
            this.dijkstra = new Dijkstra(this.graph);
        }
        return dijkstra;
    }

    /*
        GETTER der GUI Komponenten
     */


    public JTextArea getLblGraphDescHeader() {
        if (this.lblGraphDescHeader == null) {
            if(this.graphDesc != null){
                this.lblGraphDescHeader = new JTextArea(this.graphDesc.toStringWithHeaderOnly().replace("{", "\r\n ").replace(",", "\r\n"));
            } else {
                this.lblGraphDescHeader = new JTextArea("Graph");
            }

            this.lblGraphDescHeader.setMaximumSize(new Dimension(400, 80));
            this.lblGraphDescHeader.setLineWrap(true);
        }
        return this.lblGraphDescHeader;
    }

    public JTextField getTfBFSSource() {
        if (this.tfBFSSource == null) {
            this.tfBFSSource = new JTextField("source vertex");
            this.tfBFSSource.setMaximumSize(new Dimension(400, 20));
        }
        return this.tfBFSSource;
    }

    public JTextField getTfBFSTarget() {
        if (this.tfBFSTarget == null) {
            this.tfBFSTarget = new JTextField("target vertex");
            this.tfBFSTarget.setMaximumSize(new Dimension(400, 20));
        }
        return this.tfBFSTarget;
    }

    public JButton getBtBFS() {
        if (this.btBFS == null) {
            this.btBFS = new JButton("BFS");
            this.btBFS.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    readAndUpdateBFS();
                }
            });
        }
        return this.btBFS;
    }

    public JButton getBtDijkstra() {
        if (this.btDijkstra == null) {
            this.btDijkstra = new JButton("Dijkstra");
            this.btDijkstra.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    readAndUpdateDijkstra();
                }
            });
        }
        return this.btDijkstra;
    }

    public JButton getBtFloydWarshall() {
        if (this.btFloydWarshall == null) {
            this.btFloydWarshall = new JButton("Floyd-Warshall");
            this.btFloydWarshall.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    readAndUpdateFloyedWarshall();
                }
            });
        }
        return this.btFloydWarshall;
    }

    public JTextArea getLblPath() {
        if (this.lblPath == null) {
            this.lblPath = new JTextArea();
            this.lblPath.setMaximumSize(new Dimension(400, 40));
            this.lblPath.setLineWrap(true);

        }
        return this.lblPath;
    }

    public JLabel getLblDistance() {
        if (this.lblDistance == null) {
            this.lblDistance = new JLabel("---");
        }
        return this.lblDistance;
    }

    public JLabel getLblDistanceHeader() {
        if (this.lblDistanceHeader == null) {
            this.lblDistanceHeader = new JLabel("Distance");
        }
        return this.lblDistanceHeader;
    }




}
