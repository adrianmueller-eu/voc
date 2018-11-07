package de.noxafy.utils;

import java.util.List;

/**
 * @author noxafy
 * @created 25.10.18
 */
public class Log {

	private static DEBUG_LEVEL DEBUG = DEBUG_LEVEL.NONE;

	public static void info(String message) {
		System.out.println(message);
	}

	public static void debug(String debug_message) {
		debug(DEBUG_LEVEL.SHORT, debug_message);
	}

	public static void debug(DEBUG_LEVEL level, String debug_fmt_message, Object... objs) {
		if (Log.isDebugLevel(level)) {
			System.out.println("DEBUG: " + String.format(debug_fmt_message, objs));
		}
	}

	public static void debugWithTab(String debug_fmt_message, Object... objs) {
		if (Log.isDebugLevel(DEBUG_LEVEL.LONG)) {
			System.out.println("\tDEBUG: " + String.format(debug_fmt_message, objs));
		}
	}

	public static void debug(List list) {
		if (Log.isDebugLevel(DEBUG_LEVEL.LONG)) return;

		for (Object o : list) {
			debugWithTab(o.toString());
		}
	}

	public static void error(String message) {
		System.err.println("ERROR: " + message);
	}

	public static void setDebugLevel(DEBUG_LEVEL level) {
		DEBUG = level;
	}

	public static boolean isDebugLevel(DEBUG_LEVEL level) {
		return DEBUG.is(level);
	}

	public enum DEBUG_LEVEL {
		NONE, SHORT, LONG;

		boolean is(DEBUG_LEVEL level) {
			return this.ordinal() >= level.ordinal();
		}
	}
}
