package core;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.Log;
import util.PaymentJsonWriter;
import visual.gui.Cook;
import visual.gui.Employee;
import visual.gui.Manager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class representing a restaurant.
 */
public class Restaurant {
    private HashMap<String, ArrayList<Employee>> employees;//The employees of the restaurant
    private IngredientManager ingredientManager; //An IngredientManager
    private String name; // the Restaurant name
    private Menu menu; //the menu
    private Statistics statistics; // the Restaurant statistics

    private ArrayList<Order> allOrders; // All of the restaurant's orders
    private ArrayList<Table> tables; //A list of the restaurant's tables

    /**
     * Creates a restaurant.
     *
     * @param settings    A file of settings
     * @param ingredients The ingredients file
     * @param requests    The file to add ingredient reorders to
     * @param menu        the file containing the menu
     * @param payments the file containing payment history
     */
    public Restaurant(File settings, File ingredients, File requests, File menu, File payments) {
        employees = new HashMap<>();
        this.allOrders = new ArrayList<>();
        tables = new ArrayList<>();
        ingredientManager = new IngredientManager(ingredients, requests, this);
        this.menu = new Menu(menu, this);


        try {
            parseSettings(settings);
            PaymentJsonWriter.parsePayments(payments);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        this.statistics = new Statistics(this.menu, ingredientManager.getIngredients(), ingredientManager);

        this.checkThresholds();
    }

    /**
     * Returns a list of all the orders in a restaurant.
     * @return a list of orders
     */
    public ArrayList<Order> getAllOrders() {
        return allOrders;
    }



    /** Add an order to keep track of
     * @param order order to add
     */
    public void addOrder(Order order){
        allOrders.add(order);
    }

    /**
     * Checks if there are enough ingredients to make an order.
     *
     * @param order the order to check.
     * @return true if there are enough ingredients to make the order; false otherwise.
     */
    public boolean checkIngredients(Order order) {
        Map<Ingredient, Integer> ingredients = order.getAllIngredients();
        for (Map.Entry<Ingredient, Integer> entry : ingredients.entrySet()) {
            Optional<Ingredient> ing = getInventoryList().stream().filter(a -> a.equals(entry.getKey())).findFirst();

            if (entry.getKey().getAmount() < entry.getValue()) {
				return false;
			}
        }

        statistics.update(order);
        return true;
    }

    /**
     * Updates the ingredient inventory.
     *
     * @param order deduct based on this order.
     */
    public void updateInventory(Order order) {
        order.getAllIngredients().forEach(Ingredient::update);
    }

    /**
     * Forces the kitchen to check the thresholds.
     */
    public void checkThresholds() {
        this.getInventoryList().forEach(a -> {
            // Update external file
            if (a.getAmount() < a.getThreshold()) {
                if (!ingredientManager.isInReorder(a))
                    Log.log(this, String.format("Now requesting %s units of %s",
                            IngredientManager.DEFAULT_ORDER_AMOUNT, a.getDisplayName()));
                ingredientManager.reorderIngredient(a);
            } else {
                ingredientManager.removeReorderIngredient(a);
            }

            ingredientManager.updateRequestsFile();
        });
    }

    /**
     * Gets the table with the specified table number, or null, if it doesn't exist.
     *
     * @param tableNumber the number of the table to be returned.
     * @return the table with the specified table number.
     */
    public Table getTable(int tableNumber) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }

        return null;
    }

    /**
     * Returns the number of tables at a restaurant..
     * @return a number of tables.
     */

    public ArrayList<Table> getTables() {
        return this.tables;
    }

    /**
     * Sends an order to the cooks
     *
     * @param order the order to be assigned.
     */
    public void sendOrder(Order order) {
        Cook.assignOrder(order);
        ArrayList<Employee> allCooks = this.employees.get("Cook");

        if(allCooks == null)return;
        allCooks.forEach(employee -> {
            Cook cook = (Cook) employee;
            if (cook.isAvailable()) cook.refreshAssigned();

        });

    }

    /**
     * Parses a file to set the program settings.
     *
     * @param settings a file containing settings
     * @throws IOException    Unable to read settings file
     * @throws ParseException Settings malformed json file
     */
    private void parseSettings(File settings) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(settings));
        JSONObject data = (JSONObject) obj;
        name = (String) data.get("name");
        ArrayList<Long> tableSizes = (ArrayList<Long>) data.get("table_layout");

        int tableNum = 1;

        for (int size = 0; size < tableSizes.size(); size++) {
            for (long i = 0; i < tableSizes.get(size); i++) {
                tables.add(new Table(tableNum++, size + 1));
            }
        }
    }

    /**
     * Logs an employee into the program.
     *
     * @param employee an employee.
     */
    public synchronized void login(Employee employee) {
        if (isLoggedIn(employee)) {
            return;
        }
        if (!employees.containsKey(employee.getType())) {
            employees.put(employee.getType(), new ArrayList<>());
        }

        employees.get(employee.getType()).add(employee);
        if (employee instanceof Manager) statistics.addManager((Manager) employee);

        Log.log(toString(), String.format("%s logged in", employee.toString()));
    }

    /**
     * Logout an employee from the program
     *
     * @param employee an employee
     */
    public synchronized void logout(Employee employee) {
        if (employees.containsKey(employee.getType())) {
            employees.get(employee.getType()).remove(employee);
        }
        if (employee instanceof Manager) {
            statistics.removeManager((Manager) employee);
        }

        Log.log(toString(), String.format("%s logged out", employee.toString()));
    }

    /**
     * returns if an Employee is logged into the system.
     *
     * @param employee an employee
     * @return true if an employee is logged, false otherwise
     */
    public boolean isLoggedIn(Employee employee) {
        return this.getAllEmployees().stream().anyMatch(a -> a.equals(employee));
    }

    /**
     * Returns the name of restaurant.
     *
     * @return the name of the restaurant.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the IngredientManager
     *
     * @return an IngredientManager
     */

    public IngredientManager getIngredientManager() {
        return ingredientManager;
    }

    /**
     * Returns a String representation of the Restaurant.
     *
     * @return a String.
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Returns the menu.
     *
     * @return a menu.
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Return an Employee.
     *
     * @param type     the type of employee
     * @param username the username of the employee
     * @return an employee of teh given type and username, null otherwise.
     */
    public Employee getEmployee(String type, String username) {
        ArrayList<Employee> employees = this.employees.get(type);
        if (employees != null) {
            return employees.stream().filter(a ->
                    a.getUser().toLowerCase().equals(username.toLowerCase())).findFirst().orElse(null);
        } else return null;
    }

    /**
     * Returns a map of employees.
     *
     * @return a map.
     */
    public HashMap<String, ArrayList<Employee>> getEmployeeMap() {
        return employees;
    }

    /**
     * Returns a list of all employees.
     *
     * @return a list.
     */
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        this.employees.forEach((key, value) -> employees.addAll(value));
        return employees;
    }

	/**
	 * Returns a list of all employees.
	 *
	 * Returns a list of all employee of specific type
	 * @param type type of employees
	 * @return a list.
	 */
	public ArrayList<Employee> getAllEmployees(String type) {
	    if(employees.get(type) == null) return new ArrayList<>();
		return employees.get(type);
	}

    /**
     * Returns the statistics for a restaurant.
     * @return statistics
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Gets a mapping of ingredients and their amounts for the kitchen's inventory.
     *
     * @return a map of the kitchen's inventory.
     */
    public ArrayList<Ingredient> getInventoryList() {
        return ingredientManager.getIngredients();
    }

}
