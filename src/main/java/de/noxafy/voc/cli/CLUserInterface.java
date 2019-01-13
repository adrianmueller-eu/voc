package de.noxafy.voc.cli;

import de.noxafy.utils.ANSI;
import de.noxafy.utils.Log;
import de.noxafy.voc.cli.lang.Strings;
import de.noxafy.voc.core.Settings;
import de.noxafy.voc.core.UserInterface;
import de.noxafy.voc.core.model.Vocabulary;
import de.noxafy.voc.core.model.VocabularyBase;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import static de.noxafy.voc.core.model.Vocabulary.KnowledgeLevel.UNKNOWN;

/**
 * @author noxafy
 * @created 29.08.17
 */
public class CLUserInterface implements UserInterface {

	private static final Random rnd = new Random();
	private final Strings str;

	public static final String TRAINING_WINDOW_DIMENSIONS = "5;100";

	public CLUserInterface() {
		str = Settings.LANG.getStrings();
	}

	@Override
	public void init() {
		if (Settings.TRAINING_MODE) {
			tell(ANSI.getShrinkWindow(TRAINING_WINDOW_DIMENSIONS));
			tell(ANSI.CLEAR_WINDOW);
		}
	}

	@Override
	public void praiseUser() {
		String random_vibe = str.getGoodVibes()[rnd.nextInt(str.getGoodVibes().length)];
		letReadThat(random_vibe);
	}

	@Override
	public void showAnswer(String word, String mnemonic) {
		tell(word);

		if (mnemonic != null && !mnemonic.isEmpty()) {
			tell("\n\t" + ANSI.transparent(mnemonic));
		}
	}

	private boolean getAnswer(String message, boolean default_true) {
		tell(message);

		switch (Character.toLowerCase(waitForUserFinished())) {
			case 'y':
			case 'j':
			case ']':
			case '+':
				return true;
			case 'n':
				return false;
			default:
				return default_true;
		}
	}

	@Override
	public int waitForUserFinished() {
		int ans;
		try {
			ans = System.in.read();
			if (ans != 10) {
				while (System.in.read() != 10) {
					//
				}
			}
			return ans;
		}
		catch (IOException e) {
			Log.error(e.toString());
			System.exit(1);
		}
		return 0;
	}

	@Override
	public boolean isCorrect() {
		return getAnswer(" ? (y/n) [n]: ", false);
	}

	@Override
	public void doAsk(String word) {
		tell(word + " ? ");
	}

	private void tellLn(String message) {
		tell(message + "\n");
	}

	private void tell(String message) {
		System.out.print(message);
	}

	@Override
	public void prepareForNext() {
		tell("\n");
		if (Settings.TRAINING_MODE) {
			tell(ANSI.CLEAR_WINDOW);
		}
	}

	@Override
	public void noVocsFound() {
		tellLn(str.getNoVocFound());
	}

	@Override
	public void summarize(VocabularyBase base) {
		if (base.isEmpty()) return;

		final List<Vocabulary> todo = base.getTodo();
		final List<Vocabulary> unknowns = base.getUnknowns();
		final List<Vocabulary> asked_vocs = base.getAskedVocs();
		final List<Vocabulary> new_vocs = base.getNewVocs();

		final int statistics_length = 80;
		todo.addAll(unknowns);

		if (todo.isEmpty()) {
			tellLn(str.getDoneForNow());
		}

		tellLn(str.getStatistics());
		int number_vocs = asked_vocs.size() + new_vocs.size();

		final double perc_todo = todo.size() / (double) number_vocs;
		final double perc_new = new_vocs.size() / (double) number_vocs;

		int todo_signs = (int) Math.round(perc_todo * statistics_length);
		int new_signs = (int) Math.round(perc_new * statistics_length);
		int known_signs = statistics_length - (todo_signs + new_signs);

		for (; known_signs > 0; known_signs--) {
			tell("#");
		}
		for (; todo_signs > 0; todo_signs--) {
			tell("-");
		}
		for (; new_signs > 0; new_signs--) {
			tell("+");
		}

		tell(String.format("\n" + str.getKnown() + ": %d/%d (%.2f%%); ",
				asked_vocs.size() - todo.size(), number_vocs, (1 - perc_todo - perc_new) * 100));
		tell(String.format(str.getTodo() + ": %d/%d (%.2f%%); ", todo.size(), number_vocs, perc_todo * 100));
		tellLn(String.format(str.getNew() + ": %d/%d (%.2f%%)", new_vocs.size(), number_vocs, perc_new * 100));

		// map for cnt level
		EnumMap<Vocabulary.KnowledgeLevel, Integer> knowledgeLevelCnts = new EnumMap<>(Vocabulary.KnowledgeLevel.class);

		// get highest siar and create fitting array for siar cnt
		int siar_highest = 0;
		for (Vocabulary v : asked_vocs) {
			if (v.getSucceeded_in_a_row() > siar_highest) siar_highest = v.getSucceeded_in_a_row();
		}
		int[] siarCnts = new int[siar_highest + 1];

		// cnt vocs
		for (Vocabulary v : asked_vocs) {
			int siar = v.getSucceeded_in_a_row();
			// push siar
			siarCnts[siar]++;

			// push level
			Vocabulary.KnowledgeLevel level = v.getLevel();
			Integer knowledgeLevelCnt = knowledgeLevelCnts.get(level);
			if (knowledgeLevelCnt == null) knowledgeLevelCnt = 0;
			knowledgeLevelCnts.put(level, knowledgeLevelCnt + 1);
		}

		// print results
		Vocabulary.KnowledgeLevel currentLevel = UNKNOWN;
		for (int i = 3; i < siarCnts.length; i++) {
			Vocabulary.KnowledgeLevel correspondingLevel = Vocabulary.KnowledgeLevel.decide(i);
			if (correspondingLevel != currentLevel) {
				currentLevel = correspondingLevel;
				tellLn(String.format("%s: %d", currentLevel.name(), knowledgeLevelCnts.get(currentLevel)));
			}
			tellLn(String.format("\t%d: %d", i, siarCnts[i]));
		}

		if (!unknowns.isEmpty()) tellLn(unknowns.size() + str.getUnknownVocsLeft());
		CLArgsParser.printNewlineAtExit = false;
	}

	private void letReadThat(String message) {
		tellLn(message);
		if (Settings.TRAINING_MODE) {
			try {
				Thread.sleep(message.length() * 25 + 360); // about 40 chars per second + 360 attention shift
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	@Override
	public boolean shouldReset() {
		return !getAnswer(str.getFinalAndReset() + " (y/n) [y]: ", true);
	}

	@Override
	public void bye() {
		tellLn(str.comeTomorrow());
	}
}
