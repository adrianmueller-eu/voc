package noxafy.de.view;

/**
 * @author noxafy
 * @created 31.01.18
 */
public class Strings_en extends Strings {
	private static final String[] vibes = { "Good!", "Great!", "Awesome!", "Fabulous!", "Fantastic!", "Bravo!",
			"Good job!", "Nice going!", "Nicely done!", "Well done!", "Way to go!" };

	@Override
	public String[] getGoodVibes() {
		return vibes;
	}

	@Override
	public String getFinal() {
		return "All vocabulary items learned for today! :)";
	}

	@Override
	public String getFinalAndReset() {
		return "All vocabulary items already learned for today. Do you want to reset that?";
	}

	@Override
	public String getVocsLeft() {
		return " already asked items left to do.";
	}

	@Override
	public String getStatistics() {
		return "Statistics:";
	}

	@Override
	public String getNoVocFound() {
		return "No vocabulary item found.";
	}
}
