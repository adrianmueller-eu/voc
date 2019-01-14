package de.noxafy.vgen;

import de.noxafy.utils.data.FileManager;
import de.noxafy.utils.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author noxafy
 * @created 26.10.18
 */
public class LineWiseFileManager extends FileManager<String[]> {

	LineWiseFileManager(File file) {
		super(file);
	}

	@NotNull
	@Override
	protected String[] onLoad(@Nullable String content) {
		if (content == null) {
			return new String[0];
		}

		if (content.isEmpty()) {
			Log.info("File " + getFile().getAbsolutePath() + " is empty. No line processed.");
			return new String[0];
		}
		return content.split("\n");
	}

	@NotNull
	@Override
	protected String onWrite(@NotNull String[] data) {
		StringBuilder sb = new StringBuilder();
		for (String line : data) {
			if (line.isEmpty()) continue;
			sb.append(line).append("\n");
		}
		return sb.toString();
	}
}
