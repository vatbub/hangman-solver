/**
 * Sample Skeleton for "AlertDialog.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

package view.updateAvailableDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import common.UpdateChecker;
import common.UpdateInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UpdateAvailableDialog {

	private static Stage stage;
	private static ResourceBundle bundle = ResourceBundle.getBundle("view.updateAvailableDialog.AlertDialog");
	private static String messageText;
	private static UpdateInfo updateInfo;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="cancelButton"
	private Button cancelButton; // Value injected by FXMLLoader

	@FXML // fx:id="detailsLabel"
	private Label detailsLabel; // Value injected by FXMLLoader

	@FXML // fx:id="messageLabel"
	private Label messageLabel; // Value injected by FXMLLoader

	@FXML // fx:id="okButton"
	private Button okButton; // Value injected by FXMLLoader

	// Handler for Button[fx:id="cancelButton"] onAction
    @FXML
    void ignoreButtonOnAction(ActionEvent event) {
        UpdateAvailableDialog.hide();
        UpdateChecker.ignoreUpdate(updateInfo.toVersion);
    }

    // Handler for Button[fx:id="okButton"] onAction
    @FXML
    void okButtonOnAction(ActionEvent event) {
    	UpdateAvailableDialog.hide();
        // Install
    	UpdateChecker.downloadAndInstallUpdate(updateInfo);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert detailsLabel != null : "fx:id=\"detailsLabel\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'AlertDialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'AlertDialog.fxml'.";

        // Initialize your logic here: all @FXML variables will have been injected
        messageLabel.setText(messageText);

    }

	public static void show(UpdateInfo update) {
		stage = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(UpdateAvailableDialog.class.getResource("AlertDialog.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setTitle(bundle.getString("window.Title"));

			stage.setMinWidth(scene.getRoot().minWidth(0) + 70);
			stage.setMinHeight(scene.getRoot().minHeight(0) + 70);
			
			messageText = "Filesize: " + update.fileSizeInMB +" MB, Version to download: " + update.toVersion.toString();
			updateInfo = update;

			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void hide() {
		stage.hide();
	}
}
