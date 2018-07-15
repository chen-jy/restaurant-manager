package events;

import core.Order;
import core.Restaurant;
import util.Log;
import visual.gui.Cook;
import visual.gui.Employee;
import visual.gui.Manager;

import java.util.ArrayList;

/**
 * A class representing a ReceiveEvent.
 */
public class ReceiveEvent extends Event {
    private Order order; // an order
    private Cook cook; // The cook who receives the order

    /**
     * Creates a ReceiveEvent.
     *
     * @param order      an Order to be received.
     * @param cook       the Cook receiving an order.
     * @param restaurant a Restaurant.
     */
    public ReceiveEvent(Order order, Cook cook, Restaurant restaurant) {
        super(restaurant);
        this.order = order;
        this.cook = cook;
    }

    /**
     * Returns the Cook for the ReceiveEvent.
     *
     * @return a Cook.
     */
    public Cook getCook() {
        return cook;
    }

    /**
     * Return this ReceiveEvent's order.
     *
     * @return a Order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Executes a ReceiveEvent.
     */
    @Override
    public void execute() {

        ArrayList<Employee> employees = restaurant.getAllEmployees("Cook");
        cook.setAvailable(false);
        employees.forEach(employee -> {
            ((Cook) employee).refreshAssigned();
            ((Cook) employee).refreshView();
        });


        if (this.order.getProgress() == Order.ORDER_CANCELLED) {
            cook.logf("Order %s was cancelled, cannot complete event\n", order);
            return;
        }

        if (this.cook.getId() != order.getCook().getId()) {
            System.err.printf("Cook %d could not receive order %d, Reason: order was dispatched to cook %d\n",
                    this.cook.getId(), order.getOrderNumber(), order.getCook().getId());
            System.exit(1);
        }

        this.order.setProgress(Order.ORDER_RECEIVED);
        Log.logID(cook, cook.getId(), String.format("Order %s received and ready to cook", order));
        ArrayList<Employee> managers = restaurant.getAllEmployees("Manager");
        managers.forEach(manager -> ((Manager) manager).refreshView());
        order.getServer().refreshView();
    }
}
