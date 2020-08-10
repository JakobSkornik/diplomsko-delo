package procedures.neuralnetwork;

import procedures.grapher.Edge;
import procedures.grapher.Graph;
import procedures.grapher.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * RandomWalk class.
 *
 * Performs the adapted RandomWalk algorithm and outputs a List of Lists of integers
 * representing routes.
 */
public class RandomWalk {

    /** Pointer to graph object. */
    private final Graph graph;

    /** Maximum allowed length of the walk. */
    private final double WALK_LENGTH;

    /** Walks on each node. */
    private final int WALKS_PER_NODE;

    /** Constant P. Probability to perform Depth First Search. */
    private final double P;

    /** Constant Q. Probability to perform Breadth First Search. */
    private final double Q;

    /** 2D-array of demands. */
    private double[] dynamic_demands;

    /** Weight of the car. */
    private double car_weight;

    /**
     * RandomWalk constructor.
     *
     * Constructs a RandomWalk object based on parameters.
     *
     * @param graph Graph object.
     * @param length Length of walks (capacity of the car).
     * @param walks_per_node Walks per node in the graph.
     * @param p Probability p.
     * @param q Probability q.
     */
    public RandomWalk(Graph graph, double length, int walks_per_node, double p, double q) {
        this.graph = graph;
        this.WALK_LENGTH = length;
        this.WALKS_PER_NODE = walks_per_node;
        this.P = p;
        this.Q = q;
    }

    /**
     * Function that performs the walks and returns a List of Lists of integers.
     *
     * @return List of Lists.
     */
    public List<List<Integer>> walk() {
        List<List<Integer>> walks = new LinkedList<>();
        for (int iter = 0; iter < WALKS_PER_NODE; iter++) {
            for (Node node : graph.getNodes().values()) {
                List<Integer> walk = single_walk(node);
                walks.add(walk);
            }
        }
        return walks;
    }

    /**
     * Performs a single walk from a node specified in the parameter.
     *
     * @param node Node object.
     * @return List of integers representing a single walk.
     */
    public List<Integer> single_walk(Node node) {
        List<Integer> walk = new LinkedList<>();
        Node current = node;
        Node prev = graph.depot();
        car_weight = 0;
        dynamic_demands = new double[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            dynamic_demands[i] = graph.getNodes().get(i).demand();
        }
        while (car_weight < WALK_LENGTH) {
            Node next = getNeighbour(current, prev);
            walk.add(next.id());
            prev = current;
            current = next;
        }
        return walk;
    }

    /**
     * Returns a neighbour sampled from a specific distribution.
     *
     * Parameters 'P' and 'Q' specify this distribution. Walks can be 'deep' or 'wide'
     * to better cover characteristics of the graph.
     *
     * @param current
     * @param prev
     * @return
     */
    public Node getNeighbour(Node current, Node prev) {
        List<Node> neighbours = new LinkedList<>();
        List<Double> probabilities = new LinkedList<>();
        for (Edge edge : current.getEdges().values()) {
            neighbours.add(edge.to());
            if (prev.isNeighbour(edge.to())) {
                probabilities.add(1.0 * edge.weight());
            }
            else if (prev.id() == edge.to().id()) {
                probabilities.add((1 / P) * edge.weight());
            }
            else {
                probabilities.add((1 / Q) * edge.weight());
            }
        }
        int index = getRandomIndex(probabilities);
        Node result = neighbours.get(index);
        car_weight += dynamic_demands[result.id()];
        dynamic_demands[result.id()] = 0;
        return result;
    }

    /**
     * Returns a random index of a List representing a probability distribution.
     *
     * @param probs List of probabilities.
     * @return Index of a randomly selected element.
     */
    public int getRandomIndex(List<Double> probs) {
        double sum = 0;
        for (double p : probs) {
            sum += p;
        }
        List<Double> normalized = new LinkedList<>();
        double prev = 0;
        for (double p : probs) {
            double temp = p / sum + prev;
            normalized.add(temp);
            prev = temp;
        }
        Random r = new Random();
        double x = r.nextDouble();
        int i = 0;
        while (x > normalized.get(i)) {
            i++;
        }
        return i;
    }
}
