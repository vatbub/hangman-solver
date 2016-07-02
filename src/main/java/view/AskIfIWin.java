package view;

import java.io.IOException;

/**
 * Sample Skeleton for "askIfWin.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;

import common.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import view.SendReportQuestion.*;

/**
 * A dialog to ask the user if the computer has won the game. Will probably be removed when the win detector is introduced.
 * @author frede
 *
 */
public class AskIfIWin {
	
	private static Stage stage;
	private static String windowTitleCopy;
	private static ResourceBundle bundle = ResourceBundle.getBundle("view.strings.messages");

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML // fx:id="gameCancelledButton"
    private Button gameCancelledButton; // Value injected by FXMLLoader

    @FXML // fx:id="gameLostButton"
    private Button gameLostButton; // Value injected by FXMLLoader

    @FXML // fx:id="gameWonButton"
    private Button gameWonButton; // Value injected by FXMLLoader


    // Handler for Button[fx:id="gameCancelledButton"] onAction
    @FXML
    void gameCancelledButtonOnAction(ActionEvent event) {
        // handle the event here
    	hide();
    }

    // Handler for Button[fx:id="gameLostButton"] onAction
    @FXML
    void gameLostButtonOnAction(ActionEvent event) {
        // handle the event here
    	SendReportQuestion.show(windowTitleCopy, Config.iftttLooseEvent);
    	hide();
    }

    // Handler for Button[fx:id="gameWonButton"] onAction
    @FXML
    void gameWonButtonOnAction(ActionEvent event) {
        // handle the event here
    	SendReportQuestion.show(windowTitleCopy, Config.iftttWinEvent);
    	hide();
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        // Initialize your logic here: all @FXML variables will have been injected

    }
    
    public static void show(String windowTitle){
    	stage = new Stage();
    	windowTitleCopy = windowTitle;
    	Parent root;
		try {
			root = FXMLLoader.load(AskIfIWin.class.getResource("askIfWin.fxml"), bundle);
			Scene scene = new Scene(root);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setTitle(windowTitle);

			stage.setMinWidth(scene.getRoot().minWidth(0) + 18);
			stage.setMinHeight(scene.getRoot().minHeight(0) + 47);

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

