package de.noxafy.voc;

import java.io.File;

import de.noxafy.voc.core.AskingRoutine;
import de.noxafy.voc.core.Settings;
import de.noxafy.voc.fileManager.SettingsFileManager;
import de.noxafy.voc.fileManager.VocabularyFileManager;
import de.noxafy.voc.view.UserInterface;
import de.noxafy.voc.view.commandline.CLArgsParser;
import de.noxafy.voc.view.commandline.CLUserInterface;

public class Main {
	private static final String vok_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
	private static final File settings_file = new File(vok_dir + "voc.conf");
	private static final File voc_file = new File(vok_dir + "Englisch.csv");

	public static void main(String[] args) {
		// parse args
		if (args.length > 0) {
			CLArgsParser.parse(args, voc_file.getAbsolutePath(), settings_file.getAbsolutePath());
		}

		// load settings file
		final SettingsFileManager settingsFileManager = SettingsFileManager.getInstance(settings_file.getAbsolutePath());
		// load voc file
		final VocabularyFileManager vocabularyFileManager = VocabularyFileManager.getInstance(voc_file.getAbsolutePath());
		// UI
		final UserInterface ui = new CLUserInterface();

		// start voc routine
		final AskingRoutine askingRoutine = new AskingRoutine(settingsFileManager, vocabularyFileManager, ui);
		if (!Settings.justSummarize) {
			askingRoutine.run();
		}
		askingRoutine.summarize();
	}
}
