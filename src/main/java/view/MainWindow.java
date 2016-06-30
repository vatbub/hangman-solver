package view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import java.net.URL;
import java.util.ResourceBundle;
import algorithm.*;
import common.Config;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import languages.Language;
import stats.HangmanStats;
import stats.MongoSetup;
import view.noLanguageSelected.NoLanguageSelected;
import view.noSequenceEntered.NoSequenceEntered;

/**
 * The apps MainWindow controller class.
 **/
public class MainWindow extends Application implements Initializable {

	public static void main(String[] args) {
		launch(args);
	}

	private ResourceBundle bundle = ResourceBundle.getBundle("view.strings.messages");
	private Result currentSolution;
	private boolean shareThoughtsBool;
	private String lastThought;

	@FXML 
	/**
	 * ResourceBundle that was given to the FXMLLoader
	 */
	private ResourceBundle resources;

	@FXML
	/**
	 * URL location of the FXML file that was given to the FXMLLoader
	 */
	private URL location;

	@FXML
	/**
	 * fx:id="actionLabel"
	 */
	private Label actionLabel; // Value injected by FXMLLoader

	@FXML 
	/**
	 * fx:id="copyButton"
	 */
	private Button copyButton; // Value injected by FXMLLoader

	@FXML
	/**
	 * fx:id="creditsButton"
	 */
	private Button creditsButton;

	@FXML 
	/**
	 * fx:id="currentSequence"
	 */
	private TextField currentSequence; // Value injected by FXMLLoader

	@FXML 
	/**
	 * fx:id="getNextLetter"
	 */
	private Button getNextLetter; // Value injected by FXMLLoader

	@FXML 
	/**fx:id="languageSelector"
	 * 
	 */
	private ComboBox<String> languageSelector; // Value injected by FXMLLoader

	@FXML 
	/**
	 * fx:id="result"
	 */
	private TextField result; // Value injected by FXMLLoader

	@FXML
	/**
	 * fx:id="shareThoughtsCheckbox"
	 */
	private CheckBox shareThoughtsCheckbox;

	@FXML
	/**
	 * fx:id="thoughts"
	 */
	private Label thoughts;

	@FXML
	/**
	 * fx:id="newGameButton"
	 */
	private Button newGameButton;

	@FXML
	/**
	 * fx:id="proposedSolutions"
	 */
	private TextArea proposedSolutions;

	@FXML 
	/**
	 * fx:id="updateLink"
	 */
	private Hyperlink updateLink; // Value injected by FXMLLoader

	@FXML 
	/**
	 * fx:id="versionLabel"
	 */
	private Label versionLabel; // Value injected by FXMLLoader

	@FXML
	/**
	 * Handler for Hyperlink[fx:id="updateLink"] onAction
	 * @param event The event object that contains information about the event.
	 */
	void updateLinkOnAction(ActionEvent event) {
		// TODO: handle the event here
	}

	@FXML
	/**
	 * Handler for Hyperlink[fx:id="newGameButton"] onAction
	 * @param event The event object that contains information about the event.
	 */
	void newGameButtonOnAction(ActionEvent event) {
		algorithm.HangmanSolver.proposedSolutions.clear();
		currentSequence.setText("");
		proposedSolutions.setText("");
		setThought("");
		result.setText("");

		// Submit info if game was won
		AskIfIWin.show(bundle.getString("wellPerformedWindowTitle"));
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
	/**
	 * Handler for Hyperlink[fx:id="creditsButton"] onAction
	 * @param event The event object that contains information about the event.
	 */
	void creditsButtonOnAction(ActionEvent event) {
		LicenseWindow.show(bundle.getString("licenseWindowTitle"));
	}

	@FXML
	/**
	 * Handler for Hyperlink[fx:id="shareThoughtsCheckbox"] onAction
	 * @param event The event object that contains information about the event.
	 */
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
	/**
	 * Method is invoked by JavaFX after the application launch
	 */
	public void start(Stage primaryStage) {
		try {
			common.Common.setAppName("hangmanSolver");
			if (HangmanStats.uploadThread.isAlive() == false) {
				HangmanStats.uploadThread.start();
			}
			Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setTitle(bundle.getString("windowTitle"));

			primaryStage.setMinWidth(scene.getRoot().minWidth(0) + 70);
			primaryStage.setMinHeight(scene.getRoot().minHeight(0) + 70);

			primaryStage.setScene(scene);
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		        	  // Executed when Mian Window is closed
		        	  System.out.println("Shutting down....");
		        	  MongoSetup.close();
		        	  HangmanStats.uploadThread.interrupt();
		              System.out.println("Good bye");
		          }
		      });        
			
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method is invoked by the FXML Loader after all variables have been injected.
	 */
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
		versionLabel.setText(common.Common.getAppVersion());
		
		// Initialize the language search field.
		new AutoCompleteComboBoxListener<String>(languageSelector);
	}

	/**
	 * This method launches the algorithm and writes its results into the gui.
	 */
	void launchAlgorithm() {
		try {
			currentSolution = HangmanSolver.solve(currentSequence.getText(),
					Language.getSupportedLanguages().get(languageSelector.getSelectionModel().getSelectedIndex()));
			result.setText(currentSolution.result);

			String proposedSolutionsString = "";
			for (String solution : HangmanSolver.proposedSolutions) {
				proposedSolutionsString = proposedSolutionsString + solution + ", ";
			}

			// remove last ,
			proposedSolutionsString = proposedSolutionsString.substring(0, proposedSolutionsString.length() - 2);
			proposedSolutions.setText(proposedSolutionsString);

			if (currentSolution.bestWordScore >= Config.thresholdToShowWord) {
				String thoughtText = bundle.getString("thinkOfAWord")
						.replace("<percent>", Double.toString(Math.round(currentSolution.bestWordScore * 100)))
						.replace("<word>", currentSolution.bestWord);
				setThought(thoughtText);
			} else {
				setThought(bundle.getString("dontThinkAWord"));
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// No language selected
			NoLanguageSelected.show();
		} catch (StringIndexOutOfBoundsException e2) {
			// No sequence entered
			NoSequenceEntered.show();
		}

	}

	/**
	 * Writes the last thought into the thoughts-label.
	 */
	public void setThought() {
		setThought(lastThought);
	}

	/**
	 * Writes the given thought into the thoughts-label. The last thought is remembered and can be recalled with {@code setThought()}. 
	 * @param thought The thought to be written to the gui.
	 */
	public void setThought(String thought) {

		lastThought = thought;

		if (shareThoughtsBool) {
			thoughts.setText(thought);
		}
	}

	/**
	 * Loads the available languages into the gui dropdown.
	 */
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
