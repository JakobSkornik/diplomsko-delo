import procedures.grapher.Graph;
import procedures.neuralnetwork.GreedyDecoder;
import procedures.neuralnetwork.RandomWalk;
import procedures.neuralnetwork.SkipGram;

import java.util.List;

/**
 * Core class.
 *
 * Main class, that runs the system.
 */
public class Core {

    /** Graph parameters. */
    private static final int SIZE = 50;
    private static final int SPARSENESS = 99;
    private static final double EDGE_LENGTH_MEAN = 150;
    private static final double EDGE_LENGTH_DEVIATION = 25;
    private static final double DEMAND_MEAN = 30;
    private static final double DEMAND_DEVIATION = 5;

    /** RandomWalk parameters. */
    private static final int WALKS_PER_NODE = 20;
    private static final int WALK_LENGTH = 8;
    private static final double P = 0.5;
    private static final double Q = 0.5;

    /** SkipGram parameters. */
    private static final int FEATURES = 32;
    private static final int SAMPLE_SIZE = 5;
    private static final int CONTEXT_SIZE = 1;
    private static final double LEARN_RATE = 0.005;
    private static final int EPOCHS = 500;

    /** Flag that enables additional debugging information. */
    private static final boolean DEBUG_MODE = false;

    /** Problem parameters. */
    private static final double CAPACITY = 500;

    /** The main function. */
    public static void main(String[] args) {

        // Create a random graph.
        Graph graph = new Graph(SIZE, SPARSENESS, EDGE_LENGTH_MEAN, EDGE_LENGTH_DEVIATION, CAPACITY);
        graph.set_demand(DEMAND_MEAN, DEMAND_DEVIATION);
        graph.print_graph();

        // Perform RandomWalk algorithm.
        RandomWalk walker = new RandomWalk(graph, WALK_LENGTH, WALKS_PER_NODE, P, Q);
        List<List<Integer>> walks = walker.walk();

        // Train a SkipGram model.
        SkipGram skipgram = new SkipGram(SIZE, FEATURES, SAMPLE_SIZE, CONTEXT_SIZE, LEARN_RATE, DEBUG_MODE);
        skipgram.train(EPOCHS, walks);
        skipgram.predict(0);
        skipgram.predict(4);
        skipgram.predict(7);

        // Decode a solution using greedy procedure from the trained model.
        GreedyDecoder greedyDecoder = new GreedyDecoder(graph, skipgram, CAPACITY);
        greedyDecoder.decode();
        greedyDecoder.finalise();
        greedyDecoder.print_solution();
    }
}
