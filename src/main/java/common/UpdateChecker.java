package common;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

public class UpdateChecker {
	
	private static String latestSeenVersionPrefKey = "updates.latestVersionOnWebsite";
	private static Prefs updatePrefs = new Prefs(UpdateChecker.class.getName());

	/**
	 * Checks if a new release has been published on the website. This does not compare the current app version to the release version on the website, just checks if something happened on the website.
	 * @return {@code true} if a new release is available.
	 */
	public static boolean isUpdateAvailable(){
		String savedSetting = updatePrefs.getPreference(latestSeenVersionPrefKey, "");
		boolean res = false;
		
		if (savedSetting.equals("")){
			// Never checked for updates before
			res=false;
		}
		
		return res;
	}
	
	private static String getLatestReleaseNameFromGithub(){
		
		
		
		JsonReaderFactory factory = Json.createReaderFactory(null);
		//JsonReader reader1 = factory.createReader(null);
		
		return null;
	}

}
