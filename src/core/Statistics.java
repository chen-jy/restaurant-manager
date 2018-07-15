package core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import visual.gui.Manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Keeps track of most ordered dishes and most used ingredients in a Restaurant.
 */
public class Statistics {
	/**
	 * All the ingredients in a restaurant.
	 */
	private ArrayList<Ingredient> ingredients;
	/**
	 * A list of Managers.
	 */
	private ArrayList<Manager> managers;
	/**
	 * The menu.
	 */
	private Menu menuItems;
	/**
	 * An IngredientManager.
	 */
	private IngredientManager ingredientManager;
	/**
	 * The ingredient backup file.
	 */
	private final String BACKUP_I = "resources/data/ingredient_stat.json";
	/**
	 * The Menu backup file.
	 */
	private final String BACKUP_M = "resources/data/menu_stat.json";

	/**
	 * Creates a Statistics object
	 *
	 * @param menu        a Menu of all the MenuItems
	 * @param ingredients a list of all the Ingredients
	 */
	public Statistics(Menu menu, ArrayList<Ingredient> ingredients, IngredientManager ingredientManager) {
		this.menuItems = menu;
		this.ingredients = ingredients;
		this.ingredientManager = ingredientManager;
		this.managers = new ArrayList<>();

		File backupIngredient = new File(BACKUP_I);
		File backupMenu = new File(BACKUP_M);
		try {
			if (!backupIngredient.createNewFile()) {
				restoreIngredient(backupIngredient);
			}
			if (!backupMenu.createNewFile()) {
				restoreMenu(backupMenu);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		backup();
		ingredientManager.setStatistics(this);

	}

	/**
	 * Add manager to keep updated on statistics
	 *
	 * @param manager manager to update
	 */
	public void addManager(Manager manager) {
		this.managers.add(manager);
	}

	/**
	 * Stop updating manager on statistics
	 *
	 * @param manager a Manager.
	 */
	public void removeManager(Manager manager) {
		this.managers.remove(manager);
	}

	/**
	 * Prints each ingredient and the current usage of each ingredient.
	 */
	public ArrayList<Ingredient> getIngredients() {
		ingredients.sort(Ingredient::compareTo);
		return ingredients;
	}

	/**
	 * Prints each MenuItem and the current usage of each MenuItem.
	 */
	public ArrayList<MenuItem> getMenuItems() {
		menuItems.sort(MenuItem::compareTo);
		return menuItems;
	}

	/**
	 * Updates the statistics
	 *
	 * @param order order to account for in the statistics
	 */
	public void update(Order order) {
		order.getItem().use();

		order.getAllIngredients().forEach(Ingredient::updateUsage);
		managers.forEach(manager -> {
			manager.updateMenuStat();
			manager.updateIngredientStat();
		});
		backup();
	}

	/**
	 * Updates the each Manager of a restaurant's inventory.
	 */
	public void updateManagerInventory() {
		managers.forEach(Manager::updateInventory);
	}

	/**
	 * Backups the statistics, i.e. ingredient usage and menu item total orders, of the restaurant.
	 */
    private void backup() {
		JSONArray data = new JSONArray();
		for (Ingredient item : ingredients) {
			JSONObject obj = new JSONObject();
			obj.put("displayName", item.getDisplayName());
			obj.put("usage", item.getUsage());
			data.add(obj);
		}

		try (FileWriter file = new FileWriter(BACKUP_I)) {
			file.write(data.toJSONString());
		} catch (Exception ignored) {
		}


		data.clear();
		for (MenuItem item : menuItems) {
			JSONObject obj = new JSONObject();
			obj.put("name", item.getName());
			obj.put("usage", item.getUsage());
			data.add(obj);
		}

		try (FileWriter file = new FileWriter(BACKUP_M, false)) {
			file.write(data.toJSONString());
		} catch (Exception ignored) {
		}
	}

	/**
	 * Restore ingredient statistics from backup
	 *
	 * @param file backup to restore from
	 */
	private void restoreIngredient(File file) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(file));
			JSONArray data = (JSONArray) obj;

			for (Object item : data) {
				JSONObject ingredientObject = (JSONObject) item;

				String displayName = (String) ingredientObject.get("displayName");
				int usage = ((Long) ingredientObject.get("usage")).intValue();
				ingredientManager.getIngredient(displayName).setUsage(usage);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restore menu statistics from backup
	 *
	 * @param file backup to restore from
	 */
	private void restoreMenu(File file) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(file));
			JSONArray data = (JSONArray) obj;

			for (Object item : data) {
				JSONObject ingredientObject = (JSONObject) item;

				String name = (String) ingredientObject.get("name");
				int usage = ((Long) ingredientObject.get("usage")).intValue();
				menuItems.get(name).setUsage(usage);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
