package de.noxafy.utils;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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

	/**
	 * Inform about current running processes (for people who <b>don't know</b> the code).
	 * The string will only be built if debug mode is activated.
	 */
	public static void verbose(String fmt_message, Object... objs) {
		log(Level.VERBOSE, fmt_message, objs);
	}

	/**
	 * Debug to get into the current state of given objects (for people who <b>know</b> the code).
	 * The string will only be built if debug mode is activated.
	 */
	public static void debug(String fmt_message, Object... objs) {
		log(Level.DEBUG, fmt_message, objs);
	}

	/**
	 * Debug with a preceding tab and without prefix.
	 * As in {@link #debug(String, Object...)} the string will only be built if debug mode is activated.
	 */
	public static void debugWithTab(String fmt_message, Object... objs) {
		if (isLevel(Level.DEBUG)) {
			log0(String.format("\t" + fmt_message, objs));
		}
	}

	/**
	 * Debug the current state of the list.
	 * As in {@link #debug(String, Object...)} the list will only be processed if debug mode is activated.
	 */
	public static void debug(List list) {
		if (!isLevel(Level.DEBUG)) return;

		for (Object o : list) {
			debugWithTab(o.toString());
		}
	}

	/**
	 * Program will exit after an error
	 */
	public static void error(String message) {
		log(Level.ERROR, message);
	}

	/**
	 * Program will continue running, but might be in an undesired state
	 */
	public static void warn(String message) {
		log(Level.WARNING, message);
	}

	public static void log(Level level, String fmt_message, Object... objs) {
		if (isLevel(level)) {
			String prefix = "";
			if (prefixOn) {
				if (isLevel(Level.VERBOSE)) {
					String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
					prefix = String.format("%s\t[%s]: ", level.name(), timeStamp);
				}
				else {
					prefix = String.format("%s: ", level.name());
				}
			}
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
		Log.debug("Logger added!");
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
		ERROR, WARNING, INFO, VERBOSE, DEBUG;

		boolean is(Level level) {
			return level.ordinal() <= this.ordinal();
		}
	}
}
