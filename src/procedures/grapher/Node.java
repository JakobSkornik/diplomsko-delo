package procedures.grapher;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Node class.
 *
 * Each node stores it's own id, map of edges and demand.
 */
public class Node {

    /** ID of the node. */
    private final int id;

    /** Map of edges. */
    private final HashMap<Node, Edge> edges;

    /** Demand. */
    private double demand;

    /**
     * Node constructor.
     *
     * @param id Integer ID.
     */
    public Node(int id) {
        this.id = id;
        this.edges = new HashMap<>();
    }

    /**
     * Add an edge to the map of edges.
     *
     * @param neighbour Key that is used to obtain the edge.
     * @param edge Edge that is put in the map.
     */
    public void addEdge(Node neighbour, Edge edge) {
        this.edges.put(neighbour, edge);
    }

    /**
     * Returns ID of the node.
     *
     * @return The ID.
     */
    public int id() {
        return this.id;
    }

    /**
     * Returns map of the edges of this node.
     *
     * @return The map.
     */
    public HashMap<Node, Edge> getEdges() {
        return this.edges;
    }

    /**
     * Returns true if node in the parameter is neighbour to the current node.
     *
     * @param node Node that is checked whether it is a neighbour.
     * @return Boolean.
     */
    public boolean isNeighbour(Node node) {
        return edges.containsKey(node);
    }

    /**
     * Stores value in the demand field of the node.
     *
     * @param val The value that is stored.
     */
    public void setDemand(double val) {
        this.demand = val;
    }

    /**
     * Returns demand of the node.
     *
     * @return Double demand.
     */
    public double demand() {
        return this.demand;
    }

    /**
     * Returns a list of neighbours.
     *
     * @return The list.
     */
    public LinkedList<Node> neighbours() {
        LinkedList<Node> result = new LinkedList<>();
        for (Edge e : edges.values()) {
            result.add(e.to());
        }
        return result;
    }
}
