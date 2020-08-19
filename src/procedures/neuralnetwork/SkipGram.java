package procedures.neuralnetwork;

import utilities.Matrix;
import utilities.Utilities;

import java.util.*;

/**
 * SkipGram class.
 *
 * Adapted SkipGram algorithm.
 */
public class SkipGram {

    /** Utilities. */
    private final Utilities ut;

    /** Input size to the neural network. */
    private final int INPUT_SIZE;

    /** Learning rate of the neural network. */
    private double LEARN_RATE;

    /** Minimal learn rate. */
    private final double MIN_LEARN_RATE;

    /** Sample size of negative sampling. */
    private final int SAMPLE_SIZE;

    /** Size of context. */
    private final int CONTEXT_SIZE;

    /** Random walks stored in a list of lists. */
    private List<List<Integer>> WALKS;

    /** Matrix of weights between input and hidden layer. */
    private Matrix weights1;

    /** Matrix of weights between hidden and output layer. */
    private Matrix weights2;

    /** Matrix of neurons in the hidden layer. */
    private Matrix hidden_layer;

    /** Matrix of outputs. */
    private Matrix output;

    /** One-Hot vector indicating input node. */
    private Matrix x_input;

    /** Intermediate output matrix. */
    private Matrix intermediate_output;

    /** Location of the current node in walks. */
    private int current_row;
    private int current_col;

    /** List of ID's in the sample (positive node is added before computing negative sample). */
    private List<Integer> neg_sample;

    /** Precomputed sets of contexts. */
    private HashSet<Integer>[][] contexts;

    /** Unigram distribution of words. */
    private double[] unigram;

    /** Additional information flag. */
    private final boolean DEBUG;

    /**
     * Constructor for class SkipGram.
     *
     * Constructs a SkipGram class based on parameters passed.
     *
     * @param num_of_nodes Number of nodes in the graph.
     * @param features Number of features in node embedding.
     * @param sample_size Size of a negative sample.
     * @param learn_rate Rate for learning of the neural network.
     */
    public SkipGram(int num_of_nodes, int features, int sample_size, int context_size, double learn_rate, double min_learn_rate, boolean debug, Utilities ut) {
        this.INPUT_SIZE = num_of_nodes;
        this.LEARN_RATE = learn_rate;
        this.MIN_LEARN_RATE = min_learn_rate;
        this.SAMPLE_SIZE = sample_size;
        this.CONTEXT_SIZE = context_size;
        this.DEBUG = debug;
        this.ut = ut;
        x_input = new Matrix(INPUT_SIZE, 1);
        weights1 = new Matrix(INPUT_SIZE, features);
        weights2 = new Matrix(features, INPUT_SIZE);
        weights1.heInitialization(INPUT_SIZE);
        weights2.heInitialization(INPUT_SIZE);
    }

