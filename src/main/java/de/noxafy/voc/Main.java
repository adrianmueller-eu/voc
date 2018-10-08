package de.noxafy.voc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.noxafy.voc.core.AskingRoutine;
import de.noxafy.voc.core.Settings;
import de.noxafy.voc.fileManager.SettingsFileManager;
import de.noxafy.voc.fileManager.VocabularyFileManager;
import de.noxafy.voc.view.LANG;

import static de.noxafy.voc.view.ANSI.*;

public class Main {
	private static final String vok_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
	private static final File settings_file = new File(vok_dir + "voc.conf");
	private static File voc_file = new File(vok_dir + "Englisch.csv");
	private static boolean justSummarize = false;
	private static final String usage = "Usage: " + bold("voc") + " -h | [-n|-t] [-l " + underline("lang") + "] [-v|-d] [-s] [-f " + underline("csv") + "]";
	private static final String help = "Asks vocabularies based on a rating algorithm.\n" +
			usage + "\n" +
			"\t" + bold("-h") + "\tDisplays this message and exits.\n" +
			"\t" + bold("-n") + "\tOpens a new window and tests you from there in training mode.\n" +
			"\t" + "\tIn training mode the shell window shrinks to " + TRAINING_WINDOW_DIMENSIONS + " and clears the screen after each voc.\n" +
			"\t" + bold("-t") + "\tStarts training mode in this window (not recommended, use " + bold("-n") + ").\n" +
			"\t" + bold("-l") + "\tChoose an alternative interface language. Available: " + LANG.getAvailableString() + "\n" +
			"\t" + bold("-v") + "\tBe a bit verbose.\n" +
			"\t" + bold("-d") + "\tPrints very much debug information while asking. \n" +
			"\t" + "\tNot recommended in combination with " + bold("-n") + " or " + bold("-t") + ".\n" +
			"\t" + bold("-s") + "\tShows current statistics as shown after learned all vocs for a day and exits.\n" +
			"\t" + bold("-f") + "\tChoose an alternative csv file.\n" +
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
			"Further, a settings file is used (defaults to " + settings_file.getAbsolutePath() + ")\n" +
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
					Settings.DEBUG = Settings.DEBUG_LEVEL.SHORT;
					break;
				case "-d":
					Settings.DEBUG = Settings.DEBUG_LEVEL.LONG;
					break;
				case "-t":
					Settings.TRAINING_MODE = true;
					break;
				case "-n":
					try {
						args[i] = ""; // delete -n argument (prevent circularity)
						final String[] cmd = { "osascript", "-e",
								"tell application \"Terminal\" to do script \"voc -t " + String.join(" ", args) + "\""
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
