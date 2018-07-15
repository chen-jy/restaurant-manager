package events;

import core.Order;
import core.Restaurant;
import util.Log;
import visual.gui.Employee;
import visual.gui.Manager;
import visual.gui.Server;

import java.util.ArrayList;

/**
 * A class representing a OrderEvent.
 */
public class OrderEvent extends Event {
    private final Order order; //an order
    private final Server server; // The server who took the order

    /**
     * Creates an OrderEvent
     *
     * @param order      the order this event represents
     * @param server     the server who took the order
     * @param restaurant the restaurant
     */
    public OrderEvent(Order order, Server server, Restaurant restaurant) {
        super(restaurant);
        this.server = server;
        this.order = order;
    }

    /**
     * Returns this instance of OrderEvent's Order.
     *
     * @return a Order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Executes an OrderEvent.
     */
    @Override
    public void execute() {
        restaurant.sendOrder(order);
        order.setProgress(Order.ORDER_ORDERED);
        restaurant.addOrder(order);

        this.restaurant.getIngredientManager().reserve(this.order);

        Log.logID(server, server.getId(), String.format("Order %s ordered for Table %d", order,
                order.getTable().getTableNumber()));

        order.getTable().updateBill(order);

        ArrayList<Employee> employees = restaurant.getAllEmployees("Manager");
        employees.forEach(employee -> ((Manager) employee).refreshOrders());
        server.refreshOrders();
    }
}
