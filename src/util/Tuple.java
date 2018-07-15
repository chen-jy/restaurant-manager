package util;

/**
 * A tuple class
 * @param <X> the x value
 * @param <Y> the y value
 */

public class Tuple<X, Y> {
    public final X x; //the x value
    public final Y y; // the y value

    /**
     * The tuple constructor
     * @param x the x value
     * @param y the y value
     */
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a string representation of the tuple
     * @return a string
     */
    public String toString(){
        return "(" + x.toString() + ", " + y.toString() + ")";
    }
}