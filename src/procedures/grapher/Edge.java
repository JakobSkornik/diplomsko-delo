package procedures.grapher;

/**
 * Edge class.
 *
 * Stores edge information.
 */
public class Edge {

    /** Length of th edge. */
    private double weight;

    /** First node. */
    private final Node from;

    /** Second node. */
    private final Node to;

    /**
     * Edge constructor.
     *
     * @param from First node.
     * @param to Second node.
     * @param weight Weight double.
     */
    public Edge(Node from, Node to, double weight) {
        this.weight = weight;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns Node from which this edge originates.
     *
     * @return The Node.
     */
    public Node from() {
        return this.from;
    }

    /**
     * Returns Node to which this edge leads.
     *
     * @return The Node.
     */
    public Node to() {
        return this.to;
    }

    /**
     * Returns length of the edge.
     *
     * @return Double length.
     */
    public double weight() {
        return this.weight;
    }
}
