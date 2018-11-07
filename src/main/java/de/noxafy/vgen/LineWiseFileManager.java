package de.noxafy.vgen;

import de.noxafy.utils.FileManager;
import de.noxafy.utils.Log;

import java.io.File;

/**
 * @author noxafy
 * @created 26.10.18
 */
public class LineWiseFileManager extends FileManager<String[]> {

	LineWiseFileManager(File file) {
		super(file);
	}

	@Override
	protected String[] onLoad(String content) {
		if (content == null) {
			return new String[0];
		}

		if (content.isEmpty()) {
			Log.info("File " + getFile().getAbsolutePath() + " is empty. No line processed.");
			return new String[0];
		}
		return content.split("\n");
	}

	@Override
	protected String onWrite(String[] data) {
		StringBuilder sb = new StringBuilder();
		for (String line : data) {
			if (line.isEmpty()) continue;
			sb.append(line).append("\n");
		}
		return sb.toString();
	}
}
