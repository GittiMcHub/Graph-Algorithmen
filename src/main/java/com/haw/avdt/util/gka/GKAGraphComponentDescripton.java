package com.haw.avdt.util.gka;

/**
 * Diese Klasse dient zur entkopplung der verwendeten Libary
 * und dem Lesen aus der Datei
 *
 * Beschreibt jeweils eine Zeile der GKA Datei
 */
public class GKAGraphComponentDescripton {

    private final String sourceVertexName;

    private final boolean hasTarget;
    private final String targetVertexName;

    private final boolean edgeHasWeight;
    private final Integer edgeWeight;

    private final boolean edgeHasName;
    private final String edgeName;

    private final boolean edgeIsDirected;

    public GKAGraphComponentDescripton(String sourceVertexName, String targetVertexName, String edgeName, boolean edgeIsDirected, Integer edgeWeight) {
        this.sourceVertexName = sourceVertexName;
        this.targetVertexName = targetVertexName;
        this.hasTarget = (targetVertexName != null) ? true : false;

        this.edgeWeight = edgeWeight;
        this.edgeHasWeight = (edgeWeight != null) ? true : false;


        this.edgeName = edgeName;
        this.edgeHasName = (edgeName != null) ? true : false;

        this.edgeIsDirected = edgeIsDirected;
    }

    public String getSourceVertexName() {
        return sourceVertexName;
    }

    public boolean hasTarget() {
        return hasTarget;
    }

    public String getTargetVertexName() {
        return targetVertexName;
    }

    public boolean doesEdgeHasWeight() {
        return edgeHasWeight;
    }

    public int getEdgeWeight() {
        return edgeWeight;
    }

    public boolean doesEdgeHasName() {
        return edgeHasName;
    }

    public String getEdgeName() {
        return edgeName;
    }

    public boolean isEdgeDirected() {
        return edgeIsDirected;
    }

    @Override
    public String toString() {
        String s = this.sourceVertexName;

        if (this.hasTarget) {
            s += " " + ((this.isEdgeDirected()) ? "->" : "--") + " " + this.targetVertexName;
        }
        if (this.edgeHasName) {
            s += " (" + this.edgeName + ")";
        }
        if (this.edgeHasWeight) {
            s += " : " + this.edgeWeight;
        }
        s += ";";

        return s;
    }

}
