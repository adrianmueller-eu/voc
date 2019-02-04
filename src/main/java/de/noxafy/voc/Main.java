package de.noxafy.voc;

import de.noxafy.utils.Log;
import de.noxafy.voc.cli.CLArgsParser;
import de.noxafy.voc.cli.CLUserInterface;
import de.noxafy.voc.core.AskingRoutine;
import de.noxafy.voc.core.Settings;
import de.noxafy.voc.core.UserInterface;
import de.noxafy.voc.core.fileManager.SettingsFileManager;
import de.noxafy.voc.core.fileManager.VocabularyFileManager;

import java.io.File;

public class Main {

	public static File voc_file;
	public static final File settings_file = new File(System.getProperty("user.home") + "/.voc.conf");

	public static void main(String[] args) {
		// parse args
		// TODO: -a     Add voc as in voc2
		// TODO:        -> database for unknowns needed
		// TODO: -s     Search voc as in voc2
		CLArgsParser.parse(args);

		Log.addLogger(System.out);

		// load settings file
		// TODO: voc_file as part of settings
		// TODO: ask for file if no is set in config
		// TODO: default folder ~/.voc/ for csv's
		// TODO: automatic database, just specify folder (but also with default)
		final SettingsFileManager settingsFileManager = SettingsFileManager.getInstance(settings_file.getAbsolutePath());
		// load voc file
		final VocabularyFileManager vocabularyFileManager = VocabularyFileManager.getInstance(voc_file.getAbsolutePath());
		// UI
		final UserInterface ui = new CLUserInterface();

		// start voc routine
		// TODO: undo
		final AskingRoutine askingRoutine = new AskingRoutine(settingsFileManager, vocabularyFileManager, ui);
		if (!Settings.justSummarize) {
			askingRoutine.run();
		}
		askingRoutine.summarize();
	}
}
