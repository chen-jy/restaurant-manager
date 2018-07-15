package events;

import core.Restaurant;

/**
 * An event class.
 */
abstract class Event {
	// All possible types of events

    /**
     * A restaurant.
     */
    final Restaurant restaurant;

    /**
     * The Constructor for an Event.
     *
     * @param restaurant a Restaurant.
     */
    Event(Restaurant restaurant) {
        super();
        this.restaurant = restaurant;
    }

    /**
     * Executes this event.
     */
    public abstract void execute();
}
