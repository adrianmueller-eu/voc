package noxafy.de.core;

import java.io.IOException;
import java.util.ArrayList;
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
	private final List<Vocabulary> todo = new LinkedList<>();

	// Fill with Settings.NUMBER_SIMUL_VOCS vocs
	private final List<Vocabulary> todo_now;

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
		todo_now = new LinkedList<>();
	}

	public void generateVocsForToday(Settings settings) throws IOException {
		// test if enough is learned for today
		ui.debug("Already " + settings.vocs_learned_today + " vocs learned today.");
		if (settings.allDone()) {
			if (ui.getAnswer(ui.str.getFinalAndReset() + " (y/n) [y]: ", true)) {
				settings.resetAllLearned();
			}
			else {
				ui.tell(ui.str.comeTomorrow());
				System.exit(0);
				return;
			}
		}

		ui.init();
		generateTodo();

		// sort vocs to do by rating
		ui.debug("Sorting vocs to do by rating ...");
		sortList(todo);
		// add vocs from to do, but leave space for new vocs
		int new_vocs_add = generateAskedVocs(settings);

		// ask randomly from new vocs
		List<Vocabulary> new2 = new LinkedList<>(new_vocs);
		while (new_vocs_add > 0 && !new2.isEmpty()) {
			Vocabulary v = new2.remove(rand.nextInt(new2.size()));
			ui.debugWithTab("Added from newer: " + v);
			todo_now.add(v);
			new_vocs_add--;
		}

		//  ask last added new vocs first
//		List<Vocabulary> new2 = new LinkedList<>(new_vocs);
//		new2.sort(Comparator.comparing(Vocabulary::getAdded).reversed());
//		while (new_vocs_add > 0 && !new2.isEmpty()) {
//			Vocabulary v = new2.remove(0);
//			ui.debug("Added from newer voc: " + v);
//			todo_now.add(v);
//			new_vocs_add--;
//		}
	}

	private int generateAskedVocs(Settings settings) {
		int number_asked_vocs = settings.NUMBER_SIMUL_VOCS - settings.NUMBER_NEW_VOCS_AT_START - settings.vocs_learned_today;
		ui.debug("Add max " + number_asked_vocs + " vocs from already asked. Available: " + todo.size() + " vocs to do.");
		for (int i = todo.size() - 1; todo_now.size() < number_asked_vocs && i >= 0; i--) {
			Vocabulary v = todo.get(i);
			ui.debugWithTab("Added from asked: " + v);
			todo_now.add(v);
		}
		int new_vocs_add = settings.NUMBER_NEW_VOCS_AT_START;
		if (number_asked_vocs < 0) {
			new_vocs_add += number_asked_vocs;
		}
		return new_vocs_add;
	}

	public void generateTodo() {
		// sort out vocs that have to be learned now
		ui.debug("Picking up vocs that have to be learned now ...");
		Date now = new Date();
		for (Vocabulary voc : asked_vocs) {
			if (voc.shouldBeAsked(now)) {
				todo.add(voc);
				ui.debugWithTab("To ask: " + voc);
			}
			else {
				ui.debugWithTab("Not to ask: " + voc);
			}
		}
		ui.debug("There are " + todo.size() + " vocs to ask out of " + asked_vocs.size());
	}

	private void sortList(List<Vocabulary> list) {
		Date now = new Date();
		list.sort(comparingDouble(v -> v.getRating(now)));
		ui.debug("List sorted:");
		ui.debug(list);
	}

	public void update(Settings settings) {
		if (last_asked.isKnown()) {
			settings.vocLearned();
			ui.debug(last_asked.getWord() + " removed because it's known!");
			todo_now.remove(last_asked);
			todo.remove(last_asked);
		}
		// move from new to asked if not new anymore
		if (new_vocs.remove(last_asked)) {
			asked_vocs.add(last_asked);
		}

		// refill with new vocs if new vocs available
//		while (todo_now.size() < settings.NUMBER_SIMUL_VOCS && !new_vocs.isEmpty()) {
//			Vocabulary v = new_vocs.get(rand.nextInt(new_vocs.size()));
//			todo_now.add(v);
//			if (Settings.DEBUG) {
//				System.out.println("Added new voc " + v.getWord() + "!");
//			}
//		}
	}

	public Vocabulary getNextVocabulary() {
		ui.debug("Get new voc -> todo_now.size(): " + todo_now.size());
		if (todo_now.size() == 1) {
			last_asked = todo_now.get(0);
		}
		else {
			sortList(todo_now);
			// get and remove highest rated
			Vocabulary last = todo_now.get(todo_now.size() - 1);
			if (last == last_asked) {
				last_asked = todo_now.get(todo_now.size() - 2);
			}
			else {
				last_asked = last;
			}
		}
		return last_asked;
	}

	public boolean hasNextVocabulary() {
		return !todo_now.isEmpty();
	}

	public List<Vocabulary> getAllVocs() {
		ArrayList<Vocabulary> vocabularies = new ArrayList<>(asked_vocs);
		vocabularies.addAll(new_vocs);
		return vocabularies;
	}

	public void summarize() {
		if (asked_vocs.isEmpty() && new_vocs.isEmpty()) {
			return;
		}

		final int statistics_length = 80;

		ui.tellLn(ui.str.getStatistics());
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

		ui.tell(String.format("\n" + ui.str.getKnown() + ": %d/%d (%.2f%%); ", known, number_vocs, perc_known * 100));
		ui.tell(String.format(ui.str.getUnknown() + ": %d/%d (%.2f%%); ", asked_vocs.size() - known, number_vocs, (1 - perc_known - perc_new) * 100));
		ui.tellLn(String.format(ui.str.getNew() + ": %d/%d (%.2f%%)", new_vocs.size(), number_vocs, perc_new * 100));
		ui.tell(todo.size() + ui.str.getVocsLeft());
		// newline printed at exit
	}

	public boolean isEmpty() {
		return asked_vocs.isEmpty() && new_vocs.isEmpty();
	}
}
