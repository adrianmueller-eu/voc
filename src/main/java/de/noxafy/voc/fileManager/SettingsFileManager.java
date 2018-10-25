package de.noxafy.voc.fileManager;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.noxafy.voc.Log;
import de.noxafy.voc.core.Settings;

/**
 * @author noxafy
 * @created 28.08.17
 */
public final class SettingsFileManager extends FileManager<Settings> {

	private static final String str_NUMBER_NEW_VOCS_AT_START = "NUMBER_NEW_VOCS_AT_START";
	private static final String str_NUMBER_SIMUL_VOCS = "NUMBER_SIMUL_VOCS";
	private static final int NUMBER_SIMUL_VOCS_DEFAULT = 20;
	private static final int NUMBER_NEW_VOCS_AT_START_DEFAULT = 4;
	private static SettingsFileManager singleton;

	private SettingsFileManager(File file) {
		super(file);
	}

	public static SettingsFileManager getInstance(String settings_path) {
		if (singleton == null) {
			singleton = new SettingsFileManager(new File(settings_path));
		}
		return singleton;
	}

	@Override
	public Settings load() {
		String jsonContent = getStringFromFile();
		if (jsonContent == null) {
			return new Settings(NUMBER_SIMUL_VOCS_DEFAULT, NUMBER_NEW_VOCS_AT_START_DEFAULT);
		}

		JsonObject obj = Json.createReader(new StringReader(jsonContent)).readObject();

		// NUMBER_SIMUL_VOCS
		int NUMBER_SIMUL_VOCS = NUMBER_SIMUL_VOCS_DEFAULT;
		try {
			NUMBER_SIMUL_VOCS = obj.getInt(str_NUMBER_SIMUL_VOCS);
		}
		catch (NullPointerException e) {
			Log.debug(str_NUMBER_SIMUL_VOCS + " was missing.");
		}

		// NUMBER_NEW_VOCS_AT_START
		int NUMBER_NEW_VOCS_AT_START = NUMBER_NEW_VOCS_AT_START_DEFAULT;
		try {
			NUMBER_NEW_VOCS_AT_START = obj.getInt(str_NUMBER_NEW_VOCS_AT_START);
		}
		catch (Exception e) {
			Log.debug(str_NUMBER_NEW_VOCS_AT_START + " was missing.");
		}

		return new Settings(NUMBER_SIMUL_VOCS, NUMBER_NEW_VOCS_AT_START);
	}

	@Override
	public void write(Settings settings) throws IOException {
		JsonObjectBuilder obj = Json.createObjectBuilder();
		obj.add(str_NUMBER_SIMUL_VOCS, settings.NUMBER_SIMUL_VOCS);
		obj.add(str_NUMBER_NEW_VOCS_AT_START, settings.NUMBER_NEW_VOCS_AT_START);
		writeOutFile(obj.build().toString());
	}
}
