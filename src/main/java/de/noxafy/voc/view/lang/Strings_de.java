package de.noxafy.voc.view.lang;

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
	public String getUnknownVocsLeft() {
		return " Vokabeln sind gefragt, aber nicht gewusst.";
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
	public String getTodo() {
		return "Ausstehend";
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
