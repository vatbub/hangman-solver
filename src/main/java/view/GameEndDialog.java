package view;

import java.io.IOException;

/**
 * Sample Skeleton for "askIfWin.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import algorithm.GameState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logging.FOKLogger;
import stats.HangmanStats;

/**
 * A dialog to ask the user if the computer has won the game. Will probably be
 * removed when the win detector is introduced.
 * 
 * @author frede
 *
 */
public class GameEndDialog {

	private static Stage stage;
	private static MainWindow mainWindowCopy;
	private static GameState gameStateCopy;
	private static ResourceBundle bundle = ResourceBundle.getBundle("view.strings.messages");
	private static FOKLogger log = new FOKLogger(GameEndDialog.class.getName());

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="newGameButton"
	private Button newGameButton; // Value injected by FXMLLoader

	@FXML // fx:id="quitAppButton"
	private Button quitAppButton; // Value injected by FXMLLoader

	@FXML // fx:id="solutionLabel"
	private Label solutionLabel; // Value injected by FXMLLoader

	@FXML // fx:id="solutionTextBox"
	private TextField solutionTextBox; // Value injected by FXMLLoader

	@FXML // fx:id="submitButton"
	private Button submitButton; // Value injected by FXMLLoader

	@FXML // fx:id="questionLabel"
	private Label questionLabel; // Value injected by FXMLLoader

	// Handler for Button[fx:id="quitAppButton"] onAction
	@FXML
	void quitAppButtonOnAction(ActionEvent event) {
		// handle the event here
		Platform.exit();
	}

	// Handler for Button[fx:id="newGameButton"] onAction
	@FXML
	void newGameButtonOnAction(ActionEvent event) {
		// handle the event here
		mainWindowCopy.startNewGame(false);
		hide();
	}

	// Handler for Button[fx:id="submitButton"] onAction
	@FXML
	void submitButtonOnActio(ActionEvent event) {
		// handle the event here
		if (!solutionTextBox.getText().equals("")) {
			HangmanStats.addWordToDatabase(solutionTextBox.getText(), MainWindow.currentSolution.lang);
			solutionLabel.setText(bundle.getString("solutionLabel.thankYouText"));
			solutionTextBox.setDisable(true);
			submitButton.setDefaultButton(false);
			submitButton.setDisable(true);
			newGameButton.setDefaultButton(true);
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is
			// complete
	void initialize() {
		assert newGameButton != null : "fx:id=\"newGameButton\" was not injected: check your FXML file 'GameEndDialog.fxml'.";
		assert questionLabel != null : "fx:id=\"questionLabel\" was not injected: check your FXML file 'GameEndDialog.fxml'.";
		assert quitAppButton != null : "fx:id=\"quitAppButton\" was not injected: check your FXML file 'GameEndDialog.fxml'.";
		assert solutionLabel != null : "fx:id=\"solutionLabel\" was not injected: check your FXML file 'GameEndDialog.fxml'.";
		assert solutionTextBox != null : "fx:id=\"solutionTextBox\" was not injected: check your FXML file 'GameEndDialog.fxml'.";
		assert submitButton != null : "fx:id=\"submitButton\" was not injected: check your FXML file 'GameEndDialog.fxml'.";

		// Initialize your logic here: all @FXML variables will have been
		// injected
		switch (gameStateCopy) {
		case GAME_LOST:
			questionLabel.setText(bundle.getString("looseText"));
			break;
		case GAME_RUNNING:
			questionLabel.setText(bundle.getString("gameRunningText"));
			break;
		case GAME_WON:
			questionLabel.setText(bundle.getString("winText"));
			break;
		}

		solutionTextBox.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.contains("_")) {
					solutionLabel.setText(bundle.getString("solutionLabel.enterSolutionText"));
					submitButton.setDisable(true);
				} else {
					submitButton.setDisable(false);
				}
			}
		});

		solutionTextBox.setText(mainWindowCopy.currentSequence.getText());
	}

	public static void show(String windowTitle, GameState gameState, MainWindow mainWindow) {
		stage = new Stage();
		gameStateCopy = gameState;
		mainWindowCopy = mainWindow;

		Parent root;
		try {
			root = FXMLLoader.load(GameEndDialog.class.getResource("GameEndDialog.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setTitle(windowTitle);

			stage.setMinWidth(scene.getRoot().minWidth(0) + 18);
			stage.setMinHeight(scene.getRoot().minHeight(0) + 47);

			stage.setScene(scene);
			
			stage.initModality(Modality.WINDOW_MODAL);
			
		    stage.initOwner(
		        (mainWindowCopy.getScene().getWindow()));
			
			stage.show();
		} catch (IOException e) {
			log.getLogger().log(Level.SEVERE, "An error occurred", e);
		}
	}

	public static void hide() {
		stage.hide();
	}

}
