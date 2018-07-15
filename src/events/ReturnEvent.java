package events;

import core.Order;
import core.Restaurant;

/**
 * Represents a Return Event.
 */
public class ReturnEvent extends Event {
    private final Order order;// an order
    private final String message; // The reason for returning the order

    /**
     * Creates a Return Event.
     *
     * @param restaurant A Restaurant.
     * @param order      an Order to be returned.
     * @param message    The reason for return.
     */
    public ReturnEvent(Restaurant restaurant, Order order, String message) {
        super(restaurant);
        this.order = order;
        this.message = message;
    }

    /**
     * Returns an Order.
     *
     * @return an Order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Executes a Return Event.
     */
    @Override
    public void execute() {
        if (!order.isDelivered()) {
            return;
        }

        // First, cancel the order
        new CancelEvent(restaurant, order, CancelEvent.REASON.CUSTOMER_RETURNED, message).execute();
        // Now make a new order
        Order newOrder = order.cloneNew();
        new OrderEvent(newOrder, newOrder.getServer(), restaurant).execute();
    }
}
