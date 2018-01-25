package noxafy.de.fileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import noxafy.de.core.Vocabulary;

/**
 * @author noxafy
 * @created 30.08.17
 */
public class VocGenerator extends FileManager<String[]> {

	private static final String vok_dir = System.getProperty("user.home") + "/Dropbox/Sonstiges/Sprachen/Vokabeln/";
	private static final File vocs = new File(vok_dir + "vocs");
	private static final File csv = new File(vok_dir + "Englisch.csv");

	private VocGenerator(File file) {
		super(file);
	}

	public static void main(String[] args) throws IOException {
		VocGenerator vocGenerator = new VocGenerator(vocs);
		String[] lines = vocGenerator.load();
		String res;
		List<String> fails = new ArrayList<>();
		for (int i = 0; i < lines.length; i++) {
			Vocabulary voc = getVoc(lines[i]);
			if (voc == null) {
				fails.add(lines[i]);
				System.err.println("Failed to process line " + (i + 1) + ": " + lines[i]);
			}
			else {
				res = getLine(voc);
				System.out.println("Processing " + res);
				vocGenerator.appendNewVoc(res);
				lines[i] = "";
				vocGenerator.write(lines);
			}
		}
		System.out.println(lines.length + " new vocs added.");
		if (!fails.isEmpty()) {
			System.out.println(fails.size() + " lines failed processing:");
			for (String fail : fails) {
				System.out.println(fail);
			}
		}
	}

	private static Vocabulary getVoc(String line) {
		line = line.substring(1, line.length() - 1);

		String[] args = line.split("\",\"");
		String word;
		String meaning;
		String mnemonic = "";
		Date added = new Date();
		Date lastAsked = null;
		int asked = 0;
		int failed = 0;
		int succeeded_in_a_row = 0;

		try {
			switch (args.length) {
				default:
				case 8:
					succeeded_in_a_row = Integer.parseInt(args[7]);
				case 7:
					failed = Integer.parseInt(args[6]);
				case 6:
					asked = Integer.parseInt(args[5]);
				case 5:
					if (!args[4].isEmpty()) {
						lastAsked = new Date(Long.parseLong(args[4]));
					}
				case 4:
					added = new Date(Long.parseLong(args[3]));
				case 3:
					mnemonic = args[2];
				case 2:
					meaning = args[1];
					word = args[0];
					break;
				case 1:
				case 0:
					return null;
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		return new Vocabulary(word, meaning, mnemonic, added, lastAsked, asked, failed, succeeded_in_a_row);
	}

	private static String getLine(Vocabulary voc) {
		return quote(voc.getWord(), true) +
				quote(voc.getMeaning(), true) +
				quote(voc.getMnemonic(), true) +
				quote(voc.getAdded().getTime(), true) +
				quote((voc.getLastAsked() == null) ? "" : voc.getLastAsked().getTime(), true) +
				quote(voc.getAsked(), true) +
				quote(voc.getFailed(), true) +
				quote(voc.getSucceeded_in_a_row(), false);
	}

	private static String quote(Object inner, boolean comma) {
		return "\"" + inner + ((comma) ? "\"," : "\"");
	}

	@Override
	String[] load() throws IOException {
		String content = getStringFromFile();
		if (content == null) {
			System.err.println("File " + getFile().getAbsolutePath() + " not found!");
			return new String[0];
		}
		else if (content.isEmpty()) {
			System.err.println("File " + getFile().getAbsolutePath() + " is empty. No line processed.");
			return new String[0];
		}
		return content.split("\n");
	}

	@Override
	void write(String[] data) {
		StringBuilder sb = new StringBuilder();
		for (String line : data) {
			if (line.isEmpty()) continue;
			sb.append(line).append("\n");
		}
		writeOutFile(sb.toString());
	}

	private void appendNewVoc(String line) {
		line += "\n";
		try (FileOutputStream out = new FileOutputStream(csv, true)) {
			out.write(line.getBytes());
		}
		catch (IOException e) {
			throw new RuntimeException("Writing a new line to " + csv.getAbsolutePath() + " failed.", e);
		}
	}
}
