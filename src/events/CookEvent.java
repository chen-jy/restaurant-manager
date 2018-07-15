package events;

import core.Order;
import core.Restaurant;
import visual.gui.Cook;
import visual.gui.Employee;
import visual.gui.Manager;

import java.util.ArrayList;

/**
 * A class representing a CookEvent.
 */
public class CookEvent extends Event {
    private final Order order; // An order
    private final Cook cook; // Cook assigned to the order

    /**
     * Creates a Cook event.
     *
     * @param order      the order to be cooked.
     * @param cook       the Cook that cooks the Order.
     * @param restaurant A restaurant.
     */
    public CookEvent(Order order, Cook cook, Restaurant restaurant) {
        super(restaurant);
        this.order = order;
        this.cook = cook;
    }

    /**
     * Returns this instance of CookEvent's Cook.
     *
     * @return a Cook.
     */
    private Cook getCook() {
        return cook;
    }

    /**
     * Returns this instance of CookEvent's Order.
     *
     * @return a Order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Executes a CookEvent.
     */
    @Override
    public void execute() {
        if (!order.isReceived()) {
            cook.log("Order needs to be received before cooking");
            refresh();
            return;
        }

        if (this.order.getProgress() == Order.ORDER_CANCELLED) {
            cook.log(String.format("Order %s was cancelled, cannot complete cooking", order));
            this.restaurant.checkThresholds();
            refresh();
            return;

        }

        if (this.order.getCook().getId() != this.getCook().getId()) {
            cook.log(String.format("Cook %d could not cook order %d, Reason: order was received by cook %d\n",
                    this.cook.getId(),
                    this.order.getOrderNumber(),
                    this.order.getCook().getId()));
            refresh();
            return;
        }

        this.order.setProgress(Order.ORDER_COOKED);
        this.restaurant.updateInventory(order);
        this.restaurant.checkThresholds();

        this.cook.setAvailable(true);
        cook.log(String.format("Order %s cooked and ready to bus", order));
        order.getServer().log(String.format("Notified to deliver order %s", order));


        refresh();

    }

    /**
     * Refreshes the GUI after a Cook Event.
     */
    private void refresh() {
        this.cook.setAvailable(true);
        ArrayList<Employee> employees = restaurant.getAllEmployees("Cook");
        employees.forEach(employee -> ((Cook) employee).refreshAssigned());
        ArrayList<Employee> managers = restaurant.getAllEmployees("Manager");
        managers.forEach(manager -> ((Manager) manager).refreshView());
        order.getServer().refreshView();
        order.getServer().refreshOrders();
    }
}
