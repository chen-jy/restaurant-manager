package core;

import visual.gui.Cook;
import visual.gui.Server;

import java.util.HashMap;

/**
 * Represents an order, containing one item.
 */
public class Order implements Comparable {
    private MenuItem item; // The order's item
    private HashMap<Ingredient, Integer> additions;// A list of added ingredients
    private HashMap<Ingredient, Integer> subtractions; // A list of subtracted ingredients

    private Table table; // Table that the order belongs to
    private int seatNumber; // Seat number at that table
    private Cook cook; // Assigned cook
    private final Server server; // Assigned server

    private int state; // See below for possible states
    private static int orderNumberCounter = 10000; // the order number tracker
    private int orderNumber; // the order number


    // Represents different states of an order (to avoid hardcoding).
    public static final int ORDER_ORDERED = 0;
    public static final int ORDER_RECEIVED = 2;
    public static final int ORDER_COOKED = 3;
    public static final int ORDER_DELIVERED = 4;
    public static final int ORDER_CANCELLED = 5;

    /**
     * Creates an order with specific information.
     *
     * @param item         the menu item being ordered.
     * @param additions    a list of additional ingredients to be added to the item.
     * @param subtractions a list of ingredients that should be removed from the item.
     * @param table        the table that the order corresponds to.
     * @param seatNumber   the seat number a Order corresponds to.
     * @param server       a server taking the Order.
     */
    public Order(MenuItem item,
                 HashMap<Ingredient, Integer> additions,
                 HashMap<Ingredient, Integer> subtractions,
                 Table table,
                 int seatNumber,
                 Server server) {
        this.item = item;
        this.additions = additions;
        this.subtractions = subtractions;
        this.table = table;
        this.seatNumber = seatNumber;
        this.orderNumber = orderNumberCounter++;
        this.server = server;
        table.setServer(server);
        state = ORDER_ORDERED;
    }

    /**
     * Creates a new order based on a previous order when a return order event is executed.
     * @return a new order.
     */
    public Order cloneNew() {
        return new Order(this.item, this.additions, this.subtractions, this.table, this.seatNumber, this.server);
    }

    /**
     * Gets the price of the order.
     *
     * @return the price of the order.
     */
    public double getPrice() {

        return item.getBasePrice() +
                this.additions.entrySet().stream().mapToDouble((entry) ->
                        entry.getKey().getAdditionPrice() * entry.getValue()
                ).sum();
    }

    /**
     * Gets a list of all the ingredients used in the order.
     *
     * @return a list of all the ingredients used in the order.
     */
    public HashMap<Ingredient, Integer> getAllIngredients() {
        HashMap<Ingredient, Integer> result = new HashMap<>();

        // add amounts from all ingredients to results map
        item.getAllIngredients().forEach(result::put);
        // add amounts from additions to results map
        additions.forEach((k1, v1) -> result.compute(k1, (k, v) -> (v != null ? v : 0) + additions.get(k)));
        // subtract amounts from subtractions from results map
        subtractions.forEach((k1, v1) -> result.compute(k1, (k, v) -> (v != null ? v : 0) - subtractions.get(k)));

        return result;
    }

    /**
     * Gets the table that the order corresponds to.
     *
     * @return the table that requested the order.
     */
    public Table getTable() {
        return table;
    }

    /**
     * Gets the seat number this order is for.
     *
     * @return the seat number
     */
    public int getSeatNumber() {
        return seatNumber;
    }

    /**
     * Gets the cook that is responsible for the order.
     *
     * @return the cook responsible for the order.
     */
    public Cook getCook() {
        return this.cook;
    }

    /**
     * Sets the Cook for the Order.
     *
     * @param cook the cook for the order.
     */

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    /**
     * Returns the item that this order corresponds to
     *
     * @return the order's item.
     */
    public MenuItem getItem() {
        return item;
    }

    /**
     * Returns the Order number.
     *
     * @return the order number.
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * Determines if the order has been received by a cook.
     *
     * @return true if the order has been received; false otherwise.
     */
    public boolean isReceived() {
        return state >= ORDER_RECEIVED;
    }

    /**
     * Determines if the order has been delivered to its table.
     *
     * @return true if the order has been delivered; false otherwise.
     */
    public boolean isDelivered() {
        return state >= ORDER_DELIVERED;
    }

    /**
     * Determines if the order has been returned to the kitchen.
     *
     * @return true if the order has been returned; false otherwise.
     */
    public boolean isCancelled() {
        return state >= ORDER_CANCELLED;
    }

    /**
     * Determines if the order has been cooked.
     *
     * @return true is the order is cooked; false otherwise.
     */
    public boolean isCooked() {
        return state >= ORDER_COOKED;
    }

    /**
     * Sets the order's progress to a specified state.
     *
     * @param progress the new state of the order.
     */
    public void setProgress(int progress) {
        if (progress >= ORDER_ORDERED && progress <= ORDER_CANCELLED) {
            state = progress;
        }
    }

    /**
     * Returns the progress of the Order.
     *
     * @return a int representing the progress of the Order.
     */
    public int getProgress() {
        return this.state;
    }

    /**
     * Returns the Server of the Order.
     *
     * @return a Server.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Returns  String Representation of a Order.
     *
     * @return a String Representing the Order.
     */
    @Override
    public String toString() {
        return String.format("(%s id:%d)", getItem().getName(), getOrderNumber());
    }


    /**
     * Compares this Order with another instance of order for order.
     * @param o a order
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater
     * than the specified object.
     */
    @Override
    public int compareTo(Object o) {
        return ((Order) o).getOrderNumber() - this.orderNumber;
    }
}
