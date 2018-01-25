package noxafy.de.view;

/**
 * @author noxafy
 * @created 20.01.18
 */
public class ANSI {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BOLD = "\u001B[1m";
	public static final String ANSI_TRANSPARENT = "\u001B[2m";

	public static String bold(String bold) {
		return ANSI_BOLD + bold + ANSI_RESET;
	}

	public static String transparent(String transparent) {
		return ANSI_TRANSPARENT + transparent + ANSI_RESET;
	}
}
