package procedures.heuristics;

import procedures.grapher.Graph;
import procedures.grapher.Node;
import utilities.FloydWarshall;
import utilities.Utilities;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Greedy class.
 *
 * Simple greedy solver for CVRP.
 */
public class Greedy {

    /** Pointer to graph object. */
    private final Graph graph;

    /** Pointer to FloydWarshall object. */
    private final FloydWarshall floydWarshall;

    /** Capacity of the car. */
    private final double CAPACITY;

    /** Array of demands. */
    private final double[] cleanliness;

    /** Dynamic weight of the car. */
    private double weight;

    /** Distance of the solution. */
    private double total_distance;

    /** Permutation of nodes, where only pick-ups are inserted. */
    private List<Integer> permutation;

    /** List of integers representing the solution. */
    private List<Integer> solution;

    /** Utilities. */
    private final Utilities ut;

    /**
     * Constructor for class Greedy.
     *
     * @param graph Graph object.
     * @param fw FloydWarshall object.
     * @param capacity Capacity double.
     */
    public Greedy(Graph graph, FloydWarshall fw, double capacity, Utilities ut) {
        this.graph = graph;
        this.floydWarshall = fw;
        this.CAPACITY = capacity;
        this.ut = ut;
        this.solution = new LinkedList<>();
        this.permutation = new LinkedList<>();
        this.total_distance = 0;
        cleanliness = new double[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            cleanliness[i] = graph.getNodes().get(i).demand();
        }
    }

    /**
     * Function that greedily solves the CVRP.
     */
    public void solve() {
        Node current = graph.depot();
        solution.add(graph.depot().id());
        weight = 0;
        while(notClean()) {
            Node next = getNext(current);
            if (next.id() != 0) {
                weight += cleanliness[next.id()];
                cleanliness[next.id()] = 0;
            }
            else {
                weight = 0;
            }
            solution.add(next.id());
            current = next;
        }
        solution.add(graph.depot().id());
        permutation = ut.copy(solution);
        solution = floydWarshall.finalizePath(solution);
        total_distance = floydWarshall.distance();
    }

    /**
     * Function that solves a partial problem.
     *
     * @param partial State of demands.
     * @return Solution to a partial CVRP.
     */
    public List<Integer> solve(double[] partial, int curr, double w) {
        Node current = graph.getNodes().get(curr);
        List<Integer> partial_solution = new LinkedList<>();
        partial_solution.add(curr);
        weight = w;
        while(notClean()) {
            Node next = getNext(current, partial);
            if (next.id() != 0) {
                weight += partial[next.id()];
                partial[next.id()] = 0;
            }
            else {
                weight = 0;
            }
            partial_solution.add(next.id());
            current = next;
        }
        partial_solution.add(graph.depot().id());
        permutation = ut.copy(partial_solution);
        partial_solution = floydWarshall.finalizePath(partial_solution);
        total_distance = floydWarshall.distance();
        return partial_solution;
    }

    /**
     * Returns boolean indicating whether all demands have been met.
     *
     * @return The boolean.
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
     * Function that returns the closes node that fits on the car.
     *
     * @param current Current Node.
     * @return Next Node.
     */
    public Node getNext(Node current) {
        return getNode(current, cleanliness);
    }

    /**
     * Returns a node based on parameters.
     *
     * @param current Current node.
     * @param cleanliness Current state of demands.
     * @return Next node selected greedily.
     */
    private Node getNode(Node current, double[] cleanliness) {
        double min = Double.MAX_VALUE;
        int next = -1;
        for (int i = 0; i < graph.size(); i++) {
            double dist = floydWarshall.getPaths()[current.id()][i].getDistance();
            if (dist < min && i != current.id() && i != 0 && weight + cleanliness[i] <= CAPACITY && cleanliness[i] > 0) {
                min = dist;
                next = i;
            }
        }
        if (next == -1) {
            return graph.depot();
        }
        return graph.getNodes().get(next);
    }

    /**
     * Function that returns the closes node that fits on the car based on a partial solution.
     *
     * @param current Current Node.
     * @return Next Node.
     */
    public Node getNext(Node current, double[] partial) {
        return getNode(current, partial);
    }

    /**
     * Prints the final solution to standard output.
     */
    public void printSolution() {
        System.out.printf("_______________________\nGREEDY\nSOLUTION DISTANCE: %.4f\n_______________________\n", total_distance);
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

    /**
     * Return list of integers representing solution.
     *
     * @return The list.
     */
    public List<Integer> getSolution() {
        List<Integer> filtered = ut.copy(permutation);
        filtered.removeIf(integer -> integer == 0);
        return filtered;
    }

    public void log(PrintWriter printer) {
        printer.printf("2,%.4f\n", total_distance);
    }
}
