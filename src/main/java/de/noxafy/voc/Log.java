package de.noxafy.voc;

import de.noxafy.voc.core.Settings;

import java.util.List;

/**
 * @author noxafy
 * @created 25.10.18
 */
public class Log {

	public static void info(String message) {
		System.out.println(message);
	}

	public static void debug(String debug_message) {
		debug(Settings.DEBUG_LEVEL.SHORT, debug_message);
	}

	public static void debug(Settings.DEBUG_LEVEL level, String debug_fmt_message, Object... objs) {
		if (Settings.DEBUG.is(level)) {
			System.out.println("DEBUG: " + String.format(debug_fmt_message, objs));
		}
	}

	public static void debugWithTab(String debug_fmt_message, Object... objs) {
		if (Settings.DEBUG.is(Settings.DEBUG_LEVEL.LONG)) {
			System.out.println("\tDEBUG: " + String.format(debug_fmt_message, objs));
		}
	}

	public static void debug(List list) {
		if (Settings.DEBUG.is(Settings.DEBUG_LEVEL.LONG)) return;

		for (Object o : list) {
			debugWithTab(o.toString());
		}
	}

	public static void error(String message) {
		System.err.println("ERROR: " + message);
	}
}
