package view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import algorithm.*;
import common.Common;
import common.Config;
import common.ProgressDialog;
import common.UpdateChecker;
import common.UpdateInfo;
import common.Version;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import languages.Language;
import languages.TabFile;
import logging.FOKLogger;
import stats.HangmanStats;
import stats.MongoSetup;
import view.updateAvailableDialog.UpdateAvailableDialog;

/**
 * The MainWindow controller class.
 **/
public class MainWindow extends Application implements Initializable, ProgressDialog {

	private static double curVersionEasterEggTurnDegrees = 0;
	private static boolean disableUpdateChecks = false;
	private static FOKLogger log;

	public static void main(String[] args) {
		common.Common.setAppName("hangmanSolver");
		log = new FOKLogger(MainWindow.class.getName());
		for (String arg : args) {
			if (arg.toLowerCase().matches("mockappversion=.*")) {
				// Set the mock version
				String version = arg.substring(arg.toLowerCase().indexOf('=') + 1);
				Common.setMockAppVersion(version);
			} else if (arg.toLowerCase().matches("mockbuildnumber=.*")) {
				// Set the mock build number
				String buildnumber = arg.substring(arg.toLowerCase().indexOf('=') + 1);
				Common.setMockBuildNumber(buildnumber);
			} else if (arg.toLowerCase().matches("disableupdatechecks")) {
				log.getLogger().info("Update checks are disabled as app was launched from launcher.");
				disableUpdateChecks = true;
			} else if (arg.toLowerCase().matches("mockpackaging=.*")) {
				// Set the mock packaging
				String packaging = arg.substring(arg.toLowerCase().indexOf('=') + 1);
				Common.setMockPackaging(packaging);
			}
		}

		launch(args);
	}

	private ResourceBundle bundle = ResourceBundle.getBundle("view.strings.messages");
	private ResourceBundle errorMessageBundle = ResourceBundle.getBundle("view.strings.errormessages");
	private static String currentSequenceStr;
	public static Result currentSolution;
	private boolean shareThoughtsBool;
	private String lastThought;
	private static Scene scene;
	private static Stage stage;
	private static int clickCounter = 0;

	public Scene getScene() {
		return scene;
	}

	public Stage getStage() {
		return stage;
	}

	@FXML // fx:id="loadLanguagesProgressBar"
	private ProgressBar loadLanguagesProgressBar; // Value injected by
													// FXMLLoader

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
	 * fx:id="applyButton"
	 */
	private Button applyButton; // Value injected by FXMLLoader

	@FXML
	/**
	 * fx:id="creditsButton"
	 */
	private Button creditsButton;

	@FXML // fx:id="currentAppVersionTextLabel"
	private Label currentAppVersionTextLabel; // Value injected by FXMLLoader

	@FXML
	/**
	 * fx:id="currentSequence"
	 */
	public TextField currentSequence; // Value injected by FXMLLoader

	@FXML
	/**
	 * fx:id="getNextLetter"
	 */
	private Button getNextLetter; // Value injected by FXMLLoader

