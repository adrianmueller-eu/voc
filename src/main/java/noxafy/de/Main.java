package noxafy.de;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import noxafy.de.core.AskingRoutine;
import noxafy.de.core.Settings;
import noxafy.de.fileManager.SettingsFileManager;
import noxafy.de.fileManager.VocabularyFileManager;
import noxafy.de.view.ANSI;
import noxafy.de.view.LANG;

import static noxafy.de.view.ANSI.bold;
import static noxafy.de.view.ANSI.underline;

public class Main {
	static final String vok_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
	static final File settings_file = new File(vok_dir + "voc.conf");
	static File voc_file = new File(vok_dir + "Englisch.csv");
	static boolean justSummarize = false;
	static String usage = "Usage: " + bold("voc2") + " -h | [-d] [-n|-t] [-l " + underline("lang") + "] [-f " + underline("csv") + "] | -s";
	static String help = "Asks vocabularies based on a rating algorithm.\n" +
			usage + "\n" +
			"\t" + bold("-h") + "\tDisplays this message and exits.\n" +
			"\t" + bold("-d") + "\tPrints very much debug information while asking (also " + bold("-v") + "). \n" +
			"\t" + "\tNot recommended in combination with " + bold("-n") + " or " + bold("-t") + ".\n" +
			"\t" + bold("-n") + "\tOpens a new window and tests you from there in training mode.\n" +
			"\t" + "\tIn training mode the shell window shrinks to " + ANSI.TRAINING_WINDOW_DIMENSIONS + " and clears the screen after each voc.\n" +
			"\t" + bold("-t") + "\tStarts training mode in this window (not recommended, use " + bold("-n") + ").\n" +
			"\t" + bold("-l") + "\tChoose an alternative interface language. Available: " + LANG.getAvailableString() + "\n" +
			"\t" + bold("-f") + "\tChoose an alternative csv file.\n" +
			"\t" + bold("-s") + "\tShows current statistics as shown after learned all vocs for a day and exits.\n" +
			"\n" +
			"Source of vocabularies is " + underline("csv") + " (defaults to " + voc_file.getAbsolutePath() + ").\n" +
			"Each entry there contains following information:\n" +
			"\t1. word\n" +
			"\t2. meaning\n" +
			"\t3. further information for memorizing or pronouncing (mnemonic)\n" +
			"\t4. date when voc was added\n" +
			"\t5. date when voc was last asked\n" +
			"\t6. how often it has been asked\n" +
			"\t7. how often the user failed to answer\n" +
			"\t8. how often user succeeded in a row\n" +
			"The last four stats are used by rating calculation.\n" +
			"\n" +
			"Further it is using the settings file located in " + settings_file.getAbsolutePath() + "\n" +
			"There is defined:\n" +
			"\t- how many vocs should be asked each day (NUMBER_SIMUL_VOCS) and\n" +
			"\t- how many of them should be new ones (NUMBER_NEW_VOCS_AT_START).";

	public static void main(String[] args) throws IOException {
		// Print newline after sigint
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("")));

		try {
			parse(args);
		}
		catch (IllegalArgumentException e) {
			String message = e.getMessage();
			int exitCode = 1;

			if ("help".equals(message)) {
				message = help;
				exitCode = 0;
			}

			System.out.print(message);
			System.exit(exitCode);
		}

		// load settings file
		SettingsFileManager settingsFileManager = SettingsFileManager.getInstance(settings_file.getAbsolutePath());

		// load voc file
		VocabularyFileManager vocabularyFileManager = VocabularyFileManager.getInstance(voc_file.getAbsolutePath());

		// start voc routine
		AskingRoutine askingRoutine = new AskingRoutine(settingsFileManager, vocabularyFileManager);
		if (!justSummarize) {
			askingRoutine.run();
		}
		askingRoutine.summarize();
	}

	private static void parse(String[] args) throws IllegalArgumentException {
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "--help":
					throw new IllegalArgumentException("help");
				case "-s":
					justSummarize = true;
					break;
				case "-v":
				case "-d":
					Settings.DEBUG = true;
					break;
				case "-t":
					Settings.TRAINING_MODE = true;
					break;
				case "-n":
					try {
						args[i] = ""; // delete -n argument (prevent circularity)
						final String[] cmd = { "osascript", "-e",
								"tell application \"Terminal\" to do script \"voc2 -t " + String.join(" ", args) + "\""
						};
						final Process p = Runtime.getRuntime().exec(cmd);
						// print out possible error
						InputStream in = p.getErrorStream();
						int c;
						while ((c = in.read()) != -1) {
							System.out.print((char) c);
						}
						in.close();
						// exit with osascript's exit code
						System.exit(p.waitFor());
					}
					catch (IOException | InterruptedException e) {
						throw new IllegalArgumentException("New window not available in this mode");
					}
				case "-l":
					evalLang(args, ++i);
					break;
				case "-f":
					evalFile(args, ++i);
					break;
				default:
//					if (args[i].matches("-*")) {
					throw new IllegalArgumentException("Wrong argument: " + args[i] + "\n" +
							usage + " -- See -h for more help.");
//					}
			}
		}
	}

	private static void evalLang(String[] args, int i) throws IllegalArgumentException {
		if (i < args.length) {
			final LANG lang = LANG.get(args[i].toUpperCase());
			if (lang == null) {
				throw new IllegalArgumentException("Please give an available language. Available: " + LANG.getAvailableString() + ".  See -h for more help.");
			}
			else {
				Settings.LANG = lang;
			}
		}
		else {
			throw new IllegalArgumentException("Please give a language. Available: " + LANG.getAvailableString() + ".  See -h for more help.");
		}
	}

	private static void evalFile(String[] args, int i) throws IllegalArgumentException {
		if (i < args.length) {
			voc_file = new File(args[i]);
			if (!voc_file.exists()) {
				throw new IllegalArgumentException("Please give an existing file to a csv with vocs.");
			}
			else if (!voc_file.canRead() || !voc_file.canWrite()) {
				throw new IllegalArgumentException("Please give a read- and writable file to a csv with vocs. See -h for more information.");
			}
		}
		else {
			throw new IllegalArgumentException("Please give a path to a csv with vocs. See -h for more information.");
		}
	}
}
