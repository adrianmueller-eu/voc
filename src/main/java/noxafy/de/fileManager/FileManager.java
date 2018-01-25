package noxafy.de.fileManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author noxafy
 * @created 28.08.17
 */
abstract class FileManager<T> {

	private final File file;

	FileManager(File file) {
		this.file = file;
	}

	abstract T load() throws IOException;

	abstract void write(T data);

	void writeOutFile(String content) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(content.getBytes());
		}
		catch (IOException e) {
			throw new RuntimeException("Writing [" + content + "] to file " + file.getAbsolutePath() + " failed.", e);
		}
	}

	/**
	 * Fetches the content of the given file and returns it as a String.
	 * Returns <code>null</code> when file is absent.
	 *
	 * @return the content as String or <code>null</code> if no content available
	 */
	String getStringFromFile() throws IOException {
		try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = fis.read(buf)) != -1) {
				baos.write(buf, 0, bytesRead);
			}
			return baos.toString();
		}
		catch (FileNotFoundException e) {
			return null;
		}
	}

	File getFile() {
		return file;
	}
}
