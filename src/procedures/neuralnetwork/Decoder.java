package procedures.neuralnetwork;

import procedures.grapher.Graph;
import procedures.grapher.Node;
import utilities.FWCell;
import utilities.FloydWarshall;
import utilities.Matrix;

import java.util.*;

/**
 * Decoder class.
 *
 * Performs decoding of the graph embedding and outputs a permutation of
 * node visitations.
 */
public class Decoder {

    /** Pointer to graph object. */
    private final Graph graph;

    /** Pointer to SkipGram model. */
    private final SkipGram skipGram;

    /** 2D-array of dynamic demands. */
    private final double[] cleanliness;

    /** Capacity of the car. */
    private final double CAPACITY;

    /** Map of visited nodes (nodes that have had their demands met). */
    private HashMap<Integer, Node> visited;

    /**
     * Map of nodes that are reachable from this position.
     * Nodes on path to these nodes have been cleared, or are
     * directly accessible.
     */
    private HashMap<Integer, Node> reachable;

    /**
     * List representing an intermediate solution.
     * Only a permutation of visitations.
     */
    private List<Integer> solution;

    /** Distance of solution. */
    private double total_distance;

    /** List representing the final solution. */
    private LinkedList<Integer> final_sol;

    /**
     * Constructor for class Decoder.
     *
     * Constructs an object based on parameters.
     *
     * @param graph Graph object.
     * @param skipGram SkipGram object.
     * @param capacity Vehicle capacity constraint.
     */
    public Decoder(Graph graph, SkipGram skipGram, double capacity) {
        this.graph = graph;
        this.skipGram = skipGram;
        this.CAPACITY = capacity;
        cleanliness = new double[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            cleanliness[i] = graph.getNodes().get(i).demand();
        }
    }

    /**
     * Function that performs the first step of decoding.
     *
     * Computes the intermediate solution.
     */
    public void decode() {
        reachable = new HashMap<>();
        solution = new LinkedList<>();
        visited = new HashMap<>();
        Node current = graph.depot();
        visited.put(current.id(), current);
        update_reachable(current);
        solution.add(0);
        while (not_clean()) {
            double weight = 0;
            Node next = getNext(0, weight);
            weight += graph.getNodes().get(next.id()).demand();
            while (weight <= CAPACITY) {
                visited.put(next.id(), next);
                update_reachable(next);
                solution.add(next.id());
                cleanliness[next.id()] = 0;
                current = next;
                next = getNext(current.id(), weight);
                if (next == null) {
                    break;
                }
                weight += graph.getNodes().get(next.id()).demand();
            }
            solution.add(0);
        }
    }

    /**
     * Function that returns true, when all demands have been met.
     *
     * @return The boolean value.
     */
    public boolean not_clean() {
        for (double d : cleanliness) {
            if (d != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Greedily returns the next best node to visit.
     *
     * @param prev_id ID of current node.
     * @param weight Current weight of the vehicle.
     * @return Next Node object.
     */
    public Node getNext(int prev_id, double weight) {
        Matrix output = skipGram.process(prev_id);
        double max = Double.MIN_VALUE;
        int id = -1;
        for (int i = 0; i < output.rows(); i++) {
            if (max < output.get(i, 0) && !visited.containsKey(i) && weight + graph.getNodes().get(i).demand() <= CAPACITY) {
                max = output.get(i, 0);
                id = i;
            }
            else if (not_clean()) {
                for (int j = 0; j < cleanliness.length; j++) {
                    if (cleanliness[j] > 0) {
                        return graph.getNodes().get(j);
                    }
                }
            }
        }
        return graph.getNodes().get(id);
    }

    /**
     * Updates the map of reachable nodes.
     *
     * @param node Neighbours of this node are added to the map.
     */
    public void update_reachable(Node node) {
        List<Node> neighbours = node.neighbours();
        for (Node n : neighbours) {
            reachable.putIfAbsent(n.id(), n);
        }
    }

    /**
     * Finalizes the solution.
     *
     * The intermediate solution is only a permutation of nodes.
     * The sequence tells us when pickups are made. Missing steps are
     * replaced by paths computed in Floyd-Warshall algorithm. Maps
     * 'visited' and 'reachable' ensure correct sequence of permutation.
     *
     */
    public void finalise() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        floydWarshall.calculate();
        FWCell[][] paths = floydWarshall.get_paths();
        final_sol = new LinkedList();
        total_distance = 0;
        final_sol.add(0);
        boolean on_route = false;
        for (int i = 0; i < solution.size() - 1; i++) {
            int current = solution.get(i);
            int next = solution.get(i + 1);
            if (on_route) {
                LinkedList<Integer> temp = floydWarshall.path(current, next);
                total_distance += paths[current][next].getDistance();
                final_sol.addAll(temp);
                if (next == 0) {
                    on_route = false;
                }
            }
            else {
                LinkedList<Integer> temp = floydWarshall.path(current, next);
                total_distance += paths[current][next].getDistance();
                final_sol.addAll(temp);
                on_route = true;
            }
        }
    }

    /**
     * Prints the final solution to standard output.
     */
    public void print_solution() {
        System.out.printf("SOLUTION DISTANCE: %.4f\n_______________________\n", total_distance);
        int i = 1;
        for (int a : final_sol) {
            if (a == 0 && i != final_sol.size() && i != 1) {
                System.out.print("0\n0 ");
            }
            else {
                System.out.printf("%d ", a);
            }
            i++;
        }
        System.out.println();
    }

    /**
     * Prints the intermediate solution to standard output.
     */
    public void print_partial_solution() {
        System.out.println("PARTIAL SOLUTION");
        int i = 1;
        for (int a : solution) {
            if (a == 0 && i != solution.size() && i != 1) {
                System.out.print("0\n0 ");
            }
            else {
                System.out.printf("%d ", a);
            }
            i++;
        }
        System.out.println();
    }
}
