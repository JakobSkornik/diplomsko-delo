package utilities;

import procedures.grapher.Graph;
import procedures.grapher.Node;

import java.util.List;
import java.util.Random;

/**
 * Utilities class.
 *
 * Provides some general purpose functions.
 */
public class Utilities {

    private Random r = new Random();

    /**
     * Sigmoid function.
     *
     * @param x Argument x.
     * @return sigmoid(x)
     */
    public static float sigmoid(double x) {
        return (float) ((float) 1d / (1 + Math.exp(-x)));
    }

    /**
     * Function that returns a random double in range [l, u].
     *
     * @param l Lower bound for double value.
     * @param u Upper bound for double value.
     * @return Random double in range.
     */
    public double random_double(double l, double u) {
        return (l + Math.random()* (u - l));
    }

    /**
     * Returns a random integer in range [0, u].
     *
     * @param u Upper limit integer.
     * @return Random integer.
     */
    public int random_int(int u) {
        return r.nextInt(u);
    }

    /**
     * Returns the total length of the path.
     *
     * @param graph Graph object.
     * @param path List of paths in order of traversal.
     * @return Double that equals to total path length.
     */
    public double path_length(Graph graph, List<Integer> path) {
        double length = 0;
        Node prev = graph.getNodes().get(path.get(0));
        for (int i = 1; i < path.size(); i++) {
            Node next = graph.getNodes().get(path.get(i));
            length += prev.getEdges().get(next).weight();
            prev = next;
        }
        return length;
    }
}
