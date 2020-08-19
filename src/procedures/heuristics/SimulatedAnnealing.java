package procedures.heuristics;

import procedures.grapher.Graph;
import utilities.FloydWarshall;
import utilities.Utilities;

import java.io.PrintWriter;
import java.util.*;

public class SimulatedAnnealing {

    private Scanner sc = new Scanner(System.in);

    private Graph graph;

    private FloydWarshall floydWarshall;

    private Greedy greedy;

    private double CAPACITY;

    private List<Integer> current_solution;

    private List<Integer> best_solution;

    private double final_distance;

    private Utilities ut;

    public SimulatedAnnealing(Graph graph, FloydWarshall fw, Greedy greedy, double capacity, Utilities ut) {
        this.graph = graph;
        this.floydWarshall = fw;
        this.greedy = greedy;
        this.CAPACITY = capacity;
        this.ut = ut;
    }

    public void solve(double lambda, double starting_temperature) {
        current_solution = greedy.getSolution();
        best_solution = ut.copy(current_solution);
        for (double temperature = starting_temperature; temperature > 1; temperature *= lambda) {
            List<Integer> new_solution = generateNeighbour();
            if (objective(new_solution) < objective(best_solution)) {
                best_solution = ut.copy(new_solution);
            }
            if (objective(new_solution) < objective(current_solution)) {
                current_solution = ut.copy(new_solution);
            }
            else {
                double probability = Math.exp(-(objective(new_solution) - objective(current_solution)) / temperature);
                double r = ut.randomDouble(0, 1);
                if (r < probability) {
                    current_solution = ut.copy(new_solution);
                }
            }
        }
        best_solution = floydWarshall.finalizePath(floydWarshall.permutationToPath(best_solution, CAPACITY));
        final_distance = floydWarshall.distance();
    }

    public List<Integer> generateNeighbour() {
        double probability = ut.randomDouble(0, 1);
        List<Integer> result;
        if (probability > 0.5) {
            int first = ut.randomInt(current_solution.size());
            int second = ut.randomInt(current_solution.size()) ;
            result = ut.copy(current_solution);
            Collections.swap(result, first, second);
        }
        else {
            int first = ut.randomInt(current_solution.size());
            int second = ut.randomInt(current_solution.size());
            int id = current_solution.get(first);
            result = ut.copy(current_solution);
            result.remove(first);
            result.add(second, id);
        }
        return floydWarshall.fixPermutation(result, CAPACITY);
    }

    /**
     * Returns boolean indicating whether all demands have been met.
     *
     * @return The boolean.
     */
    public boolean notClean(double[] cleanliness) {
        for (double d : cleanliness) {
            if (d != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints the final solution to standard output.
     */
    public void printSolution() {
        System.out.printf("_______________________\nSIMULATED ANNEALING\nSOLUTION DISTANCE: %.4f\n_______________________\n", final_distance);
        int i = 1;
        for (int a : best_solution) {
            if (a == 0 && i != best_solution.size() && i != 1) {
                System.out.print("0\n0 ");
            }
            else {
                System.out.printf("%d ", a);
            }
            i++;
        }
        System.out.println();
    }

    public double objective(List<Integer> permutation) {
        floydWarshall.finalizePath(floydWarshall.permutationToPath(permutation, CAPACITY));
        return floydWarshall.distance();
    }

    public void log(PrintWriter printer) {
        printer.printf("3,%.4f\n", final_distance);
    }
}
