/**
 * Sample Skeleton for "AlertDialog.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

package view.noLanguageSelected;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import view.SendReportQuestion.SendReportQuestion;


public class NoLanguageSelected {
	
	private static ResourceBundle bundle = ResourceBundle.getBundle("view.noLanguageSelected.AlertDialog");
	private static Stage stage;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="detailsLabel"
    private Label detailsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="okButton"
    private Button okButton; // Value injected by FXMLLoader

    @FXML // fx:id="okParent"
    private HBox okParent; // Value injected by FXMLLoader


    // Handler for Button[fx:id="okButton"] onAction
    @FXML
    void okButtonOnAction(ActionEvent event) {
        // handle the event here
    	hide();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert detailsLabel != null : "fx:id=\"detailsLabel\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert okParent != null : "fx:id=\"okParent\" was not injected: check your FXML file 'AlertDialog.fxml'.";

        // Initialize your logic here: all @FXML variables will have been injected

    }
    
    public static void show(){
    	stage = new Stage();
    	Parent root;
		try {
			root = FXMLLoader.load(NoLanguageSelected.class.getResource("AlertDialog.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setTitle(bundle.getString("windowTitle"));

			stage.setMinWidth(scene.getRoot().minWidth(0) + 70);
			stage.setMinHeight(scene.getRoot().minHeight(0) + 70);

			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void hide(){
    	stage.hide();
    }

}
