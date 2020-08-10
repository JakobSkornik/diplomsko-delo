package procedures.neuralnetwork;

import procedures.grapher.Graph;
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
    private final Utilities ut = new Utilities();

    /** Input size to the neural network. */
    private final int INPUT_SIZE;

    /** Learning rate of the neural network. */
    private final double LEARN_RATE;

    /** Sample size of negative sampling. */
    private final int SAMPLE_SIZE;

    /** Pointer to graph. */
    private final Graph GRAPH;

    /** Random walks stored in a list of lists. */
    private List<List<Integer>> WALKS;

    /** Matrix of weights between input and hidden layer. */
    private Matrix weights1;

    /** Matrix of weights between hidden and output layer. */
    private Matrix weights2;

    /** Matrix of neurons in the hiddden layer. */
    private Matrix hidden_layer;

    /** Matrix of outputs. */
    private Matrix output;

    /** One-Hot vector indicating input node. */
    private Matrix x_input;

    /** Location of the current node in walks. */
    private int current_row;
    private int current_col;

    /** Length of a route of an individual vehicle. */
    private double path_length;

    /** List of id's in the sample. */
    private List<Integer> neg_sample;

    /**
     * Constructor for class SkipGram.
     *
     * Constructs a SkipGram class based on parameters passed.
     *
     * @param num_of_nodes Number of nodes in the graph.
     * @param features Number of features in node embedding.
     * @param sample_size Size of a negative sample.
     * @param learn_rate Rate for learning of the neural network.
     * @param GRAPH Graph object on which learning is performed.
     */
    public SkipGram(int num_of_nodes, int features, int sample_size, double learn_rate, Graph GRAPH) {
        this.INPUT_SIZE = num_of_nodes;
        this.LEARN_RATE = learn_rate;
        this.GRAPH = GRAPH;
        this.SAMPLE_SIZE = sample_size;
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
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < WALKS.size(); i++) {
                path_length = ut.path_length(GRAPH, walks.get(i));
                for (int j = 0; j < WALKS.get(i).size(); j++) {
                    current_row = i;
                    current_col = j;
                    int node_id = WALKS.get(i).get(j);
                    forward_propagate(node_id);
                    backpropagate();
                }
            }
            if (epoch % 50 == 0) {
                System.out.printf("%d. iterations\n", epoch);
            }
        }
    }

    /**
     * Predicts the next node in a route with a trained model. For debugging purposes.
     *
     * @param id Node id, over which a next node is predicted.
     */
    public void predict(int id) {
        forward_propagate(id);
        output.show();
        System.out.printf("MAX: %d: %.2f\n", output.which_max(), output.max());
        weights1.show_row(id);
    }

    /**
     * Predicts the next node in a route with a trained model and returns the output matrix.
     *
     * @param id Node id, over which a next node is predicted.
     * @return Output matrix object. Elements represent the probability of each node being the next in route.
     */
    public Matrix process(int id) {
        forward_propagate(id);
        return output;
    }

    /**
     * Performs the forward propagation of the input.
     *
     * @param id ID of a node that is forward propagated.
     */
    public void forward_propagate(int id) {
        x_input = new Matrix(INPUT_SIZE, 1);
        x_input.set(id, 0, 1);
        hidden_layer = x_input.vec_times(weights1);
        neg_sample = negative_sample();
        Matrix intermediate_output = weights2.transpose().multiply_rows(hidden_layer, neg_sample);
        output = intermediate_output.sigmoid(neg_sample);
    }

    /**
     * Performs back propagation of the output.
     */
    public void backpropagate() {
        Matrix context = getContext();
        Matrix error = new Matrix(output.rows(), 1);
        for (int i : neg_sample) {
            error.set(i, 0, output.get(i, 0) - context.get(i, 0) - (1 / path_length));
        }
        Matrix output_gradient = hidden_layer.times(error.transpose());
        Matrix input_gradient = weights1.transpose().times(error);
        input_gradient = x_input.times(input_gradient.transpose());
        update_weights(input_gradient, output_gradient);
    }

    /**
     * Softmax function that takes a vector ([N x 1] matrix) as input and outputs a vector ([N x 1] matrix).
     *
     * Final version uses negative sampling to increase performance.
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
     * Function that returns a binary vector ([N x 1] 01-matrix) indicating nodes that are in the context of the current node.
     *
     * 1 in position 'i' means that the node with id 'i' is on the current route in the RandomWalk corpus.
     *
     * @return Binary vector ([N x 1] 01-matrix) representing current context.
     */
    public Matrix getContext() {
        HashSet<Integer> walk_nodes = new HashSet<>();
        for (int a : WALKS.get(current_row)) {
            if (!walk_nodes.contains(a) && WALKS.get(current_row).get(current_col) != a) {
                walk_nodes.add(a);
            }
        }
        Matrix A = new Matrix(INPUT_SIZE, 1);
        for (int i : neg_sample) {
            if (walk_nodes.contains(i)) {
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
    public List<Integer> negative_sample() {
        LinkedList<Integer> result = new LinkedList<>();
        LinkedList<Integer> domain = new LinkedList<>();
        for (int i = 0; i < GRAPH.size(); i++) domain.add(i);
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            int id = ut.random_int(domain.size());
            result.add(domain.get(id));
            domain.remove(id);
        }
        return result;
    }

    /**
     * Updates weights with input and output gradient matrices.
     *
     * @param input_gradient Input gradient matrix.
     * @param output_gradient Output gradient matrix.
     */
    public void update_weights(Matrix input_gradient, Matrix output_gradient) {
        int id = WALKS.get(current_row).get(current_col);
        weights1 = weights1.minus(input_gradient.times(LEARN_RATE), id);
        weights2 = weights2.minus(output_gradient.times(LEARN_RATE), neg_sample);
    }

    /**
     * Returns a matrix of node embeddings. In a graph with V nodes and a SkipGram model with F features,
     * the matrix is of dimensions [V x F].
     * @return
     */
    public Matrix embedding() {
        return this.weights1;
    }
}
