package core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Menu of dishes. Can return a Menu with items of specific categories.
 */
public class Menu extends ArrayList<MenuItem> {
    private Restaurant restaurant;// The Restaurant the menu belongs to

    /**
     * Creates a menu from a file.
     *
     * @param file the Json the Menu is built from.
     * @param res  a Restaurant with this Menu.
     */
    public Menu(File file, Restaurant res) {
        super();
        this.restaurant = res;
        parseMenu(file);
    }

    /**
     * Parses a menu from a json file to add ingredients to a the menu.
     *
     * @param file a json file containing menu items. Refer to resources/menu.json for format.
     */
    private void parseMenu(File file) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONArray data = (JSONArray) obj;

            for (Object item : data) {
                JSONObject dish = (JSONObject) item;
                String name = (String) dish.get("name");
                double price = (double) dish.get("price");

				Object[] tags = ((JSONArray) dish.get("tags")).toArray();
				ArrayList<String> tagsList = Arrays.stream(tags).map(object ->
                        Objects.toString(object, null)).collect(Collectors.toCollection(ArrayList::new));

                JSONObject ingredients = ((JSONObject) dish.get("ingredients"));

                HashMap<Ingredient, Integer> ingredientsMap = new HashMap<>();

                ingredients.keySet().stream().forEach(key -> {
                    String ingredient = (String) key;
                    int amount = ((Long) ingredients.get(key)).intValue();
                    Ingredient ing = restaurant.getIngredientManager().getIngredient(ingredient);
                    ingredientsMap.put(ing, amount);
                });

                add(new MenuItem(name, price, ingredientsMap, tagsList));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an item from a menu.
     *
     * @param item the name of the menu item
     * @return a menu item if it exists, or a string otherwise.
     */
    public MenuItem get(String item) {
        Optional<MenuItem> menuItem = stream().filter(a -> a.getName().equals(item)).findFirst();
        if (!menuItem.isPresent()) {
            System.err.println("No such item " + item);
            System.err.println("Please select from the following items:");
            forEach(a -> System.err.println(a.getName()));
            return null;
        }
        return menuItem.get();
    }

    /**
     * Sets the Restaurant of the menu.
     *
     * @param restaurant a Restaurant.
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
