package de.noxafy.voc.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.noxafy.voc.view.UserInterface;

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
	private final List<Vocabulary> unknowns = new ArrayList<>();
	/**
	 * Contains all vocs already asked (not {@link Vocabulary#isNew}) but {@link Vocabulary#shouldBeAsked}, EXCEPT ones
	 * that are {@link Vocabulary#isUnknown}. Unknown vocs are only existing in database if user terminated session
	 * premature and therefore stored in own list {@link #unknowns} for being preferred in todo_now selection.
	 */
	private List<Vocabulary> todo;

	// Fill with Settings.NUMBER_SIMUL_VOCS vocs
	private final List<Vocabulary> todo_now = new LinkedList<>();

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
	}

	void generateVocsForToday(Settings settings) throws IOException {
		long now = System.currentTimeMillis();
		ui.init();
		generateTodo();

		if (todo.isEmpty() && unknowns.isEmpty()) {
			ui.debug("No todo left for now!");
			if (!ui.getAnswer(ui.str.getFinalAndReset() + " (y/n) [y]: ", true)) {
				ui.tell(ui.str.comeTomorrow());
				System.exit(0);
			}
		}

		// get how many at all should be asked
		int should_be_asked_overall = settings.NUMBER_SIMUL_VOCS;

		// add from unknown, remove them from todo_tmp
		ui.debug("Unknowns to ask: " + unknowns.size());
		for (int i = 0; i < unknowns.size() && todo_now.size() < should_be_asked_overall; i++) {
			Vocabulary v = unknowns.get(i);
			ui.debug("Add from unknown vocs: " + v, Settings.DEBUG_LEVEL.LONG);
			todo_now.add(v);
		}

		// get how many asked should be asked (leave space for new)
		int should_be_asked_from_asked = should_be_asked_overall - settings.NUMBER_NEW_VOCS_AT_START;
		ui.debug("Space left for asked: " + (should_be_asked_overall - todo_now.size()));
		// if space left
		if (should_be_asked_from_asked > todo_now.size()) {
			// sort list todo_tmp
			ui.debug("Sorting todo by rating ...");
			sortList(todo);
			// add highest rated
			ui.debug("Adding highest rated vocs.");
			for (int i = todo.size() - 1; i > 0 && todo_now.size() < should_be_asked_from_asked; i--) {
				Vocabulary v = todo.get(i);
				ui.debug("Add from asked vocs: " + v, Settings.DEBUG_LEVEL.LONG);
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
				ui.debugWithTab("Added from new vocs: %s", v);
				todo_now.add(v);
			}
		}

		ui.debug("**********************************************************");
		ui.debug("*** Todo today generation done with " + todo_now.size() + " vocs in " + (System.currentTimeMillis() - now) + " ms. ***");
		ui.debug("**********************************************************");

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
		todo = new ArrayList<>(asked_vocs.size()); // filled if asking routine has been run
		for (Vocabulary v : asked_vocs) {
			if (v.isUnknown()) {
				unknowns.add(v);
				ui.debugWithTab("To ask from unknown: %s", v);
			}
			else if (v.shouldBeAsked(now)) {
				todo.add(v);
				ui.debugWithTab("To ask: %s", v);
			}
			else {
				ui.debugWithTab("Not to ask: %s", v);
			}
		}
		ui.debug("There are " + todo.size() + " vocs to ask out of " + asked_vocs.size() + ".");
	}

	private void sortList(List<Vocabulary> list) {
		long now = System.currentTimeMillis();
		list.sort(comparingDouble(v -> v.getRating(now)));
		ui.debug("List sorted");
		ui.debug(list);
	}

	void update() {
		if (last_asked.isKnown()) {
			ui.debug("\"" + last_asked.getWord() + "\" removed because it's known!");
			todo_now.remove(last_asked);
			// Don't know where it came from, but keep "old" store updated for summary
			if (!unknowns.remove(last_asked)) {
				todo.remove(last_asked);
			}
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
		ui.debug("Fetch next voc. " + todo_now.size() + " vocs left.");
		if (todo_now.size() == 1) {
			last_asked = todo_now.get(0);
		}
		else {
			ui.debug("Sorting todo_now by rating ...");
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
		if (isEmpty()) return;

		final int statistics_length = 80;
		todo.addAll(unknowns);

		if (todo.isEmpty()) {
			ui.tellLn(ui.str.getDoneForNow());
		}

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
		ui.tellLn(String.format(ui.str.getNew() + ": %d/%d (%.2f%%)", new_vocs.size(), number_vocs, perc_new * 100));

		if (!unknowns.isEmpty()) {
			ui.tellLn(unknowns.size() + ui.str.getUnknownVocsLeft());
		}

		int k1 = 0, k2 = 0, k3 = 0, k4 = 0, k5 = 0;
		for (Vocabulary v : asked_vocs) {
			switch (v.getLevel()) {
				case LEVEL1:
					k1++;
					break;
				case LEVEL2:
					k2++;
					break;
				case LEVEL3:
					k3++;
					break;
				case LEVEL4:
					k4++;
					break;
				case LEVEL5:
					k5++;
					break;
			}
		}

		ui.tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL1.name(), k1));
		ui.tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL2.name(), k2));
		ui.tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL3.name(), k3));
		ui.tellLn(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL4.name(), k4));
		ui.tell(String.format("%s: %d", Vocabulary.KnowledgeLevel.LEVEL5.name(), k5));
		// newline printed at exit
	}

	boolean isEmpty() {
		return asked_vocs.isEmpty() && new_vocs.isEmpty();
	}

	int size() {
		return asked_vocs.size() + new_vocs.size();
	}
}
