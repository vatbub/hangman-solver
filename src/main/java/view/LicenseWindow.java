package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A window diaplying a dialog with some licensing info.
 * 
 * @author frede
 *
 */
public class LicenseWindow {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	void initialize() {
	}

	/**
	 * Shows the dialog.
	 * 
	 * @param windowTitle
	 *            The window title to set
	 */
	public static void show(String windowTitle) {
		Stage stage = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(LicenseWindow.class.getResource("LicenseWindow.fxml"));
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

}
