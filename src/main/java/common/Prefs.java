package common;

/*
 * #%L
 * examTrainer
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


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Prefs {
	private Properties props = new Properties();
	private File f;

	public Prefs(String className) {
		// Retrieve the user preference node for the package com.mycompany
		f = new File(Config.getAppDataPath() + className + ".properties");

		try {
			if (f.exists()) {
				// Load the properties
				props.load(new FileReader(f));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPreference(String prefKey, String prefValue) {
		props.setProperty(prefKey, prefValue);
		savePreferences();
	}

	public String getPreference(String prefKey, String defaultValue) {
		return props.getProperty(prefKey, defaultValue);
	}
	
	public void savePreferences(){
        try {
        	System.out.println("Saving preference file as: " + f.getAbsolutePath());
        	FileOutputStream out = new FileOutputStream( f );
			props.store(out, "This is an optional header comment string");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
