package noxafy.de.core;

import noxafy.de.view.UserInterface;

import static noxafy.de.view.ANSI.transparent;

/**
 * @author noxafy
 * @created 28.08.17
 */
public class Vocabulary {

	private static final UserInterface ui = UserInterface.getInstance();

	// foreign lang
	private final String word;
	// own lang
	private final String meaning;
	// helping comment
	private final String mnemonic;
	// additional saved
	private final long added;
	private int asked;
	private int failed;
	private int succeeded_in_a_row;
	private long lastAsked;
	// calculated
	private double rating;
	private KnowledgeLevel level;
	private long ratingDate;

	public Vocabulary(String word, String meaning, String mnemonic, long added, long lastAsked, int asked, int failed, int succeeded_in_a_row) {
		this.word = word;
		this.meaning = meaning;
		this.mnemonic = mnemonic;
		this.added = added;
		this.lastAsked = lastAsked;
		this.asked = asked;
		this.failed = failed;
		this.succeeded_in_a_row = succeeded_in_a_row;
		level = KnowledgeLevel.decide(succeeded_in_a_row);
	}

	public long getAdded() {
		return added;
	}

	/**
	 * Might be null.
	 *
	 * @return date when voc was asked last time
	 */
	public long getLastAsked() {
		return lastAsked;
	}

	public String getWord() {
		return word;
	}

	public String getMeaning() {
		return meaning;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public int getAsked() {
		return asked;
	}

	public void succeeded() {
		succeeded_in_a_row++;
		level = KnowledgeLevel.decide(succeeded_in_a_row);
		asked();
		ui.debug("Success! " + succeeded_in_a_row + " in a row.");
	}

	public void failed() {
		succeeded_in_a_row = 0;
		level = KnowledgeLevel.UNKNOWN;
		failed++;
		asked();
		ui.debug("Failed!");
	}

	private void asked() {
		asked++;
		lastAsked = System.currentTimeMillis();
	}

	public boolean hasMnemonic() {
		return (mnemonic != null) && !"".equals(mnemonic);
	}

	public boolean isNew() {
		return asked < 3;
	}

	public int getSucceeded_in_a_row() {
		return succeeded_in_a_row;
	}

	public boolean isKnown() {
		return level != KnowledgeLevel.UNKNOWN;
	}

	public int getFailed() {
		return failed;
	}

	public double getRating(long now) {
		if (ratingDate == 0 || now != ratingDate) {
			ratingDate = now;
			// 0 ... 1 for number of fails
			double failRate = (isNew()) ? 0.5 : failed / (double) asked;
			if (succeeded_in_a_row > 2) {
				failRate /= succeeded_in_a_row - 2;
			}
			// (-20000 ...) 0 ... 1 for time passed since last asked
			// never asked vocs should be asked, so default to 1
			double time_passed_rating = 1;
			if (lastAsked != 0) {
				long forgot50percents = 43200000; // half day
				long time_passed = now - lastAsked;
				// prevent div 0 error
				time_passed++;
				time_passed_rating = -forgot50percents / (double) (time_passed + forgot50percents) + 1;
				// rate just asked vocs very bad
				if (time_passed < 20000) {
					time_passed_rating -= 20000d / time_passed - 1;
				}
			}
			double random = Math.random() * 3;
			// failRate weighted 3 times
			// 57% via heuristics, 43% random
			rating = 3 * failRate + time_passed_rating + random;
			if (Settings.DEBUG) {
				StringBuilder tabs = new StringBuilder();
				for (int wlength = word.length() + 16; wlength < 48; wlength += 8) {
					tabs.append("\t");
				}
				String debug_rating = String.format("Rated \"%s\":%slevel = %s, failR = %.2f, tpr = %.2f, rnd = %.2f -> rating = %.2f",
						word, tabs.toString(), level.toString(), 3 * failRate, time_passed_rating, random, rating);
				ui.debugWithTab(debug_rating);
			}
		}
		return rating;
	}

	public boolean shouldBeAsked(long now) {
		if (level == KnowledgeLevel.UNKNOWN) return true;

		long diff = now - lastAsked;
		switch (level) {
			case LEVEL1:
				return diff > 86400000L; // 1 day
			case LEVEL2:
				return diff > 259200000L; // 3 days
			case LEVEL3:
				return diff > 604800000L; // 7 days
			case LEVEL4:
				return diff > 7862400000L; // 3 months
			case LEVEL5:
				return diff > 31536000000L; // 1 year
			default:
				throw new IllegalArgumentException("Unknown level: " + level + " for " + this);
		}
	}

	@Override
	public String toString() {
		return String.format("word: %s" + transparent("; meaning: %s%s ") + "(l: %s, a: %d, f: %d, srow: %d, rtng: %.2f)",
				word,
				meaning,
				(hasMnemonic()) ? "; mnemonic: " + mnemonic : "",
				level.toString(),
				asked,
				failed,
				succeeded_in_a_row,
				rating
		);
	}

	enum KnowledgeLevel {
		UNKNOWN, LEVEL1, LEVEL2, LEVEL3, LEVEL4, LEVEL5;

		static KnowledgeLevel decide(int succeeded_in_a_row) {
			switch (succeeded_in_a_row) {
				case 0:
				case 1:
				case 2:
					return UNKNOWN;
				case 3:
				case 4:
					return LEVEL1;
				case 5:
				case 6:
					return LEVEL2;
				case 7:
				case 8:
				case 9:
				case 10:
					return LEVEL3;
				case 11:
				case 12:
				case 13:
					return LEVEL4;
				default:
					return LEVEL5;
			}
		}
	}
}
