package view;

import common.ProgressDialog;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import languages.LanguageList;
import logging.FOKLogger;

public class LanguageSelectorSearchField implements ProgressDialog{
	
	private ComboBox<String> comboBox;
	private Parent parentNode;
	private LanguageList langList;
	private ProgressBar loadLanguagesProgressBar;
	private static FOKLogger log = new FOKLogger(LanguageSelectorSearchField.class.getName());

	public LanguageSelectorSearchField(ComboBox<String> comboBox, Parent parentNode, LanguageList langList) {
		this.comboBox = comboBox;
		this.parentNode = parentNode;
	}
	
	// Section to display load progress
	
		@Override
		public void operationsStarted() {
			log.getLogger().info("Loading language list...");
			LanguageSelectorSearchField me = this;

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					comboBox.setDisable(true);
					//TODO Get String from resource bundle
					comboBox.setPromptText("Loading languages, please wait...");

					loadLanguagesProgressBar.setPrefHeight(comboBox.getHeight());
					
					
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
