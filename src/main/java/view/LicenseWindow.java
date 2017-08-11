package view;

/*-
 * #%L
 * Hangman Solver
 * %%
 * Copyright (C) 2016 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.vatbub.common.core.logging.FOKLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * A window diaplying a dialog with some licensing info.
 *
 * @author frede
 */
public class LicenseWindow {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    /**
     * Shows the dialog.
     *
     * @param windowTitle The window title to set
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
            FOKLogger.log(LicenseWindow.class.getName(), Level.SEVERE, "An error occurred", e);
        }
    }

    @FXML
    void initialize() {
    }

}
