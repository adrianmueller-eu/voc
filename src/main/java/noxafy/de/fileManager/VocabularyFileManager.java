package noxafy.de.fileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import noxafy.de.core.Vocabulary;
import noxafy.de.core.VocabularyBase;
import noxafy.de.view.UserInterface;

/**
 * @author noxafy
 * @created 28.08.17
 */
public final class VocabularyFileManager extends FileManager<VocabularyBase> {

	private static VocabularyFileManager singleton;

	private final UserInterface ui = UserInterface.getInstance();

	private VocabularyFileManager(File file) {
		super(file);
	}

	public static VocabularyFileManager getInstance(String settings_path) {
		if (singleton == null) {
			singleton = new VocabularyFileManager(new File(settings_path));
		}
		return singleton;
	}

	@Override
	public VocabularyBase load() {
		String content = getStringFromFile();
		if (content == null) {
			return new VocabularyBase(new ArrayList<>());
		}

		String[] lines = content.split("\n");
		ArrayList<Vocabulary> vocs = new ArrayList<>(lines.length);
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			try {
				vocs.add(getVocabulary(line));
			}
			catch (Exception e) {
				ui.tellln("Failed to parse line " + (i + 1) + ": " + line);
				ui.debug(e.toString());
			}
		}
		return new VocabularyBase(vocs);
	}

	private Vocabulary getVocabulary(String org_line) {
		// cut start and end "
		String line = org_line.substring(1, org_line.length() - 1);

		String[] args = line.split("\",\"");
		if (args.length < 8) {
			throw new TooShortLineException(org_line);
		}
		String word = args[0];
		String meaning = args[1];
		String mnemonic = args[2];
		Date added = new Date(Long.parseLong(args[3]));
		Date lastAsked = null;
		if (!args[4].isEmpty()) {
			lastAsked = new Date(Long.parseLong(args[4]));
		}
		int asked = Integer.parseInt(args[5]);
		int failed = Integer.parseInt(args[6]);
		int succeeded_in_a_row = Integer.parseInt(args[7]);
		return new Vocabulary(word, meaning, mnemonic, added, lastAsked, asked, failed, succeeded_in_a_row);
	}

	@Override
	public void write(VocabularyBase base) throws IOException {
		List<Vocabulary> vocs = base.getAllVocs();
		StringBuilder csv = new StringBuilder();
		for (Vocabulary voc : vocs) {
			csv.append(getLine(voc)).append("\n");
		}
		writeOutFile(csv.toString());
	}

	private StringBuilder getLine(Vocabulary voc) {
		StringBuilder line = new StringBuilder();
		line.append(quote(voc.getWord(), true))
				.append(quote(voc.getMeaning(), true))
				.append(quote(voc.getMnemonic(), true))
				.append(quote(voc.getAdded().getTime(), true))
				.append(quote((voc.getLastAsked() == null) ? "" : voc.getLastAsked().getTime(), true))
				.append(quote(voc.getAsked(), true))
				.append(quote(voc.getFailed(), true))
				.append(quote(voc.getSucceeded_in_a_row(), false));
		return line;
	}

	private String quote(Object inner, boolean comma) {
		return "\"" + inner + ((comma) ? "\"," : "\"");
	}
}