package com.haw.avdt.model;

/**
 * Wird als Datentyp für Edges in der Libary verwendet
 * getName() → String (wenn kein Name gegeben, Object HashCode)
 * getWeight() → Integer
 */
public class Edge {

    private String name;
    private int weight;

    public Edge() {
        this.weight = 0;
    }

    public Edge(String name) {
        this.name = name;
        this.weight = 0;
    }

    public Edge(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    /**
     * @return String (wenn kein Name gegeben, Object HashCode)
     */
    public String getName() {
        return name == null ? String.valueOf(this.hashCode()) : name;
    }

    @Override
    public String toString() {
        String s = "Edge{name='" + this.getName() + '\'';
        s += this.getWeight() == 0 ? "" : ", weight=\'" + this.getWeight() + "\'";
        s += "}";
        return s;
    }

    public int getWeight() {
        return weight;
    }


}
