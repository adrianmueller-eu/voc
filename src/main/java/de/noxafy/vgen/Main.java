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
		Log.addLogger(System.out);
		Log.setPrefix(false);

		if (!parseArgs(args)) return;

		if (from == null || to == null) {
			if (to == null && from != null) {
				Log.error("Please give a csv where to write the generated items.");
			}
			else {
				Log.error("Please give a csv where to read the items from.");
			}
			return;
		}

		VocGenerator.generate(from, to);
	}

	private static boolean parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.charAt(0) == '-') {
				if (arg.length() > 1 && arg.charAt(1) == '-') {
					if ("--help".equals(arg)) {
						logHelpMessage();
						return false;
					}
				}
				for (int j = 1; j < arg.length(); j++) {
					switch (arg.charAt(j)) {
						case 'd':
							Log.setLevel(Log.Level.DEBUG);
							break;
						case 'f':
							from = evalFile(args, ++i);
							break;
						case 't':
							to = ensureFile(args, ++i);
							break;
						case 'h':
							logHelpMessage();
							return false;
						default:
							Log.error("Wrong argument: " + args[i] +
									// "\n" + usage +
									" -- See --help for more information.");
							return false;
					}
				}
			}
		}
		return true;
	}

	private static void logHelpMessage() {
		Log.info("Unfortunately help message is NIY.");
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
