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
import com.github.vatbub.common.view.core.ProgressDialog;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import languages.LanguageList;

@SuppressWarnings("FieldCanBeLocal")
public class LanguageSelectorSearchField implements ProgressDialog {
	
	private final ComboBox<String> comboBox;
	private final Parent parentNode;
	private LanguageList langList;
	private ProgressBar loadLanguagesProgressBar;

	public LanguageSelectorSearchField(ComboBox<String> comboBox, Parent parentNode, LanguageList langList) {
		this.comboBox = comboBox;
		this.parentNode = parentNode;
	}
	
	// Section to display load progress
	
		@Override
		public void operationsStarted() {
			FOKLogger.info(LanguageSelectorSearchField.class.getName(), "Loading language list...");
			LanguageSelectorSearchField me = this;

			Platform.runLater(() -> {
                comboBox.setDisable(true);
                //TODO Get String from resource bundle
                comboBox.setPromptText("Loading languages, please wait...");

                loadLanguagesProgressBar.setPrefHeight(comboBox.getHeight());


                loadLanguagesProgressBar.setVisible(true);
            });
		}

		@Override
		public void progressChanged(double operationsDone, double totalOperationsToDo) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void operationsFinished() {
			// TODO Auto-generated method stub
			
		}

}
