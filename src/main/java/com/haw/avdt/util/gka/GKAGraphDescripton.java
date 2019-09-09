package com.haw.avdt.util.gka;

import com.haw.avdt.util.gka.ex.GKAGraphIncompabilityException;

import java.util.ArrayList;

/**
 * Diese Klasse dient zur entkopplung der verwendeten Libary
 * und dem Lesen aus der Datei
 *
 * Beschreibt den Graphen aus der GKA Datei als Ganzes und beinhaltet die GKAGraphComponentDescription Objekte
 *
 */
public class GKAGraphDescripton {

    private boolean isDirected;
    private boolean isWeighted;
    private boolean doesEdgesHaveNames;

    private ArrayList<GKAGraphComponentDescripton> components;

    public GKAGraphDescripton(GKAGraphComponentDescripton component) {
        this.isDirected = component.isEdgeDirected();
        this.isWeighted = component.doesEdgeHasWeight();
        this.doesEdgesHaveNames = component.doesEdgeHasName();
        this.components = new ArrayList<>();
    }

    public void addComponent(GKAGraphComponentDescripton component) throws GKAGraphIncompabilityException {
        /*
         * Wenn die Eingegebenen Komponenten nicht der Graphbeschreibung entsprechen,
         * wird eine GKAGraphIncompabilityException geworfen
         */
        if (component.hasTarget()
                && (this.isDirected != component.isEdgeDirected()
                || this.isWeighted != component.doesEdgeHasWeight()
                || this.doesEdgesHaveNames != component.doesEdgeHasName())) {
            throw new GKAGraphIncompabilityException();
        }
        this.components.add(component);
    }

    public boolean isDirected() {
        return isDirected;
    }

    public boolean isWeighted() {
        return isWeighted;
    }

    public boolean doesEdgesHaveNames() {
        return doesEdgesHaveNames;
    }

    public ArrayList<GKAGraphComponentDescripton> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        String s = this.toStringWithHeaderOnly();
        s += "components={";
        for (GKAGraphComponentDescripton c : this.components) {
            s += " | " + c.toString();
        }
        s += "}";
        return s;
    }

    public String toStringWithHeaderOnly() {
        return "Graph{isDirected=" + this.isDirected +
                ", isWeighted=" + this.isWeighted +
                ", edgesHaveNames=" + this.doesEdgesHaveNames() +
                ", Components=" + this.components.size();
    }
}
