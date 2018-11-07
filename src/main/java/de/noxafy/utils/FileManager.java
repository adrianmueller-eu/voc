package de.noxafy.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Abstract class for writing and reading from a file as String from the file system. Inheriting classes can focus on
 * parsing the file content.
 *
 * @author noxafy
 * @created 28.08.17
 */
public abstract class FileManager<T> {

	private final File file;

	/**
	 * Give the file to read from and write to. It does not need to exist.
	 *
	 * @param file file to read from and write to
	 */
	protected FileManager(File file) {
		this.file = file;
	}

	/**
	 * Parse the given content of the specified file to an object of type {@link T}.
	 *
	 * @return Parsed data as object of type {@link T} or <code>null</code> if the file is not available.
	 */
	protected abstract T onLoad(String content);

	/**
	 * Parse the given data object to a String to be saved at the specified file.
	 *
	 * @param data The data to be parsed
	 * @return Data as String
	 */
	protected abstract String onWrite(T data);

	/**
	 * Loads the data from the specified file.
	 *
	 * @return Data from file as object of type {@link T}
	 */
	public T load() {
		return onLoad(getStringFromFile());
	}

	/**
	 * Writes the given data to the specified destination file.
	 *
	 * @param data The data to be saved in file
	 */
	public void write(T data) {
		writeOutFile(onWrite(data));
	}

	private void writeOutFile(String content) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(content.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			Log.error("Writing " + content.length() + " bytes to file " + file.getAbsolutePath() + " failed.");
			Log.error(e.toString());
		}
	}

	/**
	 * Fetches the content of the given file and returns it as a String.
	 * Returns <code>null</code> when file is absent.
	 *
	 * @return the content as String or <code>null</code> if no content available
	 */
	private String getStringFromFile() {
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
			Log.error(e.toString());
			return null;
		}
	}

	public File getFile() {
		return file;
	}
}