    /**
     * Train function.
     *
     * Performs 'epochs' number of iterations on each integer element of 'walks'.
     * Trains the SkipGram model.
     * @param epochs Number of iterations of training.
     * @param walks List of Lists of integers representing nodes.
     */
    public void train(int epochs, List<List<Integer>> walks) {
        this.WALKS = walks;
        precomputeContexts();
        createUnigramDistrib();
        System.out.printf("Size of corpus: %d\n", WALKS.size());
        double s = 0;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = 0;
            for (int i = 0; i < WALKS.size(); i++) {
                for (int j = 0; j < WALKS.get(i).size(); j++) {
                    current_row = i;
                    current_col = j;
                    int center_node = WALKS.get(i).get(j);
                    int lower = Math.max(0, j - CONTEXT_SIZE);
                    int upper = Math.min(WALKS.get(i).size(), j + CONTEXT_SIZE + 1);
                    for (int pos_index = lower; pos_index < upper; pos_index++) {
                        if (pos_index != j) {
                            int pos_node = WALKS.get(i).get(pos_index);
                            forwardPropagate(center_node, pos_node);
                            backpropagate();
                            int c = 0;
                            for (int l = 0; l < INPUT_SIZE; l++) {
                                if (contexts[i][j].contains(l)) {
                                    loss += -intermediate_output.get(l, 0);
                                    c++;
                                }
                            }
                            loss += c * Math.log((intermediate_output.exp()).sum());
                            if (DEBUG) {
                                System.out.println("___________________");
                                System.out.println("Walk: " + Arrays.toString(WALKS.get(i).toArray()));
                                System.out.printf("Current node: %d\n", center_node);
                                System.out.printf("Positive pair: [%d, %d]\n", center_node, pos_node);
                                System.out.println("Context: " + Arrays.toString(contexts[i][j].toArray()));
                                System.out.println("Negative sample with positive pair: " + Arrays.toString(neg_sample.toArray()));
                                System.out.println("Output (sigmoided):" + Arrays.deepToString(output.get()));
                            }
                        }
                    }
                }
            }
            if (epoch % 5 == 0 && epoch != 0) {
                System.out.printf("Iter.: %d | %.2fms from last measurement.\n", epoch, (System.nanoTime() - s) / 1000000);
                System.out.printf("Loss: %.2f\n", loss);
                s = System.nanoTime();
            }
            else if (epoch == 0) {
                s = System.nanoTime();
            }
            LEARN_RATE = LEARN_RATE > MIN_LEARN_RATE ? LEARN_RATE * (1 / (1 + LEARN_RATE * epoch)) : MIN_LEARN_RATE;
        }
    }

    /**
     * Calculates unigram distribution of nodes, from which negative samples are sampled.
     */
    public void createUnigramDistrib() {
        double[] frequency = new double[INPUT_SIZE];
        unigram = new double[INPUT_SIZE];
        for (List<Integer> list : WALKS) {
            for (int node : list) {
                frequency[node]++;
            }
        }
        int sum = 0;
        for (double node_freq : frequency) {
            sum += node_freq;
        }
        double prev = 0;
        for (int i = 0; i < INPUT_SIZE; i++) {
            unigram[i] = frequency[i] / sum + prev;
            prev = unigram[i];
        }
    }

    /**
     * Precomputes context for every node in corpus 'WALKS'. Offers slight improvement,
     * as contexts aren't calculated for every node in corpus in each iteration of training.
     */
    public void precomputeContexts() {
        double s = System.nanoTime();
        System.out.println("Precomputing contexts...");
        contexts = new HashSet[WALKS.size()][WALKS.get(0).size()];
        for (int i = 0; i < WALKS.size(); i++) {
            for (int j = 0; j < WALKS.get(i).size(); j++) {
                contexts[i][j] = new HashSet<>();
                int lower = Math.max(0, j - CONTEXT_SIZE);
                int upper = Math.min(WALKS.get(i).size(), j + CONTEXT_SIZE + 1);
                for (int k = lower; k < upper; k++) {
                    if (k != j) {
                        contexts[i][j].add(WALKS.get(i).get(k));
                    }
                }
            }
        }
        System.out.printf("Computing contexts took %.1f ms.\n", (System.nanoTime() - s) / 1000000);
    }

    /**
     * Predicts the next node in a route with a trained model. For debugging purposes.
     *
     * @param id Node id, over which a next node is predicted.
     */
    public void predict(int id) {
        x_input = new Matrix(INPUT_SIZE, 1);
        x_input.set(id, 0, 1);
        hidden_layer = x_input.vecTimes(weights1);
        intermediate_output = weights2.transpose().times(hidden_layer);
        output = softmax(intermediate_output);
        output.show();
        System.out.printf("Prediction for %d: %d with confidence %.2f\n", id, output.whichMax(), output.max());
    }

    /**
     * Softmax function that takes a vector ([N x 1] matrix) as input and outputs a vector ([N x 1] matrix).
     *
     * Final version uses negative sampling to increase performance and softmax is only used in prediction.
     *
     * @param A Input vector ([N x 1] matrix).
     * @return Output vector ([N x 1] matrix).
     */
    public Matrix softmax(Matrix A) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < A.rows(); i++) {
            if (A.get(i, 0) > max) {
                max = A.get(i, 0);
            }
        }
        double sum = 0;
        double[] e_x = new double[A.rows()];
        for (int i = 0; i < A.rows(); i++) {
            e_x[i] = Math.exp(A.get(i, 0) - max);
            sum += e_x[i];
        }
        for (int i = 0; i < A.rows(); i++) {
            e_x[i] = e_x[i] / sum;
        }
        return new Matrix(e_x);
    }

    /**
     * Predicts the next node in a route with a trained model and returns the output matrix.
     *
     * @param id Node id, over which a next node is predicted.
     * @return Output matrix object. Elements represent the probability of each node being the next in route.
     */
    public Matrix process(int id) {
        x_input = new Matrix(INPUT_SIZE, 1);
        x_input.set(id, 0, 1);
        hidden_layer = x_input.vecTimes(weights1);
        intermediate_output = weights2.transpose().times(hidden_layer);
        return softmax(intermediate_output);
    }

    /**
     * Performs the forward propagation of the input.
     *
     * @param id ID of a node that is forward propagated.
     */
    public void forwardPropagate(int id, int pos_id) {
        x_input = new Matrix(INPUT_SIZE, 1);
        x_input.set(id, 0, 1);
        hidden_layer = x_input.vecTimes(weights1);
        neg_sample = negativeSample(pos_id);
        intermediate_output = weights2.transpose().multiplyRows(hidden_layer, neg_sample);
        output = intermediate_output.sigmoid(neg_sample);
    }

    /**
     * Performs backpropagation of the output.
     */
    public void backpropagate() {
        Matrix context = getContext();
        Matrix error = new Matrix(output.rows(), 1);
        for (int i : neg_sample) {
            error.set(i, 0, output.get(i, 0) - context.get(i, 0));
        }
        Matrix output_gradient = error.multiplyVectorWithIndices(hidden_layer.transpose(), neg_sample);
        Matrix input_gradient = error.multiplyRowsToVector(weights2.transpose(), neg_sample);
        updateWeights(input_gradient.transpose(), output_gradient);
    }

    /**
     * Function that returns a binary vector ([N x 1] 01-matrix) indicating nodes that are in the context of the current node.
     *
     * 1 in position 'i' means that the node with id 'i' is on the current route in the RandomWalk corpus.
     *
     * @return Binary vector ([N x 1] 01-matrix) representing current context.
     */
    public Matrix getContext() {
        Matrix A = new Matrix(INPUT_SIZE, 1);
        for (int i : neg_sample) {
            if (contexts[current_row][current_col].contains(i)) {
                A.set(i, 0, 1);
            }
        }
        return A;
    }

    /**
     * Returns a negative sample.
     *
     * Each element of the List is an ID of sampled node.
     *
     * @return List of ID's.
     */
    public List<Integer> negativeSample(int pos_id) {
        LinkedList<Integer> result = new LinkedList<>();
        HashSet<Integer> selected = new HashSet<>();
        result.add(pos_id);
        selected.add(pos_id);
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            double random = ut.randomDouble(0, 1);
            int node = 0;
            while (random > unigram[node]) {
                node++;
            }
            if (selected.contains(node) || node == WALKS.get(current_row).get(current_col) || contexts[current_row][current_col].contains(node)) {
                i--;
            }
            else {
                selected.add(node);
                result.add(node);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Updates weights with input and output gradient matrices.
     *
     * @param input_gradient Input gradient matrix.
     * @param output_gradient Output gradient matrix.
     */
    public void updateWeights(Matrix input_gradient, Matrix output_gradient) {
        int id = WALKS.get(current_row).get(current_col);
        weights1 = weights1.minus(input_gradient.times(LEARN_RATE), id);
        weights2 = weights2.minus(output_gradient.times(LEARN_RATE), neg_sample);
    }

    /**
     * Returns a matrix of node embeddings. In a graph with V nodes and a SkipGram model with F features,
     * the matrix is of dimensions [V x F].
     *
     * @return Matrix of embedding.
     */
    public Matrix embedding() {
        return this.weights1;
    }
}
