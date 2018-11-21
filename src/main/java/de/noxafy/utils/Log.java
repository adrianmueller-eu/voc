package de.noxafy.utils;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author noxafy
 * @created 25.10.18
 */
public class Log {

	private static final Collection<Log> loggers = new LinkedList<>();
	private static Level LEVEL = Level.INFO;
	private static boolean prefixOn = true;

	private final PrintStream out;

	protected Log(PrintStream out) {
		this.out = out;
	}

	public static void info(String message) {
		log(Level.INFO, message);
	}

	public static void debug(String fmt_message, Object... objs) {
		log(Level.DEBUG, fmt_message, objs);
	}

	public static void verbose(String fmt_message, Object... objs) {
		log(Level.VERBOSE, fmt_message, objs);
	}

	public static void verboseWithTab(String fmt_message, Object... objs) {
		if (isLevel(Level.VERBOSE)) {
			log0(String.format("\t" + fmt_message, objs));
		}
	}

	public static void verbose(List list) {
		if (isLevel(Level.VERBOSE)) return;

		for (Object o : list) {
			verboseWithTab(o.toString());
		}
	}

	public static void error(String message) {
		log(Level.ERROR, message);
	}

	public static void log(Level level, String fmt_message, Object... objs) {
		if (isLevel(level)) {
			String prefix = prefixOn ? level.name() + ": " : "";
			String message = String.format(fmt_message, objs);
			log0(prefix + message);
		}
	}

	private static void log0(String message) {
		for (Log logger : loggers) {
			logger.out.println(message);
		}
	}

	public static void addLogger(PrintStream out) {
		if (out == null) throw new IllegalArgumentException("Please give a valid PrintStream for logging.");

		loggers.add(new Log(out));
		Log.debug("Added logger!");
	}

	public static void setLevel(Level level) {
		LEVEL = level;
	}

	public static boolean isLevel(Level level) {
		return LEVEL.is(level);
	}

	public static void setPrefix(boolean prefixOn) {
		Log.prefixOn = prefixOn;
	}

	public enum Level {
		ERROR, INFO, DEBUG, VERBOSE;

		boolean is(Level level) {
			return level.ordinal() <= this.ordinal();
		}
	}
}
