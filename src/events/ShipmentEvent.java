package events;

import core.Ingredient;
import core.Restaurant;
import util.Log;
import visual.gui.Employee;
import visual.gui.Manager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a Shipment Event.
 */
public class ShipmentEvent extends Event {
    private HashMap<Ingredient, Integer> ingredients;
    private Employee employee;

    /**
     * Creates a Shipment Event.
     *
     * @param ingredients the Ingredients in the shipment.
     * @param res         A Restaurant.
     */
    public ShipmentEvent(HashMap<Ingredient, Integer> ingredients, Restaurant res, Employee employee) {
        super(res);
        this.ingredients = ingredients;
        this.employee = employee;
    }

    /**
     * Executes a Shipment Event.
     */
    @Override
    public void execute() {

        this.ingredients.forEach((k, v) -> {
            ArrayList<Employee> employees = restaurant.getAllEmployees();

            k.addAmount(v);
            Log.log(employee, String.format("Shipment (%s - %d) received by %s", k.toString(), v, employee.toString()));
            employees.forEach(employee -> {
                if (employee instanceof Manager) {
                    Manager manager = (Manager) employee;
                    manager.addArrival(k, v);
                    manager.updateInventory();
                    restaurant.getIngredientManager().backup();

                }
            });
            restaurant.checkThresholds();

        });
    }
}
