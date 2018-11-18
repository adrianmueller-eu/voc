package de.noxafy.utils;

import java.util.List;

/**
 * @author noxafy
 * @created 25.10.18
 */
public class Log {

	private static LEVEL DEBUG = LEVEL.INFO;

	public static void info(String message) {
		System.out.println(message);
	}

	public static void debug(String debug_fmt_message, Object... objs) {
		log(LEVEL.DEBUG, debug_fmt_message, objs);
	}

	public static void verbose(String debug_fmt_message, Object... objs) {
		log(LEVEL.VERBOSE, debug_fmt_message, objs);
	}

	public static void verboseWithTab(String debug_fmt_message, Object... objs) {
		if (Log.isLevel(LEVEL.VERBOSE)) {
			System.out.println("\tVERBOSE: " + String.format(debug_fmt_message, objs));
		}
	}

	public static void verbose(List list) {
		if (!Log.isLevel(LEVEL.VERBOSE)) return;

		for (Object o : list) {
			verboseWithTab(o.toString());
		}
	}

	public static void error(String message) {
		System.err.println("ERROR: " + message);
	}

	public static void log(LEVEL level, String debug_fmt_message, Object... objs) {
		if (Log.isLevel(level)) {
			System.out.println(level.name() + ": " + String.format(debug_fmt_message, objs));
		}
	}

	public static void setLevel(LEVEL level) {
		DEBUG = level;
	}

	public static boolean isLevel(LEVEL level) {
		return DEBUG.is(level);
	}

	public enum LEVEL {
		INFO, DEBUG, VERBOSE;

		boolean is(LEVEL level) {
			return this.ordinal() >= level.ordinal();
		}
	}
}
