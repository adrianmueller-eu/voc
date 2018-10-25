package de.noxafy.voc.view.commandline;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import de.noxafy.voc.Log;
import de.noxafy.voc.core.Settings;
import de.noxafy.voc.core.model.Vocabulary;
import de.noxafy.voc.core.VocabularyBase;
import de.noxafy.voc.view.UserInterface;
import de.noxafy.voc.view.lang.Strings;

/**
 * @author noxafy
 * @created 29.08.17
 */
public class CLUserInterface implements UserInterface {

	private static final Random rnd = new Random();
	private final Strings str;

	public CLUserInterface() {
		str = Settings.LANG.getStrings();
	}

	@Override
	public void init() {
		if (Settings.TRAINING_MODE) {
			tell(ANSI.SHIRNK_WINDOW);
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

		if (!unknowns.isEmpty()) {
			tellLn(unknowns.size() + str.getUnknownVocsLeft());
		}

		int k1 = 0, k2 = 0, k3 = 0, k4 = 0, k5 = 0;
		for (Vocabulary v : asked_vocs) {
			switch (v.getLevel()) {
				case LEVEL1:
					k1++;
					break;
				case LEVEL2:
					k2++;
					break;
				case LEVEL3:
					k3++;
					break;
				case LEVEL4:
					k4++;
					break;
				case LEVEL5:
					k5++;
					break;
			}
		}

		tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL1.name(), k1));
		tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL2.name(), k2));
		tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL3.name(), k3));
		tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL4.name(), k4));
		tell(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL5.name(), k5));
		// newline printed at exit
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
