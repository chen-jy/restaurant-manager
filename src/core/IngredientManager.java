package core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A manager for ingredients.
 */
public class IngredientManager {
    private final ArrayList<Ingredient> ingredients;//All the ingredients currently in inventory
    /**
     * the default amount to reorder
     */
    public static final int DEFAULT_ORDER_AMOUNT = 20;
    private final HashMap<Ingredient, Integer> reorders; // Current re-orders; used to write to requests.txt
    private final File requests; // The requests file
    private Statistics statistics; // The statistics file
    private Restaurant res; // The restaurant for this IngredientManager

    /**
     * Constructs an IngredientManager.
     *
     * @param ingredientsFile a json file to read ingredients from.
     * @param requests        a file to write re-order requests to.
     * @param res             the restaurant
     */
    public IngredientManager(File ingredientsFile, File requests, Restaurant res) {
        ingredients = new ArrayList<>();
        reorders = new HashMap<>();
        this.res = res;
        this.requests = requests;
        File backup = new File("resources/data/inventory.json");
        try {
            if (backup.createNewFile()) {
                parseIngredients(ingredientsFile);
            } else {
                parseIngredients(backup);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        backup();
    }

    /**
     * Set statistics to update the manager
     *
     * @param statistics Statistics object
     */
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }


    /**
     * Gets the list of ingredients.
     *
     * @return a list of ingredients.
     */
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Returns if an order can be ordered based on ingredient amounts present in inventory.
     *
     * @param order an order.
     * @return true if an order can be ordered, false otherwise.
     */
    private boolean canOrder(Order order) {
        this.res.checkThresholds();
        return order.getAllIngredients().entrySet().stream().allMatch((entry) ->
                entry.getKey().getAmount() >= entry.getValue());
    }


    /**
     * Returns if an order is put into pending orders.
     *
     * @param order an order
     * @return true if an order is reserved, i.e. put into pending queues, false otherwise
     */
    public boolean reserve(Order order) {
        if (canOrder(order)) {
            Log.log(this.res, String.format("Ingredients reserved: %s", order.getAllIngredients()));
            order.getAllIngredients().forEach(Ingredient::reserve);
            statistics.updateManagerInventory();
            backup();
            this.res.checkThresholds();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns if an order can be cooked based on ingredient amount in inventory.
     *
     * @param order an order
     * @return true if an order can be cooked, false otherwise.
     */
    public boolean canCook(Order order) {
        this.res.checkThresholds();
        return order.getAllIngredients().entrySet().stream().allMatch((entry) ->
                entry.getKey().getReserved() >= entry.getValue());
    }

    /**
     * Returns if an order is being cooked.
     *
     * @param order an order
     * @return true if an order will be cooked, false otherwise.
     */
    public boolean cook(Order order) {
        this.res.checkThresholds();
        if (canCook(order)) {
            Log.log(this.res, String.format("Ingredients used: %s", order.getAllIngredients()));
            order.getAllIngredients().forEach(Ingredient::use);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Parses a json file to create ingredients.
     *
     * @param file a json file of ingredients.
     */
    private void parseIngredients(File file) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONArray data = (JSONArray) obj;

            for (Object item : data) {
                JSONObject ingredientObject = (JSONObject) item;
                String displayName = (String) ingredientObject.get("displayName");

                double additionPrice = (double) ingredientObject.get("additionPrice");
                int initialAmount = ((Long) ingredientObject.get("initialAmount")).intValue();
                int threshold = ((Long) ingredientObject.get("threshold")).intValue();
                boolean addable = (boolean) ingredientObject.get("addable");

                ingredients.add(new Ingredient(displayName, additionPrice, initialAmount, threshold, addable));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a particular ingredient from the list.
     *
     * @param ingredient the name of the ingredient to get.
     * @return the specified ingredient if it exists; null otherwise.
     */
    public Ingredient getIngredient(String ingredient) {
        Optional<Ingredient> t = ingredients.stream().filter(a -> a.getDisplayName().equals(ingredient)).findFirst();

        if (t.isPresent()) {
            return t.get();
        } else {
            System.err.println("Invalid type of ingredient " + ingredient + System.lineSeparator() +
                    "Valid Types are:");
            ingredients.forEach(a -> System.err.println("\t" + a.getDisplayName()));
            new Exception().printStackTrace();
            System.exit(1);
        }

        return null;
    }


    /**
     * Returns is an order is already set to be reordered.
     *
     * @param ingredient the ingredient to check
     * @return whether the ingredient is already due to get reordered.
     */
    public boolean isInReorder(Ingredient ingredient) {
        return (this.reorders.containsKey(ingredient));
    }

    /**
     * Orders an ingredient
     *
     * @param ingredient to order
     */
    public void reorderIngredient(Ingredient ingredient) {
        reorders.put(ingredient, DEFAULT_ORDER_AMOUNT);
    }

    /**
     * Removes an ingredient from the re-order map
     *
     * @param ingredient to un-reorder
     */
    public void removeReorderIngredient(Ingredient ingredient) {
        reorders.remove(ingredient);
    }

    /**
     * Updates the request.txt file to use the file with new shipment orders.
     */
    public void updateRequestsFile() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(requests, false)))) {
            for (Map.Entry<Ingredient, Integer> entry : reorders.entrySet()) {
                out.println(entry.getKey().getDisplayName() + "|" + entry.getValue());
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a string representation or a reorder request.
     *
     * @return a String of reorders.
     */
    public String getReorderString() {
        StringBuilder builder = new StringBuilder("===== Restock Reorders =====");

        this.reorders.forEach((k, v) -> builder.append(String.format("%s%d units of %s", System.lineSeparator(), v, k)));
        return builder.toString();
    }


    /**
     * Returns a string representation of IngredientManager.
     *
     * @return a String including the current inventory.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("====Inventory====").append(System.lineSeparator());
        ingredients.forEach(a -> sb.append(a.toDetailedString()).append(System.lineSeparator()));
        sb.append("=================").append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Backups the Ingredient inventory
     */
    public void backup() {
        JSONArray data = new JSONArray();
        for (Ingredient item : ingredients) {
            JSONObject obj = new JSONObject();
            obj.put("displayName", item.getDisplayName());
            obj.put("initialAmount", item.getAmount());
            obj.put("additionPrice", item.getAdditionPrice());
            obj.put("threshold", item.getThreshold());
            obj.put("addable", item.isAddable());
            data.add(obj);
        }

        try (FileWriter file = new FileWriter("resources/data/inventory.json", false)) {
            file.write(data.toJSONString());
        } catch (Exception ignored) {

        }
    }
}
