package de.noxafy.voc.core.fileManager;

import de.noxafy.utils.data.FileManager;
import de.noxafy.utils.TooShortLineException;
import de.noxafy.utils.Log;
import de.noxafy.voc.core.model.VocabularyBase;
import de.noxafy.voc.core.model.Vocabulary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noxafy
 * @created 28.08.17
 */
public final class VocabularyFileManager extends FileManager<VocabularyBase> {

	private static final Map<String, VocabularyFileManager> singletons = new HashMap<>();

	private VocabularyFileManager(File file) {
		super(file);
	}

	public static VocabularyFileManager getInstance(String settings_path) {
		return singletons.computeIfAbsent(settings_path, path -> new VocabularyFileManager(new File(path)));
	}

	@NotNull
	@Override
	protected VocabularyBase onLoad(@Nullable String content) {
		Log.verbose("Loading vocabulary base from: %s", getFile().getAbsolutePath());
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
				Log.warn("Failed to parse line " + (i + 1) + ": " + line);
				Log.warn(e.toString());
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
		long added = Long.parseLong(args[3]);
		long lastAsked = 0;
		if (!args[4].isEmpty()) {
			lastAsked = Long.parseLong(args[4]);
		}
		int asked = Integer.parseInt(args[5]);
		int failed = Integer.parseInt(args[6]);
		int succeeded_in_a_row = Integer.parseInt(args[7]);
		return new Vocabulary(word, meaning, mnemonic, added, lastAsked, asked, failed, succeeded_in_a_row);
	}

	@NotNull
	@Override
	protected String onWrite(@NotNull VocabularyBase base) {
		List<Vocabulary> vocs = base.getAllVocs();
		StringBuilder csv = new StringBuilder();
		// TODO: save a map of strings (voc as key) and replace only the changed key (= last_asked?)
		for (Vocabulary voc : vocs) {
			csv.append(getLine(voc)).append("\n");
		}
		Log.verbose("Write vocabulary base to: %s", getFile().getAbsolutePath());
		return csv.toString();
	}

	private StringBuilder getLine(Vocabulary voc) {
		StringBuilder line = new StringBuilder();
		line.append(quote(voc.getWord()))
				.append(quote(voc.getMeaning()))
				.append(quote(voc.getMnemonic()))
				.append(quote(voc.getAdded()))
				.append(quote(voc.getLastAsked() == 0 ? "" : voc.getLastAsked()))
				.append(quote(voc.getAsked()))
				.append(quote(voc.getFailed()))
				.append(quote(voc.getSucceeded_in_a_row()));
		return line.deleteCharAt(line.length() - 1);
	}

	private String quote(Object inner) {
		return "\"" + inner + "\",";
	}
}
