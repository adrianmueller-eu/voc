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
	private static final UserInterface singleton = new UserInterface();
	public final Strings str;

	public void ask(Vocabulary voc) throws IOException {
		boolean askWord = Math.random() < 0.5;
		doAsk(voc, askWord);
		getAnswer("", false);
		showAnswer(voc, askWord);
		if (getAnswer(" ? (y/n) [n]: ", false)) {
			voc.succeeded();
			if (voc.isKnown()) {
				String random_vibe = str.getGoodVibes()[rnd.nextInt(str.getGoodVibes().length)];
				letReadThat(random_vibe);
			}
		}
		else {
			voc.failed();
		}
		tell("\n");
		if (Settings.TRAINING_MODE) {
			tell(ANSI.CLEAR_WINDOW);
		}
	}

	public void letReadThat(String message) {
		tellln(message);
		if (Settings.TRAINING_MODE) {
			try {
				Thread.sleep(message.length() * 67 + 1710); // about 15 chars per second + 360 attention shift + 1350 feel good :)
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private UserInterface() {
		switch (Settings.LANG) {
			case "de":
				str = new Strings_de();
				break;
			default:
				// "en" is default
				str = new Strings_en();
				break;
		}

		if (Settings.TRAINING_MODE) {
			tell(ANSI.SHIRNK_WINDOW);
			tell(ANSI.CLEAR_WINDOW);
		}
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
