package noxafy.de.core;

import java.io.IOException;
import java.util.ArrayList;
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

	void generateVocsForToday(Settings settings) throws IOException {
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

		// get how many at all should be asked
		int should_be_asked_overall = settings.NUMBER_SIMUL_VOCS - settings.vocs_learned_today;
		List<Vocabulary> todo_tmp = new ArrayList<>(todo);

		// add from unknown, remove them from todo_tmp
		for (int i = 0; i < todo.size() && todo_now.size() < should_be_asked_overall; i++) {
			Vocabulary v = todo.get(i);
			if (!v.isKnown()) {
				ui.debug("Add from unknown vocs: " + v);
				todo_now.add(v);
				todo_tmp.remove(v);
			}
		}

		// get how many asked should be asked (leave space for new)
		int should_be_asked_from_asked = should_be_asked_overall - settings.NUMBER_NEW_VOCS_AT_START;
		ui.debug("Space left for asked: " + (should_be_asked_overall - todo_now.size()));
		// if space left
		if (should_be_asked_from_asked > todo_now.size()) {
			// sort list todo_tmp
			ui.debug("Sorting todo by rating ...");
			sortList(todo_tmp);
			// add highest rated
			for (int i = todo_tmp.size() - 1; i > 0 && todo_now.size() < should_be_asked_from_asked; i--) {
				Vocabulary v = todo_tmp.get(i);
				ui.debug("Add from asked vocs: " + v);
				todo_now.add(v);
			}
		}

		// if space left
		ui.debug("Space left for new: " + (should_be_asked_overall - todo_now.size()));
		if (should_be_asked_overall > todo_now.size()) {
			// ask randomly from new vocs
			List<Vocabulary> new2 = new LinkedList<>(new_vocs);
			while (todo_now.size() < should_be_asked_overall && !new2.isEmpty()) {
				Vocabulary v = new2.remove(rand.nextInt(new2.size()));
				ui.debugWithTab("Added from new vocs: " + v);
				todo_now.add(v);
			}
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

	void generateTodo() {
		// sort out vocs that have to be learned now
		ui.debug("Picking up vocs that have to be learned now ...");
		long now = System.currentTimeMillis();
		todo.clear(); // filled if asking routine has been run
		for (Vocabulary voc : asked_vocs) {
			if (voc.shouldBeAsked(now)) {
				todo.add(voc);
				ui.debugWithTab("To ask: " + voc);
			}
			else {
				ui.debugWithTab("Not to ask: " + voc);
			}
		}
		ui.debug("There are " + todo.size() + " vocs to ask out of " + asked_vocs.size() + ".");
	}

	private void sortList(List<Vocabulary> list) {
		long now = System.currentTimeMillis();
		list.sort(comparingDouble(v -> v.getRating(now)));
		ui.debug("List sorted:");
		ui.debug(list);
	}

	void update(Settings settings) {
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

	Vocabulary getNextVocabulary() {
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

	boolean hasNextVocabulary() {
		return !todo_now.isEmpty();
	}

	public List<Vocabulary> getAllVocs() {
		ArrayList<Vocabulary> vocabularies = new ArrayList<>(asked_vocs);
		vocabularies.addAll(new_vocs);
		return vocabularies;
	}

	void summarize() {
		if (asked_vocs.isEmpty() && new_vocs.isEmpty()) {
			return;
		}

		final int statistics_length = 80;

		ui.tellLn(ui.str.getStatistics());
		int number_vocs = asked_vocs.size() + new_vocs.size();
		final double perc_todo = todo.size() / (double) number_vocs;
		final double perc_new = new_vocs.size() / (double) number_vocs;

		int todo_signs = (int) Math.round(perc_todo * statistics_length);
		int new_signs = (int) Math.round(perc_new * statistics_length);
		int known_signs = statistics_length - (todo_signs + new_signs);

		for (; known_signs > 0; known_signs--) {
			ui.tell("#");
		}
		for (; todo_signs > 0; todo_signs--) {
			ui.tell("-");
		}
		for (; new_signs > 0; new_signs--) {
			ui.tell("+");
		}

		ui.tell(String.format("\n" + ui.str.getKnown() + ": %d/%d (%.2f%%); ",
				asked_vocs.size() - todo.size(), number_vocs, (1 - perc_todo - perc_new) * 100));
		ui.tell(String.format(ui.str.getTodo() + ": %d/%d (%.2f%%); ", todo.size(), number_vocs, perc_todo * 100));
		ui.tell(String.format(ui.str.getNew() + ": %d/%d (%.2f%%)", new_vocs.size(), number_vocs, perc_new * 100));

		int unknown = 0;
		for (Vocabulary asked_voc : asked_vocs) {
			if (!asked_voc.isKnown()) {
				unknown++;
			}
		}
		if (unknown > 0) {
			ui.tellLn("");
			ui.tell(unknown + ui.str.getUnknownVocsLeft());
		}
		// newline printed at exit
	}

	boolean isEmpty() {
		return asked_vocs.isEmpty() && new_vocs.isEmpty();
	}
}
