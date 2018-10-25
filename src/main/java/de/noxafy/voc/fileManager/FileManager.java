package de.noxafy.voc.fileManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import de.noxafy.voc.Log;

/**
 * @author noxafy
 * @created 28.08.17
 */
abstract class FileManager<T> {

	private final File file;

	FileManager(File file) {
		this.file = file;
	}

	abstract T load();

	abstract void write(T data) throws IOException;

	void writeOutFile(String content) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(content.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			Log.debug("Writing " + content.length() + " bytes to file " + file.getAbsolutePath() + " failed.");
			throw e;
		}
	}

	/**
	 * Fetches the content of the given file and returns it as a String.
	 * Returns <code>null</code> when file is absent.
	 *
	 * @return the content as String or <code>null</code> if no content available
	 */
	String getStringFromFile() {
		try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = fis.read(buf)) != -1) {
				baos.write(buf, 0, bytesRead);
			}
			return baos.toString();
		}
		catch (Exception e) {
			Log.error("Reading from file " + file.getAbsolutePath() + " failed.");
			Log.debug(e.toString());
			return null;
		}
	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}
}
