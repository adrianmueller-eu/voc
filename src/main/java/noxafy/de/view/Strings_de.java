package noxafy.de.view;

/**
 * @author noxafy
 * @created 31.01.18
 */
public class Strings_de implements Strings {
	private static final String[] vibes = { "Gut!", "Großartig!", "Super!", "Wunderbar!", "Sagenhaft!", "Fantastisch!", "Bravo!",
			"Gute Arbeit!", "Gut gemacht!", "So geht's!" };

	@Override
	public String[] getGoodVibes() {
		return vibes;
	}

	@Override
	public String getFinal() {
		return "Alle Vokabeln für heute gelernt! :)";
	}

	@Override
	public String getFinalAndReset() {
		return "Alle Vokabeln für heute bereits gelernt. Möchtest du das zurücksetzen?";
	}

	@Override
	public String getVocsLeft() {
		return " bereits gefragte Vokabeln stehen noch aus.";
	}

	@Override
	public String getStatistics() {
		return "Statistik:";
	}

	@Override
	public String getNoVocFound() {
		return "Keine Vokabeln gefunden! Speichere welche in die Datenbank! Für weitere Informationen: -h";
	}

	@Override
	public String getKnown() {
		return "Gewusst";
	}

	@Override
	public String getUnknown() {
		return "Nicht gewusst";
	}

	@Override
	public String getNew() {
		return "Neu";
	}

	@Override
	public String comeTomorrow() {
		return "Komm morgen wieder! :)";
	}
}
