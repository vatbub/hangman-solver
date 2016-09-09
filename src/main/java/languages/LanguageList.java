package languages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import common.ProgressDialog;

public class LanguageList extends ArrayList<Language> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4080640769912336564L;

	public LanguageList() {
		super();
	}

	public LanguageList(int arg0) {
		super(arg0);
	}

	public LanguageList(Collection<? extends Language> arg0) {
		super(arg0);
	}

	public List<String> getHumanReadableNames() {
		return getHumanReadableNames(null);
	}

	public List<String> getHumanReadableNames(ProgressDialog gui) {
		List<String> res = new ArrayList<String>(this.size());

		if (gui != null) {
			gui.operationsStarted();
		}

		for (int i = 0; i < this.size(); i++) {
			res.add(this.get(i).getHumanReadableName());

			if (gui != null) {
				gui.progressChanged((double) i, (double) this.size());
			}
		}

		if (gui != null) {
			gui.operationsFinished();
		}

		return res;
	}

	public List<String> getHumanReadableTranslatedNames() {
		return getHumanReadableTranslatedNames(null);
	}

	public List<String> getHumanReadableTranslatedNames(ProgressDialog gui) {
		List<String> res = new ArrayList<String>(this.size());

		if (gui != null) {
			gui.operationsStarted();
		}

		for (int i = 0; i < this.size(); i++) {
			res.add(this.get(i).getHumanReadableTranslatedName());

			if (gui != null) {
				gui.progressChanged((double) i, (double) this.size());
			}
		}

		if (gui != null) {
			gui.operationsFinished();
		}

		return res;
	}

}