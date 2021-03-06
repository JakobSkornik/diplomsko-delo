import procedures.grapher.Graph;
import procedures.heuristics.Greedy;
import procedures.heuristics.SimulatedAnnealing;
import procedures.neuralnetwork.*;
import utilities.FloydWarshall;
import utilities.Utilities;

import java.io.PrintWriter;
import java.util.List;

/**
 * Core class.
 *
 * Main class, that runs the system.
 */
public class Core {

    private static final int NUMBER_OF_RUNS = 10;

    /** Graph parameters. */
    private static final int SIZE = 50;
    private static final int SPARSENESS = 60;
    private static final double EDGE_LENGTH_MEAN = 150;
    private static final double EDGE_LENGTH_DEVIATION = 25;
    private static final double DEMAND_MEAN = 30;
    private static final double DEMAND_DEVIATION = 10;

    /** RandomWalk parameters. */
    private static final int WALKS_PER_NODE = 40;
    private static final int WALK_LENGTH = 10;
    private static final double P = 0.2;
    private static final double Q = 0.8;

    /** AutoEncoder parameters. */
    private static final int FEATURES = 16;
    private static final int SAMPLE_SIZE = 3;
    private static final int CONTEXT_SIZE = 1;
    private static final double LEARN_RATE = 0.01;
    private static final double MIN_LEARN_RATE = 0.0001;
    private static final int EPOCHS = 50;

    /** Flag that enables additional debugging information. */
    private static final boolean DEBUG_MODE = false;

    /** Flag that enables result logging to file. */
    private static final boolean LOGGING = true;

    /** Problem parameters. */
    private static final double CAPACITY = 100;

    /** Utilities. */
    private static final Utilities ut = new Utilities();

    /** The main function. */
    public static void main(String[] args) {

        // RUN N TIMES


        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            // Create a random graph.
            Graph graph = new Graph(SIZE, SPARSENESS, EDGE_LENGTH_MEAN, EDGE_LENGTH_DEVIATION, CAPACITY, ut);
            graph.setDemand(DEMAND_MEAN, DEMAND_DEVIATION);
            graph.printGraph();

            // Perform RandomWalk algorithm.
            RandomWalk walker = new RandomWalk(graph, WALK_LENGTH, WALKS_PER_NODE, P, Q, ut);
            List<List<Integer>> walks = walker.walk();

            // Position encoder.
            AutoEncoder autoEncoder = new AutoEncoder(SIZE, FEATURES, LEARN_RATE, MIN_LEARN_RATE, DEBUG_MODE);
            autoEncoder.train(EPOCHS, walks);

            // 01 encoder.
            AutoEncoder01 autoEncoder01 = new AutoEncoder01(SIZE, FEATURES, SAMPLE_SIZE, CONTEXT_SIZE, LEARN_RATE, MIN_LEARN_RATE, DEBUG_MODE, ut);
            autoEncoder01.train(EPOCHS, walks);

            // Compute FloydWarshall matrix.
            FloydWarshall floydWarshall = new FloydWarshall(graph);
            floydWarshall.calculate();

            // Compute greedy decoder solution.
            GreedyDecoder greedyDecoder = new GreedyDecoder(graph, autoEncoder, CAPACITY, floydWarshall);
            greedyDecoder.decode();
            greedyDecoder.printSolution();

            // Compute a greedy solution.
            Greedy greedy = new Greedy(graph, floydWarshall, CAPACITY, ut);
            greedy.solve();
            greedy.printSolution();

            // Compute a greedy 01 decoding.
            GreedyDecoder01 greedyDecoder01 = new GreedyDecoder01(graph, autoEncoder01, CAPACITY, floydWarshall);
            greedyDecoder01.decode();
            greedyDecoder01.printSolution();

            // Simulated annealing.
            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(graph, floydWarshall, greedy, CAPACITY, ut);
            simulatedAnnealing.solve(0.995, 1000);
            simulatedAnnealing.printSolution();

            // Save results to file.
            if (LOGGING) {
                try {
                    PrintWriter printWriter = new PrintWriter(SIZE + "_" + SPARSENESS + "_"  + i + ".txt");
                    printWriter.println(SIZE);
                    printWriter.println(((double) SPARSENESS) / ((double) SIZE));
                    greedy.log(printWriter);
                    greedyDecoder.log(printWriter);
                    greedyDecoder01.log(printWriter);
                    simulatedAnnealing.log(printWriter);
                    printWriter.close();
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed to write to file.");
                }
            }
        }
    }
}
