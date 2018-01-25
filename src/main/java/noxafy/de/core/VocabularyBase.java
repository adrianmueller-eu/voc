package noxafy.de.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import noxafy.de.view.UserInterface;

import static java.util.Comparator.comparingDouble;

/**
 * @author noxafy
 * @created 28.08.17
 */
public class VocabularyBase {

	private static final Random rand = new Random();
	private final UserInterface ui = UserInterface.getInstance();

	private final List<Vocabulary> asked_vocs = new LinkedList<>();
	private final List<Vocabulary> new_vocs = new LinkedList<>();

	// Fill with Settings.NUMBER_SIMUL_VOCS vocs
	private final List<Vocabulary> today;

	private Vocabulary last_asked;

	public VocabularyBase(List<Vocabulary> vocs) {
		for (Vocabulary voc : vocs) {
			if (voc.isNew()) {
				new_vocs.add(voc);
			}
			else {
				asked_vocs.add(voc);
			}
		}
		today = new LinkedList<>();
	}

	public void generateVocsForToday(Settings settings) throws IOException {
		ui.debug("Already " + settings.vocs_learned_today + " vocs learned today.");
		if (settings.allDone()) {
			ui.tell("All vocabularies for today already learned. Do you want to reset that? (y/n) [y]: ");
			int ans = System.in.read();
			if (ans != 10) {
				while (System.in.read() != 10) {
					//
				}
			}

			switch (Character.toLowerCase(ans)) {
				case 'y':
				case 'j':
				case 10:
					settings.resetAllLearned();
					break;
				default:
					return;
			}
		}

		sortList(asked_vocs);
		int number_asked_vocs = settings.NUMBER_SIMUL_VOCS - settings.NUMBER_NEW_VOCS_AT_START - settings.vocs_learned_today;
		ui.debug("Add max " + number_asked_vocs + " asked vocs. Available: " + asked_vocs.size() + " asked vocs.");
		for (int i = asked_vocs.size() - 1; today.size() < number_asked_vocs && i >= 0; i--) {
			Vocabulary v = asked_vocs.get(i);
			ui.debug("Added from asked voc: " + v);
			today.add(v);
		}
		int new_vocs_add = settings.NUMBER_NEW_VOCS_AT_START;
		if (number_asked_vocs < 0) {
			new_vocs_add += number_asked_vocs;
		}

		// ask randomly from new vocs
		List<Vocabulary> new2 = new LinkedList<>(new_vocs);
		while (new_vocs_add > 0 && !new2.isEmpty()) {
			Vocabulary v = new2.remove(rand.nextInt(new2.size()));
			ui.debug("Added from newer voc: " + v);
			today.add(v);
			new_vocs_add--;
		}

		//  ask last added new vocs first
//		List<Vocabulary> new2 = new LinkedList<>(new_vocs);
//		new2.sort(Comparator.comparing(Vocabulary::getAdded).reversed());
//		while (new_vocs_add > 0 && !new2.isEmpty()) {
//			Vocabulary v = new2.remove(0);
//			ui.debug("Added from newer voc: " + v);
//			today.add(v);
//			new_vocs_add--;
//		}
	}

	private void sortList(List<Vocabulary> list) {
		Date now = new Date();
		list.sort(comparingDouble(v -> v.getRating(now)));
		ui.debug(list);
	}

	public void update(Settings settings) {
		if (last_asked.isKnown()) {
			settings.vocLearned();
			ui.debug(last_asked.getWord() + " removed because it's known!");
			today.remove(last_asked);
		}
		// move from new to asked if not new anymore
		if (new_vocs.remove(last_asked)) {
			asked_vocs.add(last_asked);
		}

		// refill with new vocs if new vocs available
//		while (today.size() < settings.NUMBER_SIMUL_VOCS && !new_vocs.isEmpty()) {
//			Vocabulary v = new_vocs.get(rand.nextInt(new_vocs.size()));
//			today.add(v);
//			if (Settings.DEBUG) {
//				System.out.println("Added new voc " + v.getWord() + "!");
//			}
//		}
	}

	public Vocabulary getNextVocabulary() {
		ui.debug("Get new voc -> today.size(): " + today.size());
		if (today.size() == 1) {
			last_asked = today.get(0);
		}
		else {
			sortList(today);
			// get and remove highest rated
			Vocabulary last = today.get(today.size() - 1);
			if (last == last_asked) {
				last_asked = today.get(today.size() - 2);
			}
			else {
				last_asked = last;
			}
		}
		return last_asked;
	}

	public boolean hasNextVocabulary() {
		return !today.isEmpty();
	}

	public List<Vocabulary> getAllVocs() {
		ArrayList<Vocabulary> vocabularies = new ArrayList<>(asked_vocs);
		vocabularies.addAll(new_vocs);
		return vocabularies;
	}

	public void summarize() {
		final int statistics_length = 80;

		ui.tellln("Statistics:");
		int number_vocs = asked_vocs.size() + new_vocs.size();
		int known = 0;
		for (Vocabulary asked_voc : asked_vocs) {
			if (asked_voc.isKnown()) {
				known++;
			}
		}
		final double perc_known = known / (double) number_vocs;
		final double perc_new = new_vocs.size() / (double) number_vocs;

		int known_signs = (int) Math.round(perc_known * statistics_length);
		int new_signs = (int) Math.round(perc_new * statistics_length);
		int rest = statistics_length - (known_signs + new_signs);

		for (; known_signs > 0; known_signs--) {
			ui.tell("#");
		}
		for (; rest > 0; rest--) {
			ui.tell("-");
		}
		for (; new_signs > 0; new_signs--) {
			ui.tell("+");
		}

		ui.tell(String.format("\nKnown: %d/%d (%.2f%%); ", known, number_vocs, perc_known * 100));
		ui.tell(String.format("Unknown: %d/%d (%.2f%%); ", asked_vocs.size() - known, number_vocs, (1 - perc_known - perc_new) * 100));
		ui.tell(String.format("New: %d/%d (%.2f%%)", new_vocs.size(), number_vocs, perc_new * 100));
		// newline printed at exit
	}
}
