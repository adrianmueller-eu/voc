package de.noxafy.voc;

import java.util.List;

import de.noxafy.voc.core.Settings;

/**
 * @author noxafy
 * @created 25.10.18
 */
public class Log {
	public static void debug(String debug_message) {
		debug(debug_message, Settings.DEBUG_LEVEL.SHORT);
	}

	public static void debug(String debug_message, Settings.DEBUG_LEVEL level) {
		if (Settings.DEBUG.is(level)) {
			System.out.println("DEBUG: " + debug_message);
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
