package events;

import core.Order;
import core.Restaurant;
import core.Table;
import util.Alert;
import visual.gui.Cook;
import visual.gui.Employee;
import visual.gui.Manager;
import visual.gui.Server;

import java.util.ArrayList;

/**
 * A class representing a CancelEvent.
 */
public class CancelEvent extends Event {
    private final Order order; // The order being cancelled
    private final REASON reason; // Reason for cancellation
    private final String description; // A more descriptive reason

    /**
     * A String representing a REASON for a canceled Event.
     */
    public enum REASON {
        CUSTOMER_CANCELLED("Customer cancelled the order"),
        CUSTOMER_RETURNED("Customer returned the order");

        final String reason;

        /**
         * Creates a Reason.
         *
         * @param reason a String represents a Reason.
         */
        REASON(String reason) {
            this.reason = reason;
        }

        /**
         * Returns a String representation of a REASON.
         *
         * @return a String representing a REASON.
         */
        @Override
        public String toString() {
            return reason;
        }
    }

    /**
     * Creates a CancelEvent.
     *
     * @param restaurant  A Restaurant.
     * @param order       A order being cancelled.
     * @param reason      A REASON for cancellation.
     * @param description A more description reason for cancellation.
     */
    public CancelEvent(Restaurant restaurant, Order order, REASON reason, String description) {
        super(restaurant);
        this.order = order;
        this.reason = reason;
        this.description = description;
    }

    /**
     * Returns a Order for a CancelEvent.
     *
     * @return a Order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Executes a CancelEvent.
     */
    @Override
    public void execute() {
        boolean isDelivered = order.isDelivered();
        order.setProgress(Order.ORDER_CANCELLED);
        Cook cook = order.getCook();

        order.getServer().log(String.format("Order %s cancelled: %s", order, getReason()));


        if (cook != null && !isDelivered) {
            new Alert("Your current order is being cancelled", order.getServer(), order.getCook(), false).execute();

            cook.setAvailable(true);
            cook.log(String.format("Order %s cancelled, Reason: %s%s", order, getReason(), this.description != null ?
                    ", " + description : ""));
            order.setCook(null);
        } else {
            ArrayList<Employee> employees = restaurant.getAllEmployees("Cook");
            employees.forEach(employee -> ((Cook) employee).refreshAssigned());

        }


        Table table = order.getTable();
        if (table.hasOrder(order)) {
            table.removeOrder(order);
        }

        order.setProgress(Order.ORDER_CANCELLED);

        restaurant.getAllEmployees("Manager").forEach(employee -> ((Manager) employee).refreshOrders());
        restaurant.getAllEmployees("Server").forEach(employee -> {
            Server server = ((Server) employee);
            server.refreshOrders();
            server.refreshView();
        });

        Cook.unassign(order);

        refresh();
    }

    /**
     * Returns a REASON for this CancelEvent.
     *
     * @return a REASON.
     */
    private REASON getReason() {
        return reason;
    }

    /**
     * Refreshes the GUI after a Cook Event.
     */
    private void refresh() {
        ArrayList<Employee> employees = restaurant.getAllEmployees("Cook");
        employees.forEach(employee -> ((Cook) employee).refreshAssigned());
        ArrayList<Employee> managers = restaurant.getAllEmployees("Manager");
        managers.forEach(manager -> ((Manager) manager).refreshView());
        order.getServer().refreshView();
        order.getServer().refreshOrders();
    }
}
