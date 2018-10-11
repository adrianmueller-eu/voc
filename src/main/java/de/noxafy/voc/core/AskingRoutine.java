package de.noxafy.voc.core;

import java.io.IOException;

import de.noxafy.voc.fileManager.SettingsFileManager;
import de.noxafy.voc.fileManager.VocabularyFileManager;
import de.noxafy.voc.view.UserInterface;

/**
 * @author noxafy
 * @created 28.08.17
 */
public class AskingRoutine {
	private final SettingsFileManager settingsFileManager;
	private final VocabularyFileManager vocabularyFileManager;

	private final Settings settings;
	private final VocabularyBase vocabularyBase;

	private final UserInterface ui = UserInterface.getInstance();

	public AskingRoutine(SettingsFileManager settingsFileManager, VocabularyFileManager vocabularyFileManager) {
		this.settingsFileManager = settingsFileManager;
		this.vocabularyFileManager = vocabularyFileManager;

		ui.debug("Loading settings from " + settingsFileManager.getFilePath());
		settings = settingsFileManager.load();
		// Check for enough learned should conceptually be done before voc load
		// But for statistics only it's not needed, even not desired. So it will be checked on routine run.

		ui.debug("Loading vocabulary base from " + vocabularyFileManager.getFilePath());
		long now = System.currentTimeMillis();
		vocabularyBase = vocabularyFileManager.load();
		ui.debug("Loaded " + vocabularyBase.size() + " vocs in " + (System.currentTimeMillis() - now) + " ms.");
	}

	public void run() throws IOException {
		checkIfEnoughLearned(settingsFileManager);

		if (vocabularyBase.isEmpty()) {
			ui.tellLn(ui.str.getNoVocFound());
			System.exit(1);
		}
		vocabularyBase.generateVocsForToday(settings);

		while (vocabularyBase.hasNextVocabulary()) {
			Vocabulary next = vocabularyBase.getNextVocabulary();
			ui.ask(next);
			vocabularyBase.update(settings);
			writeOutChanges();
		}
		ui.tellLn(ui.str.getFinal());
	}

	private void checkIfEnoughLearned(SettingsFileManager settingsFileManager) throws IOException {
		// test if enough is learned for today
		ui.debug("Already " + settings.vocs_learned_today + " vocs learned today.");
		if (settings.allDone()) {
			if (ui.getAnswer(ui.str.getFinalAndReset() + " (y/n) [y]: ", true)) {
				settings.resetAllLearned();
				settingsFileManager.write(settings);
				ui.debug("Reset Settings.vocs_learned_today to 0.");
			}
			else {
				ui.tell(ui.str.comeTomorrow());
				System.exit(0);
			}
		}
	}

	public void summarize() {
		vocabularyBase.generateTodo();
		vocabularyBase.summarize();
	}

	private void writeOutChanges() throws IOException {
		vocabularyFileManager.write(vocabularyBase);
		settingsFileManager.write(settings);
	}
}
