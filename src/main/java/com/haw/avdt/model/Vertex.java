package com.haw.avdt.model;


/**
 * Wird als Datentyp für Vertexe in der Library verwendet
 * getName() → String (wenn kein Name gegeben, Object HashCode)
 * getMark() → Integer
 * setMark(Integer) → void
 */
public class Vertex {

    private String name;
    private Integer mark;

    public Vertex(String name) {
        this.name = name;
        this.mark = -1;
    }

    /**
     * @return String (wenn kein Name gegeben, Object HashCode)
     */
    public String getName() {
        return name;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Integer getMark() {
        return mark;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass())) {
            Vertex obj = (Vertex) o;
            return obj.getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
