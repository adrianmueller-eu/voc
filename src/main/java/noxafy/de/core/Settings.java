package noxafy.de.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author noxafy
 * @created 28.08.17
 */
public class Settings {
	// default lang for ui is "en"
	public static String LANG = "en";
	public static boolean DEBUG = false;
	// shrink window on start and clear after each voc
	public static boolean TRAINING_MODE = false;

	public final int NUMBER_SIMUL_VOCS;
	// should not be bigger than the previous two
	public final int NUMBER_NEW_VOCS_AT_START;
	public int vocs_learned_today;
	public String lastUpdated;

	public Settings(int number_simul_vocs, int number_new_vocs_at_start, int vocs_learned_today, String lastUpdated) {
		NUMBER_SIMUL_VOCS = number_simul_vocs;
		NUMBER_NEW_VOCS_AT_START = number_new_vocs_at_start;
		this.vocs_learned_today = vocs_learned_today;
		String currentDate = this.lastUpdated = new SimpleDateFormat("yyyy/DDD").format(new Date());
		if (!currentDate.equals(lastUpdated)) {
			resetAllLearned();
		}
	}

	public void vocLearned() {
		vocs_learned_today++;
	}

	public boolean allDone() {
		return vocs_learned_today >= NUMBER_SIMUL_VOCS;
	}

	public void resetAllLearned() {
		vocs_learned_today = 0;
	}
}
