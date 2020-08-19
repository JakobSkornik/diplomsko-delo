package procedures.neuralnetwork;

import procedures.grapher.Graph;
import procedures.grapher.Node;
import utilities.FloydWarshall;
import utilities.Matrix;

import java.io.PrintWriter;
import java.util.*;

/**
 * GreedyDecoder class.
 *
 * Performs decoding of the graph embedding and outputs a permutation of
 * node visitations.
 */
public class GreedyDecoder {

    /** Pointer to graph object. */
    private final Graph graph;

    /** Pointer to SkipGram model. */
    private final AutoEncoder autoEncoder;

    /** 2D-array of dynamic demands. */
    private final double[] cleanliness;

    /** Capacity of the car. */
    private final double CAPACITY;

    /** Map of visited nodes (nodes that have had their demands met). */
    private HashMap<Integer, Node> visited;

    /**
     * List representing an intermediate solution.
     * Only a permutation of visitations.
     */
    private List<Integer> solution;

    /** Distance of solution. */
    private double total_distance;

    /** List representing the final solution. */
    private List<Integer> final_sol;

    private final FloydWarshall floydWarshall;

    /**
     * Constructor for class GreedyDecoder.
     *
     * Constructs an object based on parameters.
     *
     * @param graph Graph object.
     * @param autoEncoder SkipGram object.
     * @param capacity Vehicle capacity constraint.
     */
    public GreedyDecoder(Graph graph, AutoEncoder autoEncoder, double capacity, FloydWarshall fw) {
        this.graph = graph;
        this.autoEncoder = autoEncoder;
        this.CAPACITY = capacity;
        this.floydWarshall = fw;
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
        solution = new LinkedList<>();
        visited = new HashMap<>();
        Node current = graph.depot();
        visited.put(current.id(), current);
        solution.add(0);
        while (notClean()) {
            double weight = 0;
            Node next = getNext(0, weight);
            weight += graph.getNodes().get(next.id()).demand();
            while (weight <= CAPACITY) {
                visited.put(next.id(), next);
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
        final_sol = floydWarshall.finalizePath(solution);
        total_distance = floydWarshall.distance();
    }

    /**
     * Function that returns true, when all demands have been met.
     *
     * @return The boolean value.
     */
    public boolean notClean() {
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
        Matrix output = autoEncoder.process(prev_id);
        double min = Double.MAX_VALUE;
        int id = -1;
        for (int i = 0; i < output.rows(); i++) {
            if (min > output.get(i, 0) && !visited.containsKey(i) && weight + graph.getNodes().get(i).demand() <= CAPACITY) {
                min = output.get(i, 0);
                id = i;
            }
        }
        return graph.getNodes().get(id);
    }

    /**
     * Prints the final solution to standard output.
     */
    public void printSolution() {
        System.out.printf("_______________________\nGREEDY DECODER\nSOLUTION DISTANCE: %.4f\n_______________________\n", total_distance);
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

    public void log(PrintWriter printer) {
        printer.printf("1,%.4f\n", total_distance);
    }
}
