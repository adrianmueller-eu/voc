package noxafy.de.view;

/**
 * @author noxafy
 * @created 20.01.18
 */
public class ANSI {
	private static final String RESET = "\u001B[0m";
	private static final String BOLD = "\u001B[1m";
	private static final String TRANSPARENT = "\u001B[2m";
	private static final String UNDERLINE = "\u001B[4m";

	public static final String SHIRNK_WINDOW = "\u001B[8;4;60t";
	public static final String CLEAR_WINDOW = "\u001B[2J\u001B[H";

	public static String bold(String bold) {
		return BOLD + bold + RESET;
	}

	public static String transparent(String transparent) {
		return TRANSPARENT + transparent + RESET;
	}

	public static String underline(String underline) {
		return UNDERLINE + underline + RESET;
	}
}
