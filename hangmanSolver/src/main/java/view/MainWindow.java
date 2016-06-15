package view;



import java.io.IOException;

/**
 * Sample Skeleton for "MainWindow.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class MainWindow {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="actionLabel"
    private Label actionLabel; // Value injected by FXMLLoader

    @FXML // fx:id="copyButton"
    private Button copyButton; // Value injected by FXMLLoader

    @FXML // fx:id="currentSequence"
    private TextField currentSequence; // Value injected by FXMLLoader

    @FXML // fx:id="getNextLetter"
    private Button getNextLetter; // Value injected by FXMLLoader

    @FXML // fx:id="languageSelector"
    private ComboBox<?> languageSelector; // Value injected by FXMLLoader

    @FXML // fx:id="result"
    private TextField result; // Value injected by FXMLLoader


    // Handler for Button[fx:id="copyButton"] onAction
    @FXML
    void copyResultToClipboard(ActionEvent event) {
        // handle the event here
    }

    /**
     *  Handler for TextField[fx:id="currentSequence"] onKeyTyped
     * @param event The 
     */
    @FXML
    void currentSequenceKeyTyped(KeyEvent event) {
    	
    }

    // Handler for Button[fx:id="getNextLetter"] onAction
    @FXML
    void getNextLetterAction(ActionEvent event) {
        // handle the event here
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert actionLabel != null : "fx:id=\"actionLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert copyButton != null : "fx:id=\"copyButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert currentSequence != null : "fx:id=\"currentSequence\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert getNextLetter != null : "fx:id=\"getNextLetter\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert languageSelector != null : "fx:id=\"languageSelector\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert result != null : "fx:id=\"result\" was not injected: check your FXML file 'MainWindow.fxml'.";

        // Initialize your logic here: all @FXML variables will have been injected

    }
    
    public static void main(String[] args){
    	try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Edit Person");
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);

            // Set the person into the controller.
            MainWindow controller = loader.getController();

            // Show the dialog and wait until the user closes it
            primaryStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
