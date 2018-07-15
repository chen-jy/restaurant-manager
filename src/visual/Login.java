package visual;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The Main class of this application
 */
public class Login extends Application {
    private static String CSS; // the style file for the application
    public static final Logger logger = Logger.getLogger("MyLog");  // the logger instance that is used

    /**
     * Everything starts from here
     *
     * @param primaryStage the root node of the tree
     * @throws Exception if unable to read style class
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        CSS = getClass().getResource("/css/stylecomp.css").toExternalForm();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        primaryStage.setResizable(false);
        JFXDecorator decorator = new JFXDecorator(primaryStage, root);

        Scene sc = new Scene(decorator, 250, 300);
        sc.getStylesheets().add(getCSS());
        primaryStage.setScene(sc);
        primaryStage.show();


        try {
            // This block configure the logger with handler and formatter
            FileHandler fh = new FileHandler("resources/data/log.txt", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the CSS style file
     *
     * @return the css style file
     */
    public static String getCSS() {
        return CSS;
    }
}
