import procedures.grapher.Graph;
import procedures.neuralnetwork.Decoder;
import procedures.neuralnetwork.RandomWalk;
import procedures.neuralnetwork.SkipGram;

import java.util.List;

/**
 * Core class.
 *
 * Main class, that runs the system.
 */
public class Core {

    /** Graph object. */
    private static Graph graph;

    /** Graph parameters. */
    private static final int SIZE = 20;
    private static final int SPARSENESS = 60;
    private static final double EDGE_LENGTH_MEAN = 150;
    private static final double EDGE_LENGTH_DEVIATION = 25;
    private static final double DEMAND_MEAN = 30;
    private static final double DEMAND_DEVIATION = 5;

    /** RandomWalk parameters. */
    private static final int WALKS_PER_NODE = 100;
    private static final double P = 0.5;
    private static final double Q = 0.5;

    /** SkipGram parameters. */
    private static final int FEATURES = 20;
    private static final int SAMPLE_SIZE = 3;
    private static final double LEARN_RATE = 0.0001;
    private static final int EPOCHS = 10;

    /** Problem parameters. */
    private static final double CAPACITY = 500;

    /** The main function. */
    public static void main(String[] args) {
        graph = new Graph(SIZE, SPARSENESS, EDGE_LENGTH_MEAN, EDGE_LENGTH_DEVIATION, CAPACITY);
        graph.set_demand(DEMAND_MEAN, DEMAND_DEVIATION);
        graph.print_nodes();

        RandomWalk walker = new RandomWalk(graph, CAPACITY, WALKS_PER_NODE, P, Q);
        List<List<Integer>> walks = walker.walk();

        SkipGram skipgram = new SkipGram(SIZE, FEATURES, SAMPLE_SIZE, LEARN_RATE, graph);
        skipgram.train(EPOCHS, walks);

        Decoder decoder = new Decoder(graph, skipgram, CAPACITY);
        decoder.decode();
        decoder.finalise();
        decoder.print_solution();
    }
}
