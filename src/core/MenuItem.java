package core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a dish on a menu.
 */
public class MenuItem implements Comparable<MenuItem> {
	private String name; // Serves as both reference (system) and display name
	private HashMap<Ingredient, Integer> baseIngredients;// the base ingredients for a menu item
	private double basePrice;// the base price
	private int usage; // The number of times the item was ordered (for stats)


	/**
	 * Creates a MenuItem with tags.
	 *
	 * @param name            name of the dish.
	 * @param basePrice       price of the dish without any additions.
	 * @param baseIngredients ingredients required to prepare the dish.
	 * @param tags            tags describing the dish.
	 */
	MenuItem(String name, double basePrice, HashMap<Ingredient, Integer> baseIngredients, ArrayList<String> tags) {
		this.basePrice = basePrice;
		this.name = name;
		this.baseIngredients = baseIngredients;
		this.usage = 0;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	/**
	 * Returns the price of the item without any additions.
	 *
	 * @return base price of an item.
	 */
	public double getBasePrice() {
		return basePrice;
	}

	/**
	 * Returns the name of the dish.
	 *
	 * @return name of the dish.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the list of ingredients to prepare an item.
	 *
	 * @return list of Ingredients to prepare an Item.
	 */
	public HashMap<Ingredient, Integer> getAllIngredients() {
		return baseIngredients;
	}


	/**
	 * Increases the usage of the MenuItem by 1.
	 */
	public synchronized void use() {
		usage++;
	}

	/**
	 * Returns the usage of the MenuItem.
	 *
	 * @return a int representing the usage of the MenuItem.
	 */
	public int getUsage() {
		return usage;
	}

	/**
	 * Compares the Usage of this instance of MenuItem with another MenuItem.
	 *
	 * @param o a MenuItem.
	 * @return greater than 0 if this instance's usage is greater than another MenuItem's usage.
	 */
	@Override
	public int compareTo(MenuItem o) {
		return o.usage - this.usage;
	}

	/**
	 * Returns a String representation of a MenuItem
	 *
	 * @return a the name of the MenuItem
	 */
	@Override
	public String toString() {
		return this.getName();
	}
}
