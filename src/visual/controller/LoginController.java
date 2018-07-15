package visual.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import core.Restaurant;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import visual.gui.Cook;
import visual.gui.Manager;
import visual.gui.Server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * The controller class for the Login GUI
 */
public class LoginController {
    /*
    FXML references == DO NOT set to private
     */
    @FXML
    StackPane stackPane; // the base stack pane node
    @FXML
    JFXTextField username; // the username field
    @FXML
    JFXPasswordField password; // the password field
    @FXML
    JFXButton submit; // the submit button
    @FXML
    Label errorLabel; // the error label

    private JSONArray userData; // an array of all accounts

    // Constants to store the file paths of required configuration files
    private static final String RESOURCES_FP = "resources/settings/requests.txt";
    private static final String MENU_FP = "resources/settings/menu.json";
    private static final String INGREDIENTS_FP = "resources/settings/ingredients.json";
    private static final String SETTINGS_FP = "resources/settings/settings.json";
    private static final String PAYMENTS_FP = "resources/data/payments.json";

    /**
     * Initialize this Log-In GUI and sets all the relevant listeners
     */
    public void initialize() {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(new File("resources/settings/accounts.json")));

            userData = (JSONArray) obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        username.textProperty().addListener((a1, a2, a3) -> {
            if (a3.length() >= 16) {
                username.setText(a2);
            }
        });

        File requests = new File(RESOURCES_FP);
        File menu = new File(MENU_FP);
        File ingredients = new File(INGREDIENTS_FP);
        File settings = new File(SETTINGS_FP);
        File payments = new File(PAYMENTS_FP);
        Restaurant restaurant = new Restaurant(settings, ingredients, requests, menu, payments);

        submit.setDefaultButton(true);
        submit.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = password.getText();

            if (user.isEmpty()) {
                errorLabel.setText("Username cannot be empty");
                return;
            }
            if (pass.isEmpty()) {
                errorLabel.setText("Password cannot be empty");
                return;
            }

            boolean passed = login(user, pass, restaurant);
            if (!passed) {
                errorLabel.setText("Username or password \nincorrect");
            } else {
                errorLabel.setText("");
                username.clear();
                password.clear();
            }
        });
    }

    /**
     * Logs a user in
     *
     * @param user       the user name
     * @param pass       the password (unencrypted)
     * @param restaurant a restaurant reference
     * @return boolean, whether the user was successfully logged in or not.
     */
    public boolean login(String user, String pass, Restaurant restaurant) {
        String type = findType(user, pass);
        if (type == null) {
            return false;
        }

        switch (type) {
            case "Server":
                new Server(user, restaurant);
                return true;
            case "Cook":
                new Cook(user, restaurant);
                return true;
            case "Manager":
                new Manager(user, restaurant);
                return true;
            default:
                return false;
        }
    }

    /**
     * Finds the type of the user and logs the user in, if possible.
     *
     * @param username the username of the user.
     * @param password the user's unencrypted password.
     * @return the user's type if the user exists; null otherwise.
     */
    private String findType(String username, String password) {
        Optional baseuser = userData.stream().filter(type -> {
            JSONArray users = (JSONArray) ((JSONObject) type).get("users");

            return users.stream().anyMatch(user -> {
                JSONObject userobject = (JSONObject) user;

                return userobject.get("user").toString().toLowerCase().equals(username.toLowerCase())
                        && userobject.get("pass").equals(sha256(password));
            });
        }).findFirst();

        if (baseuser.isPresent()) {
            return ((JSONObject) baseuser.get()).get("type").toString();
        } else {
            return null;
        }
    }

    /**
     * Computes the SHA-256 hash of a given string and returns it in hexadecimal as a string.
     *
     * @param cleartext the string to be hashed.
     * @return the SHA-256 hash of the cleartext.
     */
    private String sha256(String cleartext) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] plaintext = cleartext.getBytes(StandardCharsets.UTF_8); // Convert the cleartext to byte form
            byte[] hash = digest.digest(plaintext); // Hash the prepared plaintext

            for (byte b : hash) {
                sb.append(byteToHex(b)); // Convert the hashed bytes into hexadecimal
            }
        } catch (NoSuchAlgorithmException ignored) {
        } // Should never be thrown, since SHA-256 must exist

        return sb.toString(); // Return the hexadecimal string
    }

    /**
     * Converts a single byte into a hexadecimal number with length 2. If the number is less than 0xf, it
     * is padded with an extra '0' on the right.
     *
     * @param b the byte to be converted into hexadecimal.
     * @return the length-2 hexadecimal representation of b.
     */
    private String byteToHex(byte b) {
        if (Byte.toUnsignedInt(b) < 16) {
            b <<= 1; // Logical left shift by 1 bit
        }
        return Integer.toHexString(b & 0xff); // Bitwise AND with 255 to convert to unsigned number
    }
}
