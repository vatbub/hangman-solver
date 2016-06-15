package view;

import java.io.IOException;

/**
 * Sample Skeleton for "MainWindow.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainWindow extends Application implements Initializable {

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

	@FXML
	private Label invalidCharactersMessageLabel;

	@FXML
	private Rectangle invalidCharactersMessageRectangle;

	@FXML
	private Polygon invalidCharactersMessageTriangle;

	@FXML
	private AnchorPane invalidCharactersMessage;
	
	// Handler for Button[fx:id="copyButton"] onAction
	@FXML
	void copyResultToClipboard(ActionEvent event) {
		// handle the event here
	}

	/**
	 * Handler for TextField[fx:id="currentSequence"] onKeyTyped<br>
	 * Removes any letters from the text field that are not underscores or
	 * spaces and displays a message to the user, if he has entered an invalid
	 * letter.
	 * 
	 * @param event
	 *            The event object (injected automatically)
	 */
	@FXML
	void currentSequenceKeyTyped(KeyEvent event) {
		String curValue = currentSequence.getText();
		boolean showMessage = false;
		String newValue = "";

		for (int i = 0; i < curValue.length() - 1; i++) {
			if (curValue.substring(i, i + 1).equals(" ") || curValue.substring(i, i + 1).equals("_")) {
				System.out.println(newValue);
				newValue = newValue + curValue.substring(i, i + 1);
			} else {
				showMessage = true;
			}
		}

		if (showMessage) {
			showinvalidCharactersMessage();
		} else {
			hideinvalidCharactersMessage();
		}
	}

	private void showinvalidCharactersMessage() {
		showinvalidCharactersMessage(false);
	}

	private void showinvalidCharactersMessage(boolean noAnimation) {
		invalidCharactersMessageLabel.setMouseTransparent(false);
		invalidCharactersMessageRectangle.setMouseTransparent(false);
		invalidCharactersMessageTriangle.setMouseTransparent(false);

		if (!noAnimation) {
			FadeTransition ft = new FadeTransition(Duration.millis(300), invalidCharactersMessage);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();

		} else {
			invalidCharactersMessageLabel.setOpacity(1);
			invalidCharactersMessageRectangle.setOpacity(1);
			invalidCharactersMessageTriangle.setOpacity(1);
		}
	}

	private void hideinvalidCharactersMessage() {
		hideinvalidCharactersMessage(false);
	}

	private void hideinvalidCharactersMessage(boolean noAnimation) {
		invalidCharactersMessageLabel.setMouseTransparent(true);
		invalidCharactersMessageRectangle.setMouseTransparent(true);
		invalidCharactersMessageTriangle.setMouseTransparent(true);

		if (!noAnimation) {
			for (double i = 1; i >= 0; i = i - 0.01) {
				invalidCharactersMessageLabel.setOpacity(i);
				invalidCharactersMessageRectangle.setOpacity(i);
				invalidCharactersMessageTriangle.setOpacity(i);
			}
		} else {
			invalidCharactersMessageLabel.setOpacity(0);
			invalidCharactersMessageRectangle.setOpacity(0);
			invalidCharactersMessageTriangle.setOpacity(0);
		}
	}

	// Handler for Button[fx:id="getNextLetter"] onAction
	@FXML
	void getNextLetterAction(ActionEvent event) {
		// handle the event here
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("view.strings.messages");
			System.out.println(bundle.getString("languageLabel"));
			Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		assert actionLabel != null : "fx:id=\"actionLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert copyButton != null : "fx:id=\"copyButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert currentSequence != null : "fx:id=\"currentSequence\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert getNextLetter != null : "fx:id=\"getNextLetter\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert languageSelector != null : "fx:id=\"languageSelector\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert result != null : "fx:id=\"result\" was not injected: check your FXML file 'MainWindow.fxml'.";

		// Initialize your logic here: all @FXML variables will have been
		// injected

		hideinvalidCharactersMessage(true);
	}

}
