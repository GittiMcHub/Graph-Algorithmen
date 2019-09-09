package com.haw.avdt.view;

import com.haw.avdt.util.GraphFactory;
import com.haw.avdt.util.gka.GKAFileReader;
import com.haw.avdt.util.gka.GKAGraphDescripton;
import com.haw.avdt.view.util.MessageConsole;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;

public class GraphFileChooser extends JFrame {


    public GraphFileChooser(){
        this.setTitle("*.gka FileChooser");
        this.setSize(new Dimension(600, 300));
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                System.exit(0);
            }
        });

        JPanel main = new JPanel();
        BorderLayout borderLayout = new BorderLayout();
        main.setLayout(borderLayout);

        JTextPane logArea = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(logArea);

        MessageConsole mc = new MessageConsole(logArea);
        mc.redirectOut();
        mc.redirectErr(Color.RED, null);
        mc.setMessageLines(100);


        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(".gka Dateien");
        FileFilter filter = new FileNameExtensionFilter("*.gka Datei", "gka");
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(Paths.get(System.getProperty("user.dir")).toFile());

        JButton btnOpen = new JButton("*.gka Datei laden");
        btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(GraphFileChooser.this);

                if(returnVal == JFileChooser.APPROVE_OPTION){
                    try {
                        GKAGraphDescripton gd  = GKAFileReader.read(Paths.get(fc.getSelectedFile().toURI()).toString());
                        new GraphViz(gd);

                    } catch (Exception ex) {

                    }
                }
            }
        });



        main.add(scrollPane, BorderLayout.CENTER);
        main.add(btnOpen, BorderLayout.PAGE_END);

        this.setJMenuBar(this.createMenuBar());
        this.getContentPane().add(main);
        this.setVisible(true);
    }

    private JMenuBar createMenuBar(){

        JMenuBar menuBar = new JMenuBar();

        JMenu factoryMenu = new JMenu("Factory");


        JMenuItem itemBIG = new JMenuItem("BIG Graph");
        itemBIG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GraphViz(GraphFactory.createBIGJUNGGraph());
            }
        });

        JMenuItem itemBigNet = new JMenuItem("BigNet Graph");
        itemBigNet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GraphViz(GraphFactory.createBigNetJUNGGraph());
            }
        });

        GraphFileChooser parent = this;
        JMenuItem itemRandomDirectedJungGraph = new JMenuItem("Create random directed ");
        itemRandomDirectedJungGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vertexes = JOptionPane.showInputDialog(parent,
                        "How many Vertecies?", null);
                String edges = JOptionPane.showInputDialog(parent,
                        "How many Edges?", null);

                new GraphViz(GraphFactory.createRandomDirectedJUNGGraph(Integer.valueOf(vertexes), Integer.valueOf(edges)));
            }
        });

        JMenuItem itemRandomNetwork = new JMenuItem("Create random Network ");
        itemRandomNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vertexes = JOptionPane.showInputDialog(parent,
                        "How many Vertecies?", null);
                String edges = JOptionPane.showInputDialog(parent,
                        "How many Edges?", null);

                new GraphViz(GraphFactory.createRandomNetwork(Integer.valueOf(vertexes), Integer.valueOf(edges)));
            }
        });

        menuBar.add(factoryMenu);
        factoryMenu.add(itemBIG);
        factoryMenu.add(itemBigNet);
        factoryMenu.addSeparator();
        factoryMenu.add(itemRandomDirectedJungGraph);
        factoryMenu.add(itemRandomNetwork);

        return menuBar;
    }

}
