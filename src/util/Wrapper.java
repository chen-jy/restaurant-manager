package util;

import java.util.Observable;

/**
 * A wrapper class, for adding to the GUI list
 *
 * @param <T> the type of this wrapper
 */
public class Wrapper<T extends Comparable> extends Observable implements Comparable<Wrapper> {
    private T value; // the data to wrap
    private int data;// additional flags
    private int variable; //the secondary flag used in addition to data, for sorting in JFX list views
    private String toString; // an optional string representation of this wrapper

    /**
     * Initializes a new Wrapper class
     *
     * @param value the data to wrap
     * @param data  additional int flags
     */
    public Wrapper(T value, int data, int variable) {
        this.value = value;
        this.data = data;
        this.variable = variable;
        this.toString = value == null ? "null" : value.toString();
    }

    /**
     * Overrides the default toString return value
     *
     * @param value the value to override with
     */
    public void setToString(String value) {
        this.toString = value;
    }

    /**
     * Gets the wrapped data
     *
     * @return the wrapper data
     */
    public T getValue() {
        return value;
    }

    /**
     * Gets the additional data flag
     *
     * @return data
     */
    public int getData() {
        return data;
    }

    /**
     * Returns the variable.
     * @return a int
     */

    public int getVariable() {
        return variable;
    }


    /**
     *
     * @param amount
     */
    public void addVariable(int amount) {
        this.variable += amount;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets a string representation of this wrapper
     *
     * @return this String representation
     */
    @Override
    public String toString() {
        return toString;
    }

    /**
     * Compares this value to another value based on alphabetical order of toString()
     *
     * @param o the value to compare to
     * @return this comparedTo o
     */
    @Override
    public int compareTo(Wrapper o) {
        return this.getValue().toString().compareTo(o.getValue().toString());
    }
}
