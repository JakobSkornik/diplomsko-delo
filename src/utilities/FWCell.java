package utilities;

/**
 * FWCell class.
 *
 * Each object is stored in a 2D-array and carries information about shortest path
 * between x and y node. Cell is retrievable with array[x][y].
 */
public class FWCell {

    /** ID of the next node, to reconstruct the path. */
    private int next;

    /** Distance of currently stored path. */
    private double distance;

    /**
     * FWCell constructor.
     *
     * Constructs a single FloydWarshall cell.
     *
     * @param next Integer next.
     * @param d Double distance.
     */
    public FWCell(int next, double d) {
        this.next = next;
        this.distance = d;
    }

    /**
     * Sets the values of this object.
     *
     * @param id Value for next.
     * @param d Value for distance.
     */
    public void set(int id, double d) {
        next = id;
        distance = d;
    }

    /**
     * Returns distance of this object.
     *
     * @return Double distance.
     */
    public double getDistance() {
        return this.distance;
    }

    /**
     * Sets only distance to parameter d.
     *
     * @param d Double distance.
     */
    public void setDist(double d) {
        this.distance = d;
    }

    /**
     * Returns the next node to visit when reconstructing the path.
     *
     * @return ID of the next node.
     */
    public int getNext() {
        return this.next;
    }
}
