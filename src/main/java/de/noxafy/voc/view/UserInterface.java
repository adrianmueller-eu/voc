package de.noxafy.voc.view;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import de.noxafy.voc.core.Settings;
import de.noxafy.voc.core.Vocabulary;
import de.noxafy.voc.view.lang.Strings;

/**
 * @author noxafy
 * @created 29.08.17
 */
public final class UserInterface {

	private static final Random rnd = new Random();
	private static final UserInterface singleton = new UserInterface();
	public final Strings str;

	private UserInterface() {
		str = Settings.LANG.getStrings();
	}

	public static UserInterface getInstance() {
		return singleton;
	}

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

	private void showAnswer(Vocabulary voc, boolean askWord) {
		tell((askWord) ? voc.getMeaning() : voc.getWord());

		if (voc.hasMnemonic()) {
			tell("\n\t" + ANSI.transparent(voc.getMnemonic()));
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

	public void tellLn(String message) {
		tell(message + "\n");
	}

	public void tell(String message) {
		System.out.print(message);
	}

	public void debug(String debug_message) {
		debug(debug_message, Settings.DEBUG_LEVEL.SHORT);
	}

	public void debug(String debug_message, Settings.DEBUG_LEVEL level) {
		if (Settings.DEBUG.is(level)) {
			tellLn("DEBUG: " + debug_message);
		}
	}

	public void debugWithTab(String debug_fmt_message, Object... objs) {
		if (Settings.DEBUG.is(Settings.DEBUG_LEVEL.LONG)) {
			tellLn("\tDEBUG: " + String.format(debug_fmt_message, objs));
		}
	}

	public void debug(List list) {
		if (Settings.DEBUG.is(Settings.DEBUG_LEVEL.LONG)) return;

		for (Object o : list) {
			debugWithTab(o.toString());
		}
	}

	public void init() {
		if (Settings.TRAINING_MODE) {
			tell(ANSI.SHIRNK_WINDOW);
			tell(ANSI.CLEAR_WINDOW);
		}
	}
}
