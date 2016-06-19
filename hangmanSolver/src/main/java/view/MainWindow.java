package view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Sample Skeleton for "MainWindow.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;
import algorithm.*;
import common.Config;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import languages.Language;

public class MainWindow extends Application implements Initializable {

	public static void main(String[] args) {
		launch(args);
	}

	private ResourceBundle bundle = ResourceBundle.getBundle("view.strings.messages");
	private Result currentSolution;
	private boolean shareThoughtsBool;
	private String lastThought;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="actionLabel"
	private Label actionLabel; // Value injected by FXMLLoader

	@FXML // fx:id="copyButton"
	private Button copyButton; // Value injected by FXMLLoader

	@FXML
	private Button creditsButton;

	@FXML // fx:id="currentSequence"
	private TextField currentSequence; // Value injected by FXMLLoader

	@FXML // fx:id="getNextLetter"
	private Button getNextLetter; // Value injected by FXMLLoader

	@FXML // fx:id="languageSelector"
	private ComboBox<String> languageSelector; // Value injected by FXMLLoader

	@FXML // fx:id="result"
	private TextField result; // Value injected by FXMLLoader

	@FXML
	private CheckBox shareThoughtsCheckbox;

	@FXML
	private Label thoughts;

	@FXML
	private Button newGameButton;
	
	@FXML
	private TextArea proposedSolutions;

	@FXML
	void newGameButtonOnAction(ActionEvent event) {
		algorithm.HangmanSolver.proposedSolutions.clear();
		currentSequence.setText("");
	}

	/**
	 * Handler for Button[fx:id="copyButton"] onAction<br>
	 * <br>
	 * Copies the result of the algorithm to the system clipboard
	 * 
	 * @param event
	 *            The event object (automatically injected)
	 */
	@FXML
	void copyResultToClipboard(ActionEvent event) {
		StringSelection selection = new StringSelection(result.getText());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	/**
	 * Handler for Button[fx:id="getNextLetter"] onAction<br>
	 * Fires when the user is in the text field and hits the enter key or clicks
	 * the 'get result button'
	 * 
	 * @param event
	 *            The event object (automatically injected)
	 */
	@FXML
	void getNextLetterAction(ActionEvent event) {
		launchAlgorithm();
	}

	@FXML
	void creditsButtonOnAction(ActionEvent event) {
		LicenseWindow.show(bundle.getString("licenseWindowTitle"));
	}

	@FXML
	void shareThoughtsCheckboxOnAction(ActionEvent event) {
		shareThoughtsBool = shareThoughtsCheckbox.isSelected();

		if (shareThoughtsBool) {
			setThought();
		} else {
			// Clear the thoughts label
			thoughts.setText("");
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setTitle(bundle.getString("windowTitle"));

			primaryStage.setMinWidth(scene.getRoot().minWidth(0) + 70);
			primaryStage.setMinHeight(scene.getRoot().minHeight(0) + 70);

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
		loadLanguageList();
		shareThoughtsCheckbox.setSelected(true);
		shareThoughtsBool = true;
	}

	void launchAlgorithm() {
		currentSolution = HangmanSolver.solve(currentSequence.getText(),
				Language.getSupportedLanguages().get(languageSelector.getSelectionModel().getSelectedIndex()));
		result.setText(currentSolution.result);
		
		String proposedSolutionsString = "";
		for (String solution:HangmanSolver.proposedSolutions){
			proposedSolutionsString = proposedSolutionsString + solution + ", ";
		}
		
		// remove last ,
		proposedSolutionsString = proposedSolutionsString.substring(0, proposedSolutionsString.length()-2);
		proposedSolutions.setText(proposedSolutionsString);

		if (currentSolution.bestWordScore >= Config.thresholdToShowWord) {
			String thoughtText = bundle.getString("thinkOfAWord")
					.replace("<percent>", Double.toString(Math.round(currentSolution.bestWordScore * 100)))
					.replace("<word>", currentSolution.bestWord);
			setThought(thoughtText);
		}else {
			setThought(bundle.getString("dontThinkAWord"));
		}

	}

	public void setThought() {
		setThought(lastThought);
	}

	public void setThought(String thought) {

		lastThought = thought;

		if (shareThoughtsBool) {
			thoughts.setText(thought);
		}
	}

	private void loadLanguageList() {
		System.out.println("Loading language list...");

		ObservableList<String> items = FXCollections.observableArrayList();

		// Load the languages
		for (Language lang : Language.getSupportedLanguages()) {
			items.add(lang.getHumanReadableName());
		}

		languageSelector.setItems(items);

		System.out.println("Languages loaded");

	}

}
