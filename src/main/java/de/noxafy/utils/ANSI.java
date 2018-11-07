package de.noxafy.utils;

/**
 * @author noxafy
 * @created 20.01.18
 */
public class ANSI {

	private static final String escape = "\u001B";
	private static final String RESET = escape + "[0m";
	private static final String BOLD = escape + "[1m";
	private static final String TRANSPARENT = escape + "[2m";
	private static final String UNDERLINE = escape + "[4m";

	// public static final String CLS = "\u001Bc";
	public static final String CLEAR_WINDOW = escape + "[2J" + escape + "[H"; // on other terminals also [3J

	public static String bold(String bold) {
		return BOLD + bold + RESET;
	}

	public static String transparent(String transparent) {
		return TRANSPARENT + transparent + RESET;
	}

	public static String underline(String underline) {
		return UNDERLINE + underline + RESET;
	}

	public static String getShrinkWindow(String window_dimensions) {
		return escape + "[8;" + window_dimensions + "t";
	}
}
