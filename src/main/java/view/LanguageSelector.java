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


import common.ProgressDialog;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import languages.LanguageList;
import logging.FOKLogger;

public class LanguageSelector extends ComboBox<String> implements ProgressDialog{
	
	private LanguageList langList;
	private ProgressBar loadLanguagesProgressBar;
	private static FOKLogger log = new FOKLogger(LanguageSelector.class.getName());

	public LanguageSelector() {
		super();
	}

	public LanguageSelector(ObservableList<String> arg0) {
		super(arg0);
	}
	
	public LanguageSelector(LanguageList langList){
		super();
		setLanguageList(langList);
	}
	
	public void setLanguageList(LanguageList langList){
		this.langList = langList;
		//this.setItems(new Observable);
	}

	// Section to display load progress
	
	@Override
	public void operationsStarted() {
		log.getLogger().info("Loading language list...");
		LanguageSelector me = this;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				me.setDisable(true);
				//TODO Get String from resource bundle
				me.setPromptText("Loading languages, please wait...");

				loadLanguagesProgressBar.setPrefHeight(me.getHeight());
				me.getParent();
				
				loadLanguagesProgressBar.setVisible(true);
			}
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
