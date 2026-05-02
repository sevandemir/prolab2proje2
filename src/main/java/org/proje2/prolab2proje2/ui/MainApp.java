package org.proje2.prolab2proje2.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * MainApp serves as the entry point for starting the JavaFX application.
 */
public class MainApp extends Application {

    /**
     * Initializes the JavaFX Stage by loading the FXML view and associated stylesheets.
     * @param primaryStage The primary window frame for the application.
     * @throws Exception If loading the FXML or CSS fails.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Specify the path to the main FXML layout file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/proje2/prolab2proje2/ui/design.fxml"));
        
        // 2. Load the FXML view graph.
        Parent root = loader.load();
        
        // 3. Construct a scene with default width (900px) and height (600px).
        Scene scene = new Scene(root, 1000, 900);
        
        // 4. Attach the CSS style file for styling the UI components.
        String css = Objects.requireNonNull(getClass().getResource("/org/proje2/prolab2proje2/ui/styles.css")).toExternalForm();
        scene.getStylesheets().add(css);
        
        // 5. Setup basic window properties.
        primaryStage.setTitle("Prolab 2 - ML Algorithm Performance Benchmarker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}