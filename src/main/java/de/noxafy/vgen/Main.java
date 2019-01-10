package de.noxafy.vgen;

import de.noxafy.utils.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author noxafy
 * @created 26.10.18
 */
public class Main {

	private static File from = null;
	private static File to = null;

	public static void main(String[] args) {
		if (!parseArgs(args)) return;

		Log.addLogger(System.out);
		Log.setPrefix(false);

		if (from == null || to == null) {
			if (to == null && from != null) {
				Log.error("Please give a csv where to write the generated items.");
				return;
			}
			else if (to != null) {
				Log.error("Please give a csv where to read the items from.");
				return;
			}
			final String voc_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
			from = new File(voc_dir + "vocs");
			to = new File(voc_dir + "Englisch.csv");
		}

		VocGenerator.generate(from, to);
	}

	private static boolean parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-d":
					Log.setLevel(Log.Level.DEBUG);
					break;
				case "-f":
					from = evalFile(args, ++i);
					break;
				case "-t":
					to = ensureFile(args, ++i);
					break;
				//				case "-h":
				//				case "--help":
			}
		}
		return true;
	}

	private static File evalFile(String[] args, int i) {
		if (i < args.length) {
			File file = new File(args[i]);
			if (!file.exists()) {
				Log.error("Please give an existing file to a csv with vocs.");
				System.exit(1);
			}
			else if (!file.canRead()) {
				Log.error("Please give a readable file to a csv with vocs. See --help for more information.");
				System.exit(1);
			}
			else if (!file.canWrite()) {
				Log.error("Please give a writable file to a csv with vocs. See --help for more information.");
				System.exit(1);
			}
			return file;
		}
		else {
			Log.error("Please give a path to a csv with vocs. See --help for more information.");
			System.exit(1);
			return null;
		}
	}

	private static File ensureFile(String[] args, int i) {
		if (i < args.length) {
			File file = new File(args[i]);
			try {
				file.createNewFile();
				return file;
			}
			catch (IOException e) {
				throw new IllegalArgumentException("File creation failed: " + file.getAbsolutePath());
			}
		}
		else {
			Log.error("Please give a path to a csv with vocs. See --help for more information.");
			System.exit(1);
		}
		return null;
	}
}
