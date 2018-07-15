package core;

/**
 * Represents individual ingredients with the updateUsage of enums (bundled constants).
 */
public class Ingredient implements Comparable<Ingredient> {
	private String displayName; // Name displayed to customers
	private double additionPrice; // Price as an extra addition
	private int amount; // Current stock
	private int reserved = 0; //The amount of the ingredient that is reserved for pending orders
	private int threshold; // Minimum threshold to re-order
	private boolean addable; // If it can be added as extra to orders
	private int usage; // Amount used from program start

	/**
	 * Creates a new ingredient with the specified attributes.
	 *
	 * @param displayName   the ingredient's display name.
	 * @param additionPrice the price per unit to add to a menu item.
	 * @param amount        the amount of each ingredient in stock
	 * @param threshold     the minimum amount of a ingredient before it needs to be reordered
	 * @param addable       if the ingredient can be added to a menu item or not
	 */
	public Ingredient(String displayName, double additionPrice, int amount, int threshold, boolean addable) {
		// Ensure that the ingredient has no null or invalid fields
		if (displayName == null) {
			System.err.println("Ingredients must have display names.");
			return;
		}
		if (additionPrice < 0) {
			additionPrice = 0d;
		}
		if (amount < 0) {
			amount = 0;
		}

		this.displayName = displayName;
		this.additionPrice = additionPrice;
		this.amount = amount;
		this.threshold = threshold;
		this.addable = addable;
		this.usage = 0;
	}

	/**
	 * Updates the amount of an ingredient.
	 *
	 * @param amount the amount being removed from an ingredient's current amount.
	 */
	public void update(int amount) {
		this.amount -= amount;
	}

	/**
	 * Sets the Ingredient usage amount.
	 *
	 * @param usage the new usage amount.
	 */
	public void setUsage(int usage) {
		this.usage = usage;
	}

	/**
	 * Gets the Display Name of the ingredient.
	 *
	 * @return a string representation of the ingredient's display name.
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Returns the price of adding the ingredient as an addition to a menu order.
	 *
	 * @return a int representing the price of adding the item to a menu order.
	 */
	public double getAdditionPrice() {
		return additionPrice;
	}

	/**
	 * Returns the the ingredient's threshold.
	 *
	 * @return a int representing the ingredient's threshold.
	 */
	public int getThreshold() {
		return threshold;
	}

	/**
	 * Returns the amount of ingredient in stock.
	 *
	 * @return the amount of an ingredient in stock.
	 */
	public int getAmount() {
		return amount;
	}


	/**
	 * Increases the amount of the ingredient in stock.
	 *
	 * @param amount the amount to increase the ingredient's current stock by.
	 */
	public void addAmount(int amount) {
		this.amount += amount;
	}

	/**
	 * Returns if an ingredient can be added to a menu order.
	 * <p>
	 * Currently unused since all events are done through events.txt.
	 *
	 * @return true if an ingredient can be added to an order.
	 */
	public boolean isAddable() {
		return addable;
	}

	/**
	 * Uses the ingredient.
	 *
	 * @param usage updateUsage ingredient that many times
	 */
	public synchronized void updateUsage(int usage) {
		this.usage += usage;
	}

	/**
	 * Updates the amount of an ingredient.
	 *
	 * @param amount the amount being removed from an ingredient's current amount.
	 */
	public synchronized void use(int amount) {
		this.amount -= amount;
	}


	/**
	 * Reserves a set amount of ingredient for a pending order.
	 *
	 * @param amount the amount of an ingredient to be reserved.
	 */
	public synchronized void reserve(int amount) {
		this.reserved += amount;
		this.amount -= amount;
	}

	/**
	 * Returns the amount of an ingredient that is reserved.
	 *
	 * @return an int representing reserved amount.
	 */
	public int getReserved() {
		return reserved;
	}

	/**
	 * Gets the number of times ingredient was used
	 *
	 * @return the number of times ingredient was used
	 */
	public int getUsage() {
		return usage;
	}

	/**
	 * Compares two ingredients, used for sorting.
	 *
	 * @param o An ingredient.
	 * @return greater than 0 if this instance's usage is greater than another ingredient's usage.
	 */
	@Override
	public int compareTo(Ingredient o) {
		return o.usage - this.usage;
	}

	/**
	 * Returns if this instance of ingredient is equal to another ingredient.
	 *
	 * @param other An ingredient.
	 * @return true if both ingredients are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Ingredient && ((Ingredient) other).getDisplayName().equals(this.getDisplayName());
	}

	/**
	 * Returns a String representation of the ingredient.
	 *
	 * @return a String containing the display name of the ingredient.
	 */
	@Override
	public String toString() {
		return this.getDisplayName();
	}

	/**
	 * Return a detailed String representation of the ingredient.
	 *
	 * @return a String containing the display name and amount.
	 */
	public String toDetailedString() {
		return String.format("%-12s%5d", this.displayName, this.amount);
	}
}
