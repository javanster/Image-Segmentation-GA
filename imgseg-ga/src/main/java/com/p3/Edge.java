package com.p3;

/**
 * Represents an edge in a graph.
 */
public class Edge implements Comparable<Edge> {
    
    private int from;
    private int to;
    private double weight;

    /**
     * Constructs a new Edge object.
     *
     * @param from   the source vertex of the edge
     * @param to     the destination vertex of the edge
     * @param weight the weight of the edge
     */
    public Edge(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * Returns the pixel index of the source vertex of the edge.
     * 
     * @return the pixel index of the source vertex of the edge
     */
    public int getFrom() {
        return from;
    }

    /**
     * Returns the pixel index of the destination vertex of the edge.
     * 
     * @return the pixel index of the destination vertex of the edge
     */
    public int getTo() {
        return to;
    }

    /**
     * Compares this edge with another edge based on their weights.
     *
     * @param other the other edge to compare with
     * @return a negative integer, zero, or a positive integer as this edge is less than, equal to, or greater than the other edge
     */
    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.weight, other.weight);
    }
}
