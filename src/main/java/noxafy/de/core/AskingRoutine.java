package noxafy.de.core;

import java.io.IOException;

import noxafy.de.fileManager.SettingsFileManager;
import noxafy.de.fileManager.VocabularyFileManager;
import noxafy.de.view.UserInterface;

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

		settings = settingsFileManager.load();
		vocabularyBase = vocabularyFileManager.load();
	}

	public void run() throws IOException {
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
		ui.letReadThat(ui.str.getFinal());
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
