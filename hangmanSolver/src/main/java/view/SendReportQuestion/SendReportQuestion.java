package view.SendReportQuestion;

import java.io.IOException;

/**
 * Sample Skeleton for "AlertDialog.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;

import common.Config;
import common.Internet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import view.LicenseWindow;


public class SendReportQuestion {
	
	private static String eventName;
	private static Stage stage;
	private static ResourceBundle bundle = ResourceBundle.getBundle("view.SendReportQuestion.AlertDialog");

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="actionButton"
    private Button actionButton; // Value injected by FXMLLoader

    @FXML // fx:id="actionParent"
    private HBox actionParent; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader

    @FXML // fx:id="detailsLabel"
    private Label detailsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="okParent"
    private HBox okParent; // Value injected by FXMLLoader
    
    @FXML
    void actionButtonOnAction(ActionEvent event) {
    	try {
			Internet.sendEventToIFTTTMakerChannel(Config.iftttMakerApiKey, eventName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			hide();
		}
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
    	hide();
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert actionButton != null : "fx:id=\"actionButton\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert actionParent != null : "fx:id=\"actionParent\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert detailsLabel != null : "fx:id=\"detailsLabel\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert okParent != null : "fx:id=\"okParent\" was not injected: check your FXML file 'AlertDialog.fxml'.";

        // Initialize your logic here: all @FXML variables will have been injected

    }
    
    public static void show(String windowTitle, String iftttEventName){
    	stage = new Stage();
    	Parent root;
    	eventName = iftttEventName;
		try {
			root = FXMLLoader.load(SendReportQuestion.class.getResource("AlertDialog.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setTitle(windowTitle);

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
