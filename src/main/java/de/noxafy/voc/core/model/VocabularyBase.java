package de.noxafy.voc.core.model;

import de.noxafy.utils.Log;
import de.noxafy.voc.core.Settings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.util.Comparator.comparingDouble;

/**
 * @author noxafy
 * @created 28.08.17
 */
public class VocabularyBase {

	private static final Random rand = new Random();

	private final List<Vocabulary> asked_vocs = new LinkedList<>();
	private final List<Vocabulary> new_vocs = new LinkedList<>();
	private final List<Vocabulary> unknowns = new ArrayList<>();

	/**
	 * Contains all vocs already asked (not {@link Vocabulary#isNew}) but {@link Vocabulary#shouldBeAsked}, EXCEPT ones
	 * that are {@link Vocabulary#isUnknown}. Unknown vocs are only existing in database, if user terminated session
	 * premature, and are therefore stored in an dedicated list {@link #unknowns} to get preferred in {@link #generateVocsForToday}.
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

	public void generateVocsForToday(Settings settings) {
		long now = System.currentTimeMillis();

		// get how many at all should be asked
		int should_be_asked_overall = settings.NUMBER_SIMUL_VOCS;

		// first, add all from unknown vocs (per definition not contained in todolist)
		Log.debug("Unknowns to ask: " + unknowns.size());
		for (int i = 0; i < unknowns.size() && todo_now.size() < should_be_asked_overall; i++) {
			Vocabulary v = unknowns.get(i);
			Log.verbose("Add from unknown vocs: %s", v);
			todo_now.add(v);
		}

		// see if all todos fit in rest (ignores new vocs constraint, but satisfies user)
		if (should_be_asked_overall - todo_now.size() >= todo.size()) {
			Log.debug("Add all " + todo.size() + " items to do.");
			todo_now.addAll(todo);
		}
		else {
			// add the highest rated vocs from todolist but leave space for new
			int should_be_asked_from_asked = should_be_asked_overall - settings.NUMBER_NEW_VOCS_AT_START;
			Log.debug("Space left for asked (+ new): " + (should_be_asked_overall - todo_now.size()));
			// if space left
			if (should_be_asked_overall - todo_now.size() > 0) {
				// sort todolist
				Log.debug("Sorting todo by rating ...");
				sortList(todo);
				// add highest rated vocs
				Log.debug("Adding highest rated vocs.");
				for (int i = todo.size() - 1; i > 0 && todo_now.size() < should_be_asked_from_asked; i--) {
					Vocabulary v = todo.get(i);
					Log.verbose("Add from asked vocs: %s", v);
					todo_now.add(v);
				}
			}
		}

		// if space left
		Log.debug("Space left for new: " + (should_be_asked_overall - todo_now.size()));
		if (should_be_asked_overall > todo_now.size()) {
			// ask randomly from new vocs
			List<Vocabulary> new2 = new LinkedList<>(new_vocs);
			while (todo_now.size() < should_be_asked_overall && !new2.isEmpty()) {
				Vocabulary v = new2.remove(rand.nextInt(new2.size()));
				Log.verboseWithTab("Added from new vocs: %s", v);
				todo_now.add(v);
			}
		}

		Log.debug("**********************************************************");
		Log.debug("*** Todo today generation done with " + todo_now.size() + " vocs in " + (System.currentTimeMillis() - now) + " ms. ***");
		Log.debug("**********************************************************");

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

	public void generateTodo() {
		// sort out vocs that have to be learned now
		Log.debug("Picking up vocs that have to be learned now ...");
		long now = System.currentTimeMillis();
		todo = new ArrayList<>(asked_vocs.size()); // filled if asking routine has been run
		for (Vocabulary v : asked_vocs) {
			if (v.isUnknown()) {
				unknowns.add(v);
				Log.verboseWithTab("To ask from unknown: %s", v);
			}
			else if (v.shouldBeAsked(now)) {
				todo.add(v);
				Log.verboseWithTab("To ask: %s", v);
			}
			else {
				Log.verboseWithTab("Not to ask: %s", v);
			}
		}
		Log.debug("There are " + todo.size() + " vocs to ask out of " + asked_vocs.size() + ".");
	}

	private void sortList(List<Vocabulary> list) {
		long now = System.currentTimeMillis();
		list.sort(comparingDouble(v -> v.getRating(now)));
		Log.debug("List sorted");
		Log.verbose(list);
	}

	public void update() {
		if (last_asked.isKnown()) {
			Log.debug("\"" + last_asked.getWord() + "\" removed because it's known!");
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

	public Vocabulary getNextVocabulary() {
		Log.debug("Fetch next voc. " + todo_now.size() + " vocs left.");
		if (todo_now.size() == 1) {
			last_asked = todo_now.get(0);
		}
		else {
			Log.debug("Sorting todo_now by rating ...");
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

	public boolean isEmpty() {
		return asked_vocs.isEmpty() && new_vocs.isEmpty();
	}

	public int size() {
		return asked_vocs.size() + new_vocs.size();
	}

	public List<Vocabulary> getAskedVocs() {
		return asked_vocs;
	}

	public List<Vocabulary> getNewVocs() {
		return new_vocs;
	}

	public List<Vocabulary> getUnknowns() {
		return unknowns;
	}

	public List<Vocabulary> getTodo() {
		return todo;
	}

	public boolean isNothingTodo() {
		return todo.isEmpty() && unknowns.isEmpty();
	}
}
