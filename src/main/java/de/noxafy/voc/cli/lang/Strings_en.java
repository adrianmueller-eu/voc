package de.noxafy.voc.cli.lang;

/**
 * @author noxafy
 * @created 31.01.18
 */
public class Strings_en implements Strings {
	private static final String[] vibes = { "Good!", "Great!", "Awesome!", "Fabulous!", "Fantastic!", "Bravo!",
			"Good job!", "Nice going!", "Nicely done!", "Well done!", "Way to go!" };

	@Override
	public String[] getGoodVibes() {
		return vibes;
	}

	@Override
	public String getDoneForNow() {
		return "All vocabulary items learned for today! :)";
	}

	@Override
	public String getFinalAndReset() {
		return "All vocabulary items for today are already learned. Do you want to learn a new set of vocs?";
	}

	@Override
	public String getUnknownVocsLeft() {
		return " vocs are asked, but unknown.";
	}

	@Override
	public String getStatistics() {
		return "Statistics:";
	}

	@Override
	public String getNoVocFound() {
		return "No vocabulary items found. Please specify some in database. See -h for more information.";
	}

	@Override
	public String getKnown() {
		return "Known";
	}

	@Override
	public String getTodo() {
		return "Todo";
	}

	@Override
	public String getNew() {
		return "New";
	}

	@Override
	public String comeTomorrow() {
		return "Come back again tomorrow! :)";
	}
}
