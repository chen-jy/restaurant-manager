package events;

import core.Order;
import core.Restaurant;
import util.Log;
import visual.gui.Manager;
import visual.gui.Server;

/**
 * A class representing a DeliverEvent.
 */
public class DeliverEvent extends Event {
    private final Order order; //an order
    private final Server server; // Server assigned to deliver the order

    /**
     * Creates a DeliverEvent.
     *
     * @param order      The Order being delivered.
     * @param server     The Server delivering the order.
     * @param restaurant a Restaurant.
     */
    public DeliverEvent(Order order, Server server, Restaurant restaurant) {
        super(restaurant);
        this.order = order;
        this.server = server;
        this.server.setOrder(order);
    }

    /**
     * Executes a DeliverEvent.
     */
    @Override
    public void execute() {
        if (!order.isCooked()) {
            System.err.println("Order needs to be cooked before delivered");
            System.exit(1);
        }
        if (this.order.getProgress() == Order.ORDER_CANCELLED) {
            System.err.println(String.format("Order %s was cancelled, cannot complete event", order));
            return;
        }

        order.setProgress(Order.ORDER_DELIVERED);
        Log.logID(server, server.getId(), String.format("Order %s delivered to Table (%d)", order,
                order.getTable().getTableNumber()));

        restaurant.getAllEmployees("Manager").forEach(manager -> {
            ((Manager) manager).refreshOrders();
            ((Manager) manager).refreshView();
        });

    }
}
