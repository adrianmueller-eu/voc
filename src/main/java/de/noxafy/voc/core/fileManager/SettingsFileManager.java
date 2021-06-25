package de.noxafy.voc.core.fileManager;

import de.noxafy.utils.Log;
import de.noxafy.utils.data.FileManager;
import de.noxafy.voc.core.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author noxafy
 * @created 28.08.17
 */
public final class SettingsFileManager extends FileManager<Settings> {

	private static final String str_NUMBER_NEW_VOCS = "NUMBER_NEW_VOCS";
	private static final String str_NUMBER_SIMUL_VOCS = "NUMBER_SIMUL_VOCS";
	private static final int NUMBER_SIMUL_VOCS_DEFAULT = 20;
	private static final int NUMBER_NEW_VOCS_DEFAULT = 4;
	private static SettingsFileManager singleton;

	private SettingsFileManager(File file) {
		super(file);
		// settings file should be created silently
		try {
			file.createNewFile();
		}
		catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static SettingsFileManager getInstance(String settings_path) {
		if (singleton == null) {
			singleton = new SettingsFileManager(new File(settings_path));
		}
		return singleton;
	}

	@NotNull
	@Override
	protected Settings onLoad(@Nullable String jsonContent) {
		Log.verbose("Loading settings from: %s", getFile().getAbsolutePath());

		int NUMBER_SIMUL_VOCS = NUMBER_SIMUL_VOCS_DEFAULT;
		int NUMBER_NEW_VOCS = NUMBER_NEW_VOCS_DEFAULT;
		boolean error = false;
		if (jsonContent == null || jsonContent.isEmpty()) {
			Log.warn("No settings file found. It will be created at " + getFile().getAbsolutePath() + " now.");
			error = true;
		}
		else {
			JsonObject obj = Json.createReader(new StringReader(jsonContent)).readObject();

			try {
				NUMBER_SIMUL_VOCS = obj.getInt(str_NUMBER_SIMUL_VOCS);
			}
			catch (NullPointerException e) {
				Log.warn(str_NUMBER_SIMUL_VOCS + " was missing.");
				error = true;
			}

			try {
				NUMBER_NEW_VOCS = obj.getInt(str_NUMBER_NEW_VOCS);
			}
			catch (Exception e) {
				Log.warn(str_NUMBER_NEW_VOCS + " was missing.");
				error = true;
			}
		}

		Settings settings = new Settings(NUMBER_SIMUL_VOCS, NUMBER_NEW_VOCS);
		if (error) this.write(settings); // to ensure sync
		return settings;
	}

	@NotNull
	@Override
	protected String onWrite(@NotNull Settings settings) {
		JsonObjectBuilder obj = Json.createObjectBuilder();
		obj.add(str_NUMBER_SIMUL_VOCS, settings.NUMBER_SIMUL_VOCS);
		obj.add(str_NUMBER_NEW_VOCS, settings.NUMBER_NEW_VOCS);
		Log.verbose("Write settings to: %s", getFile().getAbsolutePath());
		return obj.build().toString();
	}
}
