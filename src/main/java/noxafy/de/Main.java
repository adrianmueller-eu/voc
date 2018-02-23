package noxafy.de;

import java.io.File;
import java.io.IOException;

import noxafy.de.core.AskingRoutine;
import noxafy.de.core.Settings;
import noxafy.de.fileManager.SettingsFileManager;
import noxafy.de.fileManager.VocabularyFileManager;

import static noxafy.de.view.ANSI.bold;
import static noxafy.de.view.ANSI.underline;

public class Main {
	static final String vok_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
	static final File settings_file = new File(vok_dir + "voc.conf");
	static File voc_file = new File(vok_dir + "Englisch.csv");
	static boolean justSummarize = false;
	static String usage = "Usage: " + bold("voc2") + " -h | -s | [-d] [-l " + underline("lang") + "] [-f " + underline("csv") + "]";
	static String help = "Asks vocabularies two-way and based on a rating algorithm.\n" +
			usage + "\n" +
			"\t" + bold("-h") + "\tDisplays this message and exits.\n" +
			"\t" + bold("-s") + "\tShows current statistics as shown after learned all vocs for a day and exists.\n" +
			"\t" + bold("-d") + "\tVerboses very much debug information while asking.\n" +
			"\t" + bold("-t") + "\tShrinks the shell window to 4;60 and clears the screen after each voc.\n" +
			"\t" + bold("-l") + "\tChoose an alternative language. Available: \"de\", \"en\"\n" +
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
			"Further it is using the settings file located in " + settings_file.getAbsolutePath() + "\n" +
			"There is defined:\n" +
			"\t- how many vocs should be asked each day (NUMBER_SIMUL_VOCS) and\n" +
			"\t- how many of them should be new ones (NUMBER_NEW_VOCS_AT_START).";

	public static void main(String[] args) throws IOException {
		// Print newline after sigint
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("")));

		if (!parseArgs(args)) return;

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

	private static boolean parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "--help":
					System.out.print(help);
					return false;
				case "-s":
					justSummarize = true;
					break;
				case "-d":
					Settings.DEBUG = true;
					break;
				case "-t":
					Settings.TRAINING_MODE = true;
					break;
				case "-l":
					evalLang(args, ++i);
					break;
				case "-f":
					evalFile(args, ++i);
					break;
				default:
					if (args[i].matches("-*")) {
						System.out.println("Wrong argument: " + args[i]);
						System.out.print(usage + " -- See -h for more help.");
						System.exit(1);
					}
			}
		}
		return true;
	}

	private static void evalLang(String[] args, int i) {
		if (i < args.length) {
			switch (args[i]) {
				case "de":
					Settings.LANG = "de";
					break;
				case "en":
					Settings.LANG = "en";
					break;
				default:
					System.out.print("Please give an available language. Available: \"de\", \"en\".  See -h for more help.");
					System.exit(1);
			}
		}
		else {
			System.out.print("Please give a language. Available: \"de\", \"en\".  See -h for more help.");
			System.exit(1);
		}
	}

	private static void evalFile(String[] args, int i) {
		if (i < args.length) {
			voc_file = new File(args[i]);
			if (!voc_file.exists()) {
				System.out.print("Please give an existing file to a csv with vocs.");
				System.exit(1);
			}
			else if (!voc_file.canRead()) {
				System.out.print("Please give a readable file to a csv with vocs. See -h for more information.");
				System.exit(1);
			}
			else if (!voc_file.canWrite()) {
				System.out.print("Please give a writable file to a csv with vocs. See -h for more information.");
				System.exit(1);
			}
		}
		else {
			System.out.print("Please give a path to a csv with vocs. See -h for more information.");
			System.exit(1);
		}
	}
}
