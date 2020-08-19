package procedures.grapher;

import utilities.Utilities;

import java.util.*;

/**
 * Graph class.
 *
 * Stores graph information.
 */
public class Graph {

    /** Utilities. */
    private final Utilities ut;

    /** Map of nodes. */
    private final HashMap<Integer, Node> nodes;

    /** Number of nodes in the graph. */
    private final int num_of_nodes;

    /** Number of edges in the graph. */
    private final int num_of_edges;

    /** Pointer to depot. */
    private Node depot;

    /** Car capacity. */
    private final double CAPACITY;

    /**
     * Graph constructor.
     *
     * @param size Number of Nodes in the graph.
     * @param sparseness Number of Edges in the graph.
     * @param mean Edge length mean.
     * @param deviation Edge length deviation.
     */
    public Graph(int size, int sparseness, double mean, double deviation, double car_cap, Utilities ut) {
        this.nodes = new HashMap<>();
        this.num_of_nodes = size;
        this.num_of_edges = sparseness;
        this.CAPACITY = car_cap;
        this.ut = ut;
        HashMap<Integer, Node> initial = new HashMap<>();
        HashSet<Node> S = new HashSet<>();
        for (int i = 0; i < size; i++) {
            Node temp = new Node(i);
            initial.put(i, temp);
            S.add(temp);
            if (i == 0) {
                depot = temp;
            }
        }
        Node current = initial.get(0);
        S.remove(current);
        nodes.put(current.id(), current);
        int num_of_edges = 0;
        while (!S.isEmpty()) {
            Node neighbour = initial.get(ut.randomInt(initial.size()));
            if (!nodes.containsKey(neighbour.id())) {
                double length = ut.gaussian() * deviation + mean;
                if (length < 0) {
                    length = -length;
                }
                Edge edge1 = new Edge(current, neighbour, length);
                current.addEdge(neighbour, edge1);
                Edge edge2 = new Edge(neighbour, current, length);
                neighbour.addEdge(current, edge2);
                S.remove(neighbour);
                nodes.put(neighbour.id(), neighbour);
                num_of_edges++;
            }
            current = neighbour;
        }
        while (sparseness - num_of_edges >= 0) {
            Node first = nodes.get(ut.randomInt(nodes.size()));
            Node second = nodes.get(ut.randomInt(nodes.size()));
            if (first.id() != second.id() && !first.isNeighbour(second)) {
                double length = ut.gaussian() * deviation + mean;
                if (length < 0) {
                    length = -length;
                }
                Edge edge1 = new Edge(first, second, length);
                first.addEdge(second, edge1);
                Edge edge2 = new Edge(second, first, length);
                second.addEdge(first, edge2);
                num_of_edges++;
            }
        }
    }

    /**
     * Sets the demand of all non-depot nodes depending on Gaussian distribution parameters.
     *
     * @param mean Gaussian distribution mean.
     * @param deviation Gaussian distribution deviation.
     */
    public void setDemand(double mean, double deviation) {
        for (int i = 1; i < nodes.size(); i++) {
            Node temp = nodes.get(i);
            double demand = ut.gaussian() * deviation + mean;
            if (demand > CAPACITY) {
                demand = demand - CAPACITY;
            }
            if (demand < 0) {
                demand = -demand;
            }
            temp.setDemand(demand);
        }
    }

    /**
     * Prints a formatted text with graph information.
     */
    public void printGraph() {
        for (Node n : nodes.values()) {
            System.out.println("_____________________");
            System.out.printf("NODE %d\n", n.id());
            System.out.printf("DEMAND %.2f\n", n.demand());
            LinkedList<Integer> edges = new LinkedList<>();
            for (Edge e : n.getEdges().values()) {
                edges.add(e.to().id());
            }
            Collections.sort(edges);
            for (int e : edges) {
                System.out.printf("%d ", e);
            }
            System.out.println();
        }
        System.out.println("_____________________");
    }

    /**
     * Returns a map of nodes.
     *
     * @return The map.
     */
    public HashMap<Integer, Node> getNodes() {
        return this.nodes;
    }

    /**
     * Returns the size of the graph. (Number of nodes.)
     *
     * @return The size.
     */
    public int size() {
        return this.num_of_nodes;
    }

    /**
     * Returns the sparseness of the graph. (Number of edges.)
     *
     * @return The sparseness.
     */
    public int sparseness() {
        return  this.num_of_edges;
    }

    /**
     * Returns pointer to depot node.
     *
     * @return The pointer.
     */
    public Node depot() {
        return this.depot;
    }
}
