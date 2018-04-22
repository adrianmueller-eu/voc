package noxafy.de.view;

import noxafy.de.core.Settings;

/**
 * @author noxafy
 * @created 22.04.18
 */
public enum LANG {
	DE(new Strings_de()), EN(new Strings_en());

	private final Strings strings;

	LANG(Strings strings) {
		this.strings = strings;
	}

	public static LANG get(String lang) {
		try {
			return valueOf(lang);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static String getAvailable() {
		final StringBuilder res = new StringBuilder();
		for (LANG lang : values()) {
			res.append("\"").append(lang.name().toLowerCase()).append("\"");
			if (lang == Settings.LANG) {
				res.append("(default)");
			}
			res.append(",");
		}
		if (res.length() > 0) {
			res.deleteCharAt(res.length() - 1);
		}
		return res.toString();
	}

	public Strings getStrings() {
		return strings;
	}
}