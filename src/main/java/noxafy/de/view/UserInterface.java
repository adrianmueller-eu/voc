package noxafy.de.view;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import noxafy.de.core.Settings;
import noxafy.de.core.Vocabulary;

import static noxafy.de.view.ANSI.transparent;

/**
 * @author noxafy
 * @created 29.08.17
 */
public final class UserInterface {

	private static final Random rnd = new Random();
	private static final String[] good_vibes = { "Good!", "Great!", "Awesome!", "Fabulous!", "Fantastic!", "Bravo!",
			"Good job!", "Nice going!", "Nicely done!", "Well done!", "Way to go!" };
	private static final UserInterface singleton = new UserInterface();

	public void ask(Vocabulary voc) throws IOException {
		boolean askWord = Math.random() < 0.5;
		doAsk(voc, askWord);
		getAnswer("", false);
		showAnswer(voc, askWord);
		if (getAnswer(" ? (y/n) [n]: ", false)) {
			voc.succeeded();
		}
		else {
			voc.failed();
		}
		voc.asked();
		if (voc.isKnown()) {
			tellln(good_vibes[rnd.nextInt(good_vibes.length)]);
		}
		tell("\n");
	}

	private UserInterface() {
	}

	public static UserInterface getInstance() {
		return singleton;
	}

	private void showAnswer(Vocabulary voc, boolean askWord) {
		tell((askWord) ? voc.getMeaning() : voc.getWord());

		if (voc.hasMnemonic()) {
			tell("\n\t" + transparent(voc.getMnemonic()));
		}
	}

	public boolean getAnswer(String message, boolean default_true) throws IOException {
		if (!message.isEmpty()) {
			tell(message);
		}
		int ans = System.in.read();
		if (ans != 10) {
			while (System.in.read() != 10) {
				//
			}
		}

		switch (Character.toLowerCase(ans)) {
			case 'y':
			case 'j':
				return true;
			case 'n':
				return false;
			default:
				return default_true;
		}
	}

	private void doAsk(Vocabulary voc, boolean askWord) {
		tell(((askWord) ? voc.getWord() : voc.getMeaning()) + " ? ");
	}

	public void tellln(String message) {
		tell(message + "\n");
	}

	public void tell(String message) {
		System.out.print(message);
	}

	public void debug(String debug_message) {
		if (Settings.DEBUG) {
			tellln("DEBUG: " + debug_message);
		}
	}

	public void debug(List list) {
		if (Settings.DEBUG) {
			for (Object o : list) {
				debug(o.toString());
			}
		}
	}
}