	@FXML
	/**
	 * fx:id="languageSelector"
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
	 * 
	 * @param event
	 *            The event object that contains information about the event.
	 */
	void updateLinkOnAction(ActionEvent event) {
		// Check for new version ignoring ignored updates
		Thread updateThread = new Thread() {
			@Override
			public void run() {
				UpdateInfo update = UpdateChecker.isUpdateAvailableCompareAppVersion(Config.getUpdateRepoBaseURL(),
						Config.groupID, Config.artifactID, Config.updateFileClassifier);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						new UpdateAvailableDialog(update);
					}
				});
			}
		};
		updateThread.setName("manualUpdateThread");
		updateThread.start();
	}

	@FXML
	/**
	 * Handler for Hyperlink[fx:id="newGameButton"] onAction
	 * 
	 */
	void newGameButtonOnAction(ActionEvent event) {
		startNewGame();
	}

	public void startNewGame() {
		startNewGame(true);
	}

	public void startNewGame(boolean submitWord) {
		if (submitWord) {
			// Maybe Submit the word to the MongoDB database
			submitWordOnQuit();
		}

		algorithm.HangmanSolver.proposedSolutions.clear();
		applyButton.setDisable(true);
		languageSelector.setDisable(false);
		currentSequence.setText("");
		proposedSolutions.setText("");
		setThought("");
		result.setText("");
		currentSequence.setDisable(false);
		currentSequence.requestFocus();
	}

	/**
	 * Handler for Button[fx:id="applyButton"] onKeyPressed
	 * 
	 * @param event
	 *            The event object (automatically injected)
	 */
	@FXML
	void applyButtonOnKeyPressed(KeyEvent event) {
		if (!event.getCode().equals(KeyCode.ENTER) && !event.getCode().equals(KeyCode.SPACE)) {
			// If any other Key than ENTER or SPACE is pressed (they have
			// special meanings already handled by JavaFX
			// focus the currentSequence

			if (event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)
					|| event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN)) {
				// Consume the event to disable the default focus system
				event.consume();
			}
			currentSequence.requestFocus();
		}
	}

	/**
	 * Handler for Button[fx:id="applyButton"] onAction<br>
	 * <br>
	 * Applies the current guess to the letter sequence using the bestWord.
	 * 
	 * @param event
	 *            The event object (automatically injected)
	 */
	@FXML
	void applyResult(ActionEvent event) {
		String newSequence = "";

		// Split the pattern up in words
		List<String> words = new ArrayList<String>(Arrays.asList(currentSequence.getText().split(" ")));
		List<String> bestWords = new ArrayList<String>(Arrays.asList(currentSolution.bestWord.split(" ")));

		boolean wordReplaced = false;

		if (currentSolution.result.length() > 1) {
			// The next guess is a word.

			for (int i = 0; i < words.size(); i++) {
				if (!words.get(i).contains("_") || wordReplaced) {
					// Word is already solved or the solution was already
					// applied
					if (newSequence.length() != 0) {
						newSequence = newSequence + " ";
					}
					newSequence = newSequence + words.get(i);
				} else {
					// Replace word
					String newWord = "";
					String oldWord = words.get(i);

					for (int t = 0; t < oldWord.length(); t++) {
						if (oldWord.charAt(t) == '_') {
							// replace it
							newWord = newWord + bestWords.get(i).charAt(t);
						} else {
							// Don't replace it as there is no _
							newWord = newWord + oldWord.charAt(t);
						}
					}

					if (newSequence.length() != 0) {
						newSequence = newSequence + " ";
					}
					newSequence = newSequence + newWord;
					if (currentSolution.resultType == ResultType.word) {
						wordReplaced = true;
					}
				}
			}
		} else {
			// The next guess is a letter

			for (int i = 0; i < words.size(); i++) {
				if (!words.get(i).contains("_") || wordReplaced) {
					// Word is already solved or the solution was already
					// applied
					if (newSequence.length() != 0) {
						newSequence = newSequence + " ";
					}
					newSequence = newSequence + words.get(i);
				} else {
					// add letters
					String newWord = "";
					String oldWord = words.get(i);

					for (int t = 0; t < oldWord.length(); t++) {
						if (oldWord.charAt(t) == '_' && Character.toUpperCase(bestWords.get(i).charAt(t)) == Character
								.toUpperCase(currentSolution.result.charAt(0))) {
							// replace it
							newWord = newWord + bestWords.get(i).charAt(t);
						} else {
							// Don't replace it as there is no _
							newWord = newWord + oldWord.charAt(t);
						}
					}

					if (newSequence.length() != 0) {
						newSequence = newSequence + " ";
					}
					newSequence = newSequence + newWord;
					// wordReplaced=true;
				}
			}
		}

		// Set the new sequence in the gui
		currentSequence.setText(newSequence);

		/*
		 * // submit solved words for (int i = 0; i < words.size(); i++) { if
		 * (!words.get(i).contains("_")) { if (!words.get(i).equals("")) { //
		 * Submit the word to the internet db. // Although this method is called
		 * quite often, it keeps // track of // the submissions to avoid
		 * duplicates. HangmanStats.addWordToDatabase(words.get(i),
		 * currentSolution.lang); } } }
		 */

		// get the next guess
		launchAlgorithm();
	}

	@FXML
	void currentAppVersionTextLabelOnMouseClicked(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			// Do the easter egg when clicking with the left mouse button
			clickCounter++;

			if (clickCounter >= 3) {
				// rotate
				double angle = (Math.random() - 0.5) * 1440;
				curVersionEasterEggTurnDegrees = curVersionEasterEggTurnDegrees + angle;

				RotateTransition rt = new RotateTransition(Duration.millis(500), currentAppVersionTextLabel);
				rt.setByAngle(angle);
				rt.setAutoReverse(true);

				rt.play();
				clickCounter = 0;

				currentAppVersionTextLabel.setTooltip(new Tooltip(bundle.getString("resetEasterEgg")));

				// remove whole turns
				while (curVersionEasterEggTurnDegrees > 360.0) {
					curVersionEasterEggTurnDegrees = curVersionEasterEggTurnDegrees - 360.0;
				}
				while (curVersionEasterEggTurnDegrees < -360.0) {
					curVersionEasterEggTurnDegrees = curVersionEasterEggTurnDegrees + 360.0;
				}
			}
		} else {
			// Reset the easter egg
			if (Math.abs(360.0 - curVersionEasterEggTurnDegrees) < Math.abs(curVersionEasterEggTurnDegrees)) {
				curVersionEasterEggTurnDegrees = -(360.0 - curVersionEasterEggTurnDegrees);
			}
			double angle = -curVersionEasterEggTurnDegrees;
			curVersionEasterEggTurnDegrees = 0;

			RotateTransition rt = new RotateTransition(Duration.millis(500), currentAppVersionTextLabel);
			rt.setByAngle(angle);
			rt.setAutoReverse(true);

			rt.play();
			currentAppVersionTextLabel.setTooltip(null);
		}
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
	 * 
	 * @param event
	 *            The event object that contains information about the event.
	 */
	void creditsButtonOnAction(ActionEvent event) {
		LicenseWindow.show(bundle.getString("licenseWindowTitle"));
	}

	@FXML
	/**
	 * Handler for Hyperlink[fx:id="shareThoughtsCheckbox"] onAction
	 * 
	 * @param event
	 *            The event object that contains information about the event.
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
		stage = primaryStage;
		try {
			if (HangmanStats.uploadThread.isAlive() == false) {
				HangmanStats.uploadThread.start();
			}

			// Dont check for updates if launched from launcher
			if (!disableUpdateChecks) {
				Thread updateThread = new Thread() {
					@Override
					public void run() {
						UpdateInfo update = UpdateChecker.isUpdateAvailable(Config.getUpdateRepoBaseURL(),
								Config.groupID, Config.artifactID, Config.updateFileClassifier);
						if (update.showAlert) {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									new UpdateAvailableDialog(update);
								}

							});
						}
					}
				};
				updateThread.setName("updateThread");
				updateThread.start();
			}

			Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"), bundle);

			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("MainWindow.css").toExternalForm());

			primaryStage.setTitle(bundle.getString("windowTitle"));

			primaryStage.setMinWidth(scene.getRoot().minWidth(0) + 70);
			primaryStage.setMinHeight(scene.getRoot().minHeight(0) + 70);

			primaryStage.setScene(scene);

			// Set Icon
			primaryStage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("icon.png")));

			primaryStage.show();
		} catch (Exception e) {
			log.getLogger().log(Level.SEVERE, "An error occurred", e);
		}
	}

	@Override
	public void stop() {
		shutDown();
	}

	/**
	 * Method is invoked by the FXML Loader after all variables have been
	 * injected.
	 */
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert actionLabel != null : "fx:id=\"actionLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert applyButton != null : "fx:id=\"applyButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert creditsButton != null : "fx:id=\"creditsButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert currentAppVersionTextLabel != null : "fx:id=\"currentAppVersionTextLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert currentSequence != null : "fx:id=\"currentSequence\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert getNextLetter != null : "fx:id=\"getNextLetter\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert languageSelector != null : "fx:id=\"languageSelector\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert loadLanguagesProgressBar != null : "fx:id=\"loadLanguagesProgressBar\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert newGameButton != null : "fx:id=\"newGameButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert proposedSolutions != null : "fx:id=\"proposedSolutions\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert result != null : "fx:id=\"result\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert shareThoughtsCheckbox != null : "fx:id=\"shareThoughtsCheckbox\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert thoughts != null : "fx:id=\"thoughts\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert updateLink != null : "fx:id=\"updateLink\" was not injected: check your FXML file 'MainWindow.fxml'.";
		assert versionLabel != null : "fx:id=\"versionLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";

		// Initialize your logic here: all @FXML variables will have been
		// injected

		// Initialize the language search field.
		new AutoCompleteComboBoxListener<String>(languageSelector);

		loadLanguageList();
		shareThoughtsCheckbox.setSelected(true);
		shareThoughtsBool = true;
		try {
			versionLabel.setText(new Version(Common.getAppVersion(), Common.getBuildNumber()).toString(false));
		} catch (IllegalArgumentException e) {
			versionLabel.setText(Common.UNKNOWN_APP_VERSION);
		}

		// Make update link invisible if launched from launcher
		if (disableUpdateChecks) {
			updateLink.setDisable(true);
			updateLink.setVisible(false);
		}

		// Listen for TextField text changes
		currentSequence.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				currentSequenceStr = currentSequence.getText();
				getNextLetter.setText(bundle.getString("computeNextLetterButtonLabel"));
			}
		});
	}

	/**
	 * This method launches the algorithm and writes its results into the gui.
	 */
	void launchAlgorithm() {
		MainWindow window = this;
		Thread algorithmThread = new Thread() {
			@Override
			public void run() {
				try {

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							languageSelector.setDisable(true);
							getNextLetter.setDisable(true);
							applyButton.setDisable(true);
							newGameButton.setDisable(true);
							currentSequence.setDisable(true);
							getNextLetter.setText(bundle.getString("computeNextLetterButton.waitForAlgorithmText"));
						}
					});

					currentSolution = HangmanSolver.solve(currentSequence.getText(), Language.getSupportedLanguages()
							.get(languageSelector.getSelectionModel().getSelectedIndex()));
					/*
					 * Platform.runLater(new Runnable() {
					 * 
					 * @Override public void run() {
					 * System.out.println("Setting resultText...");
					 * result.setText(currentSolution.result); } });
					 */

					String proposedSolutionsString = "";
					for (String solution : HangmanSolver.proposedSolutions) {
						proposedSolutionsString = proposedSolutionsString + solution + ", ";
					}

					// remove last ,
					proposedSolutionsString = proposedSolutionsString.substring(0,
							proposedSolutionsString.length() - 2);
					final String proposedSolutionsStringCopy = proposedSolutionsString;

					/*
					 * Platform.runLater(new Runnable() {
					 * 
					 * @Override public void run() {
					 * proposedSolutions.setText(proposedSolutionsStringCopy); }
					 * });
					 */

					if (currentSolution.gameState == GameState.GAME_LOST
							|| currentSolution.gameState == GameState.GAME_WON) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								GameEndDialog.show(bundle.getString("GameEndDialog.windowTitle"),
										currentSolution.gameState, window);
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// Update gui

								// next guess
								result.setText(currentSolution.result);

								// already proposed solutions
								proposedSolutions.setText(proposedSolutionsStringCopy);

								// thought
								String thoughtText = "";

								if (currentSolution.bestWordScore >= Config.thresholdToShowWord) {
									applyButton.setDisable(false);
									thoughtText = bundle.getString("thinkOfAWord")
											.replace("<percent>",
													Double.toString(Math.round(currentSolution.bestWordScore * 100)))
											.replace("<word>", currentSolution.bestWord);
								} else {
									applyButton.setDisable(true);
									thoughtText = bundle.getString("dontThinkAWord");
								}

								// Add the remeaning wrong guesses
								thoughtText = thoughtText + " " + bundle.getString("remeaningWrongGuesses")
										.replace("<number>", Integer.toString(
												Config.maxTurnCountToLoose - HangmanSolver.getWrongGuessCount()));

								setThought(thoughtText);

								// Update buttons etc only if everything else
								// succeeded
								getNextLetter.setText(bundle.getString("computeNextLetterButton.letterWrongText"));
								currentSequence.setDisable(false);

								// If the apply button is enabled, give it the
								// focus, else focus the current sequence
								if (!applyButton.isDisable()) {
									applyButton.requestFocus();
								} else {
									currentSequence.requestFocus();
								}
							}
						});
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// No language selected
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// NoLanguageSelected.show();
							Alert alert = new Alert(Alert.AlertType.ERROR,
									errorMessageBundle.getString("selectLanguage"));
							alert.show();
							// Replace button text with original string
							getNextLetter.setText(bundle.getString("computeNextLetterButtonLabel"));
							languageSelector.setDisable(false);
							currentSequence.setDisable(false);
							languageSelector.requestFocus();
						}
					});
				} catch (StringIndexOutOfBoundsException e2) {
					// No sequence entered
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// NoSequenceEntered.show();
							Alert alert = new Alert(Alert.AlertType.ERROR,
									errorMessageBundle.getString("enterWordSequence"));
							alert.show();
							// Replace button text with original string
							getNextLetter.setText(bundle.getString("computeNextLetterButtonLabel"));
							currentSequence.setDisable(false);
							currentSequence.requestFocus();
						}
					});
				} finally {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							getNextLetter.setDisable(false);
							newGameButton.setDisable(false);
						}
					});
				}
			}
		};
		algorithmThread.start();

	}

	/**
	 * Writes the last thought into the thoughts-label.
	 */
	public void setThought() {
		setThought(lastThought);
	}

	/**
	 * Writes the given thought into the thoughts-label. The last thought is
	 * remembered and can be recalled with {@code setThought()}.
	 * 
	 * @param thought
	 *            The thought to be written to the gui.
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
		MainWindow gui = this;
		Thread loadLangThread = new Thread() {
			@Override
			public void run() {

				ObservableList<String> items = FXCollections
						.observableArrayList(Language.getSupportedLanguages().getHumanReadableTranslatedNames(gui));

				languageSelector.setItems(items);
			}
		};

		loadLangThread.start();
	}

	// Status updates when loading languages

	@Override
	public void operationsStarted() {
		log.getLogger().info("Loading language list...");

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				languageSelector.setDisable(true);
				languageSelector.setPromptText(bundle.getString("languageSelector.waitText"));
				currentSequence.setDisable(true);
				getNextLetter.setDisable(true);
				result.setDisable(true);
				newGameButton.setDisable(true);

				loadLanguagesProgressBar.setPrefHeight(languageSelector.getHeight());
				loadLanguagesProgressBar.setVisible(true);
			}
		});
	}

	@Override
	public void progressChanged(double operationsDone, double totalOperationsToDo) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				loadLanguagesProgressBar.setProgress(operationsDone / totalOperationsToDo);
			}
		});
	}

	@Override
	public void operationsFinished() {
		log.getLogger().info("Languages loaded");

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				languageSelector.setDisable(false);
				languageSelector.setPromptText(bundle.getString("languageSelector.PromptText"));
				currentSequence.setDisable(false);
				getNextLetter.setDisable(false);
				result.setDisable(false);
				newGameButton.setDisable(false);
				loadLanguagesProgressBar.setVisible(false);

				languageSelector.requestFocus();
			}
		});
	}

	/**
	 * This method is executed before the app exits and executes several
	 * shutdown commands.<br>
	 * <b>IMPORTANT: This method does not quit the app, it just prepares the app
	 * for shutdown!</b>
	 */
	public static void shutDown() {
		try {
			log.getLogger().info("Shutting down....");
			// Maybe submit the current word
			submitWordOnQuit();
			HangmanStats.uploadThread.interrupt();
			HangmanStats.uploadThread.join();
			MongoSetup.close();
			log.getLogger().info("Good bye");
		} catch (InterruptedException e) {
			log.getLogger().log(Level.SEVERE, "An error occurred", e);
		}

	}

	/**
	 * Submits the current words when the user closes the app and the current
	 * sequence and the bestWord have a correlation bigger or equal than
	 * {@code Config.thresholdToSelectWord}.
	 */
	private static void submitWordOnQuit() {
		try {
			String[] words = currentSequenceStr.split(" ");

			for (String word : words) {
				if (word.length() == currentSolution.bestWord.length())
					if (TabFile.stringCorrelation(word, currentSolution.bestWord) >= Config
							.thresholdToSelectWord(word.length())) {
						HangmanStats.addWordToDatabase(currentSolution.bestWord, currentSolution.lang);
					}
			}
		} catch (NullPointerException e) {
			// Do nothing, no word entered
		}
	}
}
