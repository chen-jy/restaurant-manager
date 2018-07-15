package core;

import events.GetBillEvent;
import visual.gui.Server;

import java.util.ArrayList;

/**
 * Represents a table at a restaurant.
 */
public class Table implements Comparable {
    private final int tableNumber; // the table number
    /**
     * The first index refers to the seat number and the second for that seat's orders
     */

    private int tableCapacity; // the total capacity of a table

    private Server server; //the table's server


    /**
     * The first index refers to the seat number and the second for that seat's orders
     */
    private final ArrayList<ArrayList<Order>> seatOrders;

    /**
     * Creates a table with a particular table number and number of seats.
     *
     * @param tableNumber   the table's identifying number.
     * @param numberOfSeats the number of seats at a Table.
     */
    public Table(int tableNumber, int numberOfSeats) {
        this.tableNumber = tableNumber;
        this.tableCapacity = numberOfSeats;

        // Create empty lists of orders for each seat at the table
        seatOrders = new ArrayList<>();
        for (int i = 0; i < numberOfSeats; i++) {
            seatOrders.add(new ArrayList<>());
        }
    }

    /**
     * Returns the number of seats at a table.
     *
     * @return the number of people at a table
     */
    public int getTableCapacity() {
        return tableCapacity;
    }

    /**
     * Sets the table's server.
     *
     * @param server a server
     */

    public void setServer(Server server) {
        if (this.server == null) {
            this.server = server;
        }

    }

    /**
     * Return a table's server.
     *
     * @return a server.
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * Gets this table's unique number.
     *
     * @return the tableNumber.
     */
    public int getTableNumber() {
        return tableNumber;
    }

    /**
     * Clears the orders of a Table.
     */
    public void clearOrders() {
        seatOrders.forEach(ArrayList::clear);
    }

    /**
     * Removes all orders from the table associated with a particular seat number.
     *
     * @param seat the seat number whose orders should be removed.
     */
    public void clearOrders(int seat) {
        seatOrders.get(seat - 1).clear();
    }

    /**
     * Updates the table's bill with a new order.
     *
     * @param order the order that is being added to the bill.
     */
    public void updateBill(Order order) {
        seatOrders.get(order.getSeatNumber() - 1).add(order);
    }

    /**
     * Removes an Order from a table's orders.
     *
     * @param order the order that is being removed from the bill.
     */
    public void removeOrder(Order order) {
        ArrayList<Order> orders = seatOrders.get(order.getSeatNumber() - 1);
        if (orders.contains(order)) {
            orders.remove(order);
        }
    }

    /**
     * Checks if a table has an order.
     *
     * @param order an order.
     * @return true if a table has the order, false otherwise.
     */
    public boolean hasOrder(Order order) {
        return seatOrders.get(order.getSeatNumber() - 1).contains(order);
    }

    /**
     * Gets a list of all the orders at a table.
     *
     * @return a list containing all of the table's orders.
     */
    public ArrayList<Order> getOrders() {
        ArrayList<Order> all = new ArrayList<>();
        this.seatOrders.forEach(all::addAll);
        return all;
    }

    /**
     * Checks if a table as any active orders
     *
     * @return a boolean if this table has active orders, false otherwise
     */
    public boolean hasActiveOrders() {
        // exists an x such that not P(x)
        // not forall x, P(x)
        return !this.getOrders().stream().allMatch(Order::isDelivered);
    }

    /**
     * Returns if this table us currently occupied.
     *
     * @return true if this table is occupied, false otherwise.
     */
    public boolean isOccupied() {
        return this.seatOrders.stream().filter(a -> !a.isEmpty()).count() > 0;
    }

    /**
     * Gets a string representation of this table, used for logging
     *
     * @return "Table (id)"
     */
    @Override
    public String toString() {
        return "Table (" + this.getTableNumber() + ")";
    }

    /**
     * Returns the total amount of the bill including tax and tip.
     *
     * @return the total bill amount.
     */
    public double getTotalBillAmount() {
        double basePrice = getBasePrice();
        return basePrice + getTipAmount(basePrice) + getTaxAmount(basePrice);
    }

    /**
     * Returns  the tip amount when a table is greater than or equal to 8.
     *
     * @param base the base amount to be paid
     * @return the amount of tip that needs to be paid,
     */
    public double getTipAmount(double base) {
        return this.seatOrders.stream().filter(a -> !a.isEmpty()).count() >= 8 ? base * GetBillEvent.AUTOMATIC_TIP_AMOUNT : 0d;
    }

    /**
     * Returns the base price of a table's orders.
     *
     * @return the base price.
     */
    public double getBasePrice() {
        return this.getOrders().stream().mapToDouble(a -> a.isDelivered() ? a.getPrice() : 0).sum();
    }

    /**
     * Returns the base price of this seat
     *
     * @param seat the seat
     * @return the base price
     */
    public double getBasePrice(int seat) {
        if (this.tableCapacity < seat)
            return 0;

        return this.seatOrders.get(seat - 1).stream().mapToDouble(a -> a.isDelivered() ? a.getPrice() : 0).sum();

    }

    /**
     * Returns the tax amount of a table's orders.
     *
     * @param base the base amount to be paid
     * @return the tax amount
     */
    public double getTaxAmount(double base) {
        return base * GetBillEvent.TAX_AMOUNT;
    }

    /**
     * Compared this table to another table for order
     *
     * @param o a table
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater
     * than the specified object.
     */
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
