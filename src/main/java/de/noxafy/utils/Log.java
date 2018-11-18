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
		for (Log logger : loggers) {
			logger.out.print("\t");
		}
		log(Level.VERBOSE, fmt_message, objs);
	}

	public static void verbose(List list) {
		if (!Log.isLevel(Level.VERBOSE)) return;

		for (Object o : list) {
			verboseWithTab(o.toString());
		}
	}

	public static void error(String message) {
		log(Level.ERROR, message);
	}

	public static void log(Level level, String fmt_message, Object... objs) {
		if (Log.isLevel(level)) {
			String mes = level.name() + ": " + String.format(fmt_message, objs);
			for (Log logger : loggers) {
				logger.out.println(mes);
			}
		}
	}

	public static void addLogger(PrintStream out) {
		if (out == null) throw new IllegalArgumentException("Please give a valid PrintStream for logging.");

		loggers.add(new Log(out));
	}

	public static void setLevel(Level level) {
		LEVEL = level;
	}

	public static boolean isLevel(Level level) {
		return LEVEL.is(level);
	}

	public enum Level {
		ERROR, INFO, DEBUG, VERBOSE;

		boolean is(Level level) {
			return this.ordinal() >= level.ordinal();
		}
	}
}
