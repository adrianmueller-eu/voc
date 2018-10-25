package de.noxafy.voc.view;

import de.noxafy.voc.core.VocabularyBase;

/**
 * @author noxafy
 * @created 25.10.18
 */
public interface UserInterface {
	void init();

	boolean shouldReset();

	void doAsk(String word);

	void showAnswer(String word, String mnemonic);

	int waitForUserFinished();

	boolean isCorrect();

	void prepareForNext();

	void noVocsFound();

	void praiseUser();

	void summarize(VocabularyBase base);

	void bye();

}
