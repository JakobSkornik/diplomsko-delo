package utilities;

import procedures.grapher.Edge;
import procedures.grapher.Graph;
import procedures.grapher.Node;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    /** Distance of path is stored here after calling permuationToPath() method. */
    private double permutation_distance;

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
    public FWCell[][] getPaths() {
        return this.dists;
    }

    /**
     * Transform a permutation of integers into a real path.
     *
     * Each pair of nodes is replaced with a shortest path between them.
     * Distance of the path is stored in 'permutation_distance' field.
     *
     * @param permutation The initial permutation.
     * @return Final permutation.
     */
    public List<Integer> finalizePath(List<Integer> permutation) {
        List<Integer> path = new LinkedList<>();
        permutation_distance = 0;
        path.add(0);
        for (int i = 0; i < permutation.size() - 1; i++) {
            int current = permutation.get(i);
            int next = permutation.get(i + 1);
            LinkedList<Integer> temp = path(current, next);
            permutation_distance += dists[current][next].getDistance();
            path.addAll(temp);
        }
        return path;
    }

    /**
     * Transform a permutation of integers into a real path.
     *
     * Each pair of nodes is replaced with a shortest path between them.
     * Distance of the path is stored in 'permutation_distance' field.
     *
     * @param permutation The initial permutation.
     * @return Final permutation.
     */
    public List<Integer> permutationToPath(List<Integer> permutation, double capacity) {
        List<Integer> path = new LinkedList<>();
        double weight = 0;
        double[] demands = new double[graph.size()];
        for (Node node : graph.getNodes().values()) {
            demands[node.id()] = node.demand();
        }
        path.add(0);
        int current = 0;
        for (int i = 0; i < permutation.size(); i++) {
            int next = permutation.get(i);
            if (demands[next] > 0) {
                LinkedList<Integer> temp = path(current, next);
                int idx = 0;
                while (current != next) {
                    int id = temp.get(idx);
                    if (id == 0) {
                        weight = 0;
                        idx++;
                    }
                    else if (weight + demands[id] <= capacity) {
                        weight += demands[id];
                        demands[id] = 0;
                        path.add(id);
                        idx++;
                    }
                    else {
                        LinkedList<Integer> return_path = path(id, 0);
                        for (int id_return : return_path) {
                            if (weight + demands[id_return] <= capacity) {
                                weight += demands[id_return];
                                demands[id_return] = 0;
                            }
                        }
                        path.add(id);
                        path.addAll(return_path);
                        temp = path(0, next);
                        weight = 0;
                        id = 0;
                        idx = 0;
                    }
                    current = id;
                }
            }
        }
        LinkedList<Integer> return_path = path(current, 0);
        path.addAll(return_path);
        return path;
    }

    public List<Integer> fixPermutation(List<Integer> permutation, double capacity) {
        List<Integer> fixed = new LinkedList<>();
        double weight = 0;
        double[] demands = new double[graph.size()];
        for (Node node : graph.getNodes().values()) {
            demands[node.id()] = node.demand();
        }
        int current = 0;
        for (int i = 0; i < permutation.size(); i++) {
            int next = permutation.get(i);
            if (demands[next] > 0) {
                LinkedList<Integer> temp = path(current, next);
                int idx = 0;
                while (current != next) {
                    int id = temp.get(idx);
                    if (id == 0) {
                        weight = 0;
                        idx++;
                    }
                    else if (weight + demands[id] <= capacity) {
                        if (demands[id] > 0) {
                            fixed.add(id);
                        }
                        weight += demands[id];
                        demands[id] = 0;
                        idx++;
                    }
                    else {
                        LinkedList<Integer> return_path = path(id, 0);
                        for (int id_return : return_path) {
                            if (weight + demands[id_return] <= capacity) {
                                if (demands[id_return] > 0) {
                                    fixed.add(id_return);
                                }
                                weight += demands[id_return];
                                demands[id_return] = 0;

                            }
                        }
                        temp = path(0, next);
                        weight = 0;
                        id = 0;
                        idx = 0;
                    }
                    current = id;
                }
            }
        }
        return fixed;
    }

    /**
     * Returns permutation distance.
     *
     * @return The distance.
     */
    public double distance() {
        return this.permutation_distance;
    }
}
