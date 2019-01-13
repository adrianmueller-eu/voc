package de.noxafy.vgen;

import de.noxafy.utils.Log;
import de.noxafy.utils.TooShortLineException;
import de.noxafy.voc.core.model.Vocabulary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author noxafy
 * @created 30.08.17
 */
final class VocGenerator {

	static void generate(File from, File to) {
		final LineWiseFileManager fileManager = new LineWiseFileManager(from);
		String[] lines = fileManager.load();
		String res;
		int success = 0;
		for (int i = 0; i < lines.length; i++) {
			// read voc from line
			Vocabulary voc;
			try {
				voc = getVoc(lines[i]);
			}
			catch (IllegalArgumentException e) {
				Log.warn("Failed to process line " + (i + 1) + ": " + lines[i]);
				Log.warn(e.toString());
				continue;
			}
			// parse voc to line
			res = getLine(voc);
			Log.info("Processing " + res);
			// add to destination file
			try {
				appendNewVoc(res, to);
			}
			catch (IOException e) {
				Log.error("Writing line [" + res + "] to " + to.getAbsolutePath() + " failed. Source (" + from.getAbsolutePath() + ") not " + "updated.");
				throw new RuntimeException(e);
			}
			// update source file
			lines[i] = "";
			fileManager.write(lines);

			success++;
		}
		Log.info(success + " new vocs added.");
	}

	private static Vocabulary getVoc(String org_line) throws IllegalArgumentException {
		String line = org_line.substring(1, org_line.length() - 1);
		String[] args = line.split("\",\"");

		String word, meaning, mnemonic = "";
		long added = 0, lastAsked = 0;
		int asked = 0, failed = 0, succeeded_in_a_row = 0;

		switch (args.length) {
			default:
			case 8:
				succeeded_in_a_row = parseInt("succeeded_in_a_row", args[7]);
				// fallthrough
			case 7:
				failed = parseInt("failed", args[6]);
				// fallthrough
			case 6:
				asked = parseInt("asked", args[5]);
				// fallthrough
			case 5:
				if (!args[4].isEmpty()) {
					lastAsked = parseLong("lastAsked", args[4]);
				}
				// fallthrough
			case 4:
				added = parseLong("added", args[3]);
				// fallthrough
			case 3:
				mnemonic = args[2];
				// fallthrough
			case 2:
				meaning = args[1];
				if (meaning.isEmpty()) {
					throw new IllegalArgumentException("Meaning field must not be empty.");
				}
				word = args[0];
				if (word.isEmpty()) {
					throw new IllegalArgumentException("Word field must not be empty.");
				}
				break;
			case 1:
			case 0:
				throw new TooShortLineException(org_line);
		}
		if (added == 0) added = System.currentTimeMillis();

		return new Vocabulary(word, meaning, mnemonic, added, lastAsked, asked, failed, succeeded_in_a_row);
	}

	private static String getLine(Vocabulary voc) {
		return quote(voc.getWord(), true) +
				quote(voc.getMeaning(), true) +
				quote(voc.getMnemonic(), true) +
				quote(voc.getAdded(), true) +
				quote((voc.getLastAsked() == 0) ? "" : voc.getLastAsked(), true) +
				quote(voc.getAsked(), true) +
				quote(voc.getFailed(), true) +
				quote(voc.getSucceeded_in_a_row(), false);
	}

	private static String quote(Object inner, boolean comma) {
		return "\"" + inner + ((comma) ? "\"," : "\"");
	}

	private static int parseInt(String type, String toParse) {
		int res;
		try {
			res = Integer.parseInt(toParse);
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Failed to parse \"" + type + "\": " + toParse);
		}
		return res;
	}

	private static long parseLong(String type, String toParse) {
		long res;
		try {
			res = Long.parseLong(toParse);
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Failed to parse \"" + type + "\": " + toParse);
		}
		return res;
	}

	private static void appendNewVoc(String line, File to) throws IOException {
		line += "\n";
		try (FileOutputStream out = new FileOutputStream(to, true)) {
			out.write(line.getBytes(StandardCharsets.UTF_8));
		}
	}
}
