package procedures.neuralnetwork;

import utilities.Matrix;
import utilities.Utilities;

import java.util.*;

/**
 * Autoencoder class.
 *
 * Adapted SkipGram algorithm.
 */
public class AutoEncoder {

    /** Input size to the neural network. */
    private final int INPUT_SIZE;

    /** Learning rate of the neural network. */
    private double LEARN_RATE;

    /** Minimal learn rate. */
    private final double MIN_LEARN_RATE;

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

    /** List of ID's in the sample. */
    private List<Integer> sample;

    /** Current row. */
    private int current_row;

    /** Current column. */
    private int current_col;

    /** Additional information flag. */
    private final boolean DEBUG;

    /**
     * Constructor for class SkipGram.
     *
     * Constructs a SkipGram class based on parameters passed.
     *
     * @param num_of_nodes Number of nodes in the graph.
     * @param features Number of features in node embedding.
     * @param learn_rate Rate for learning of the neural network.
     */
    public AutoEncoder(int num_of_nodes, int features, double learn_rate, double min_learn_rate, boolean debug) {
        this.INPUT_SIZE = num_of_nodes;
        this.LEARN_RATE = learn_rate;
        this.MIN_LEARN_RATE = min_learn_rate;
        this.DEBUG = debug;
        x_input = new Matrix(INPUT_SIZE, 1);
        weights1 = new Matrix(INPUT_SIZE, features);
        weights2 = new Matrix(features, INPUT_SIZE);
        weights1.gaussian();
        weights2.gaussian();
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
        System.out.printf("Number of walks: %d\n", WALKS.size());
        double s = 0;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = 0;
            for (int i = 0; i < WALKS.size(); i++) {
                current_row = i;
                for (int j = 0; j < WALKS.get(i).size() - 2; j++) {
                    current_col = j;
                    int id = WALKS.get(i).get(j);
                    forwardPropagate(id);
                    backpropagate();
                    int c = 0;
                    for (int k = 0; k < INPUT_SIZE; k++) {
                        if (sample.contains(k)) {
                            loss += -output.get(k, 0);
                            c++;
                        }
                    }
                    loss += c * Math.log((output.exp()).sum());
                    if (DEBUG) {
                        System.out.println("___________________");
                        System.out.println("Walk: " + Arrays.toString(WALKS.get(i).toArray()));
                        System.out.printf("Current node: %d\n", id);
                        System.out.println("Context: " + Arrays.toString(sample.toArray()));
                        System.out.println("Output:" + Arrays.deepToString(output.get()));
                    }
                }
            }
            if (epoch % 5 == 0 && epoch != 0) {
                System.out.println("___________________");
                System.out.printf("Iter: %4d | %.2fms\n", epoch, (System.nanoTime() - s) / 1000000);
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
     * Predicts the next node in a route with a trained model. For debugging purposes.
     *
     * @param id Node id, over which a next node is predicted.
     */
    public void predict(int id) {
        x_input = new Matrix(INPUT_SIZE, 1);
        x_input.set(id, 0, 1);
        hidden_layer = x_input.vecTimes(weights1);
        output = weights2.transpose().times(hidden_layer);
        output.show();
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
        output = weights2.transpose().times(hidden_layer);
        return output;
    }

    /**
     * Performs the forward propagation of the input.
     *
     * @param id ID of a node that is forward propagated.
     */
    public void forwardPropagate(int id) {
        x_input = new Matrix(INPUT_SIZE, 1);
        x_input.set(id, 0, 1);
        hidden_layer = x_input.vecTimes(weights1);
        sample = getContext();
        Collections.sort(sample);
        output = weights2.transpose().multiplyRows(hidden_layer, sample);
    }

    /**
     * Performs backpropagation of the output.
     */
    public void backpropagate() {
        sample = getContext();
        Matrix error = new Matrix(output.rows(), 1);
        for (int i = 0; i < sample.size(); i++) {
            int id = sample.get(i);
            error.set(id, 0, output.get(id, 0) - i);
        }
        Collections.sort(sample);
        Matrix output_gradient = error.multiplyVectorWithIndices(hidden_layer.transpose(), sample);
        Matrix input_gradient = error.multiplyRowsToVector(weights2.transpose(), sample);
        updateWeights(input_gradient.transpose(), output_gradient);
    }

    /**
     * Function that returns a binary vector ([N x 1] 01-matrix) indicating nodes that are in the context of the current node.
     *
     * 1 in position 'i' means that the node with id 'i' is on the current route in the RandomWalk corpus.
     *
     * @return Binary vector ([N x 1] 01-matrix) representing current context.
     */
    public List<Integer> getContext() {
        List<Integer> result = new LinkedList<>();
        for (int i = current_col + 1; i < WALKS.get(current_row).size(); i++) {
            if (!result.contains(WALKS.get(current_row).get(i))) {
                result.add(WALKS.get(current_row).get(i));
            }
        }
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
        weights2 = weights2.minus(output_gradient.times(LEARN_RATE), sample);
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
