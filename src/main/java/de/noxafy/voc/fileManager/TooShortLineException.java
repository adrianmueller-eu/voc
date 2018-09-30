package de.noxafy.voc.fileManager;

/**
 * @author noxafy
 * @created 01.02.18
 */
public class TooShortLineException extends RuntimeException {
	TooShortLineException(String line) {
		super("Line has not enough arguments or is bad formatted: " + line);
	}
}
