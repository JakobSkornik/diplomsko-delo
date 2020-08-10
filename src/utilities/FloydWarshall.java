package utilities;

import procedures.grapher.Edge;
import procedures.grapher.Graph;
import procedures.grapher.Node;

import java.util.LinkedList;

/**
 * FloydWarshall class.
 *
 * Performs the Floyd-Warshall algorithm.
 */
public class FloydWarshall {

    /** Pointer to graph object. */
    private final Graph graph;

    /** 2D-array of FloydWarshall cells. */
    private FWCell[][] dists;

    /**
     * Constructor for FloydWarshall object.
     *
     * @param g Graph object.
     */
    public FloydWarshall(Graph g) {
        this.graph = g;
    }

    /**
     * Calculates shortest paths and their distances.
     */
    public void calculate() {
        int size = graph.getNodes().size();
        FWCell[][] distances = new FWCell[size][size];
        dists = new FWCell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distances[i][j] = new FWCell(-1, Double.MAX_VALUE);
            }
        }
        for (Node node : graph.getNodes().values()) {
            for (Edge edge : node.getEdges().values()) {
                distances[node.id()][edge.to().id()].set(edge.to().id(), edge.weight());
            }
        }
        for (int i = 0; i < size; i++) {
            distances[i][i].set(i, 0);
        }
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (distances[i][j].getDistance() > distances[i][k].getDistance() + distances[k][j].getDistance()) {
                        distances[i][j].set(distances[i][k].getNext(), distances[i][k].getDistance() + distances[k][j].getDistance());
                    }
                }
            }
        }
        dists = distances;
    }

    /**
     * Reconstructs paths based on values 'next' in each FWCell.
     *
     * @param from Index of starting node.
     * @param to Index of target node.
     * @return List representing shortest path.
     */
    public LinkedList<Integer> path(int from, int to) {
        if (dists[from][to].getNext() == -1) {
            return null;
        }
        LinkedList<Integer> result = new LinkedList<>();
        while (from != to) {
            from = dists[from][to].getNext();
            result.add(from);
        }
        return result;
    }

    /**
     * Returns 2D-array of FWCells.
     *
     * @return The 2D-array.
     */
    public FWCell[][] get_paths() {
        return this.dists;
    }
}
