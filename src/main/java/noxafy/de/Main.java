package noxafy.de;

import java.io.File;
import java.io.IOException;

import noxafy.de.core.AskingRoutine;
import noxafy.de.core.Settings;
import noxafy.de.fileManager.SettingsFileManager;
import noxafy.de.fileManager.VocabularyFileManager;

import static noxafy.de.view.ANSI.bold;

public class Main {

	public static void main(String[] args) throws IOException {
		// Print newline after sigint
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("")));

		final String vok_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
		final File settings_file = new File(vok_dir + "voc.conf");
		final File voc_file = new File(vok_dir + "Englisch.csv");

		boolean justSummarize = false;
		if (args.length > 0) {
			switch (args[0]) {
				case "-d":
					Settings.DEBUG = true;
					break;
				case "-s":
					justSummarize = true;
					break;
				case "-h":
				case "--help":
					System.out.print("Asks vocabularies two-way and based on a rating algorithm.\n" +
							"Usage: " + bold("voc2") + " [ -h | -s | -d ]\n" +
							"\t" + bold("-h") + "\tDisplay this message and exit.\n" +
							"\t" + bold("-s") + "\tShow current statistics as shown after learned all vocs for a day.\n" +
							"\t" + bold("-d") + "\tVerbose very much debug information while asking.\n" +
							"\n" +
							"Source of vocabularies is " + voc_file.getAbsolutePath() + ".\n" +
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
							"\t- how many of them are new ones (NUMBER_NEW_VOCS_AT_START).");
					return;
			}
		}

		// load settings file
		SettingsFileManager settingsFileManager = SettingsFileManager.getInstance(settings_file);

		// load voc file
		VocabularyFileManager vocabularyFileManager = VocabularyFileManager.getInstance(voc_file);

		// start voc routine
		AskingRoutine askingRoutine = new AskingRoutine(settingsFileManager, vocabularyFileManager);
		if (!justSummarize) {
			askingRoutine.run();
		}
		askingRoutine.summarize();
	}
}
