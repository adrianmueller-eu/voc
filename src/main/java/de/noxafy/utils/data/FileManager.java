package de.noxafy.utils.data;

import de.noxafy.utils.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Abstract class for writing and reading from a file as String from the file system. Inheriting classes can focus on
 * parsing the file content.
 *
 * @author noxafy
 * @created 28.08.17
 */
public abstract class FileManager<T> extends DataManager<T, String> {

	private final File file;

	/**
	 * Give the file to read from and write to. It does not need to exist.
	 *
	 * @param file file to read from and write to
	 */
	protected FileManager(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	/**
	 * Fetches the content of the given file and returns it as a String.
	 * Returns <code>null</code> when file is absent.
	 *
	 * @return the content as String or <code>null</code> if no content available
	 */
	@Nullable
	@Override
	protected String readData() {
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

	@Override
	protected void writeData(@NotNull String data) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(data.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			Log.error("Writing " + data.length() + " bytes to file " + file.getAbsolutePath() + " failed.");
			Log.error(e.toString());
		}
	}
}
