package de.noxafy.voc.core;

import de.noxafy.utils.Log;
import de.noxafy.voc.core.fileManager.SettingsFileManager;
import de.noxafy.voc.core.fileManager.VocabularyFileManager;
import de.noxafy.voc.core.model.Vocabulary;
import de.noxafy.voc.core.model.VocabularyBase;

/**
 * @author noxafy
 * @created 28.08.17
 */
public class AskingRoutine {
	private final SettingsFileManager settingsFileManager;
	private final VocabularyFileManager vocabularyFileManager;

	private final Settings settings;
	private final VocabularyBase vocabularyBase;

	private final UserInterface ui;

	public AskingRoutine(SettingsFileManager settingsFileManager, VocabularyFileManager vocabularyFileManager, UserInterface ui) {
		this.settingsFileManager = settingsFileManager;
		this.vocabularyFileManager = vocabularyFileManager;
		this.ui = ui;

		Log.debug("Loading settings from " + settingsFileManager.getFile().getAbsolutePath());
		settings = settingsFileManager.load();

		Log.debug("Loading vocabulary base from " + vocabularyFileManager.getFile().getAbsolutePath());
		long now = System.currentTimeMillis();
		vocabularyBase = vocabularyFileManager.load();
		Log.debug("Loaded " + vocabularyBase.size() + " vocs in " + (System.currentTimeMillis() - now) + " ms.");
	}

	public void run() {
		if (vocabularyBase.isEmpty()) {
			ui.noVocsFound();
			System.exit(1);
		}

		ui.init();
		vocabularyBase.generateTodo();
		if (vocabularyBase.isNothingTodo()) {
			Log.debug("No todo left for now!");
			if (ui.shouldReset()) {
				ui.bye();
				System.exit(0);
			}
		}
		vocabularyBase.generateVocsForToday(settings);

		while (vocabularyBase.hasNextVocabulary()) {
			Vocabulary next = vocabularyBase.getNextVocabulary();
			ask(next);
			vocabularyBase.update();
			writeOutChanges();
		}
	}

	private void ask(Vocabulary voc) {
		boolean askWord = Math.random() < 0.5;
		if (askWord) {
			ui.doAsk(voc.getWord());
			ui.waitForUserFinished();
			ui.showAnswer(voc.getMeaning(), voc.getMnemonic());
		}
		else {
			ui.doAsk(voc.getMeaning());
			ui.waitForUserFinished();
			ui.showAnswer(voc.getWord(), voc.getMnemonic());
		}
		int siar_before = voc.getSucceeded_in_a_row();
		if (ui.isCorrect()) {
			voc.succeeded();
			if (voc.isKnown()) {
				ui.praiseUser();
			}
		}
		else {
			voc.failed();
		}
		Log.log(Settings.TRAINING_MODE ? Log.Level.INFO : Log.Level.DEBUG, siar_before + " -> " + voc.getSucceeded_in_a_row());

		ui.prepareForNext();
	}

	public void summarize() {
		vocabularyBase.generateTodo();
		ui.summarize(vocabularyBase);
	}

	private void writeOutChanges() {
		vocabularyFileManager.write(vocabularyBase);
		settingsFileManager.write(settings);
	}
}
