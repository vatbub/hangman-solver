package view;

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
