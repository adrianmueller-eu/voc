package de.noxafy.vgen;

import de.noxafy.voc.Log;
import de.noxafy.voc.fileManager.FileManager;

import java.io.File;

/**
 * @author noxafy
 * @created 26.10.18
 */
public class VocabularyFileManager extends FileManager<String[]> {

	VocabularyFileManager(File file) {
		super(file);
	}

	@Override
	protected String[] onLoad(String content) {
		if (content == null) {
			return new String[0];
		}

		if (content.isEmpty()) {
			Log.info("File " + getFilePath() + " is empty. No line processed.");
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
