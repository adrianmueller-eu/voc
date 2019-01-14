package de.noxafy.utils.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author noxafy
 * @created 14.01.19
 */
public abstract class DataManager<T, S> {

	/**
	 * Loads the data from the specified data source.
	 *
	 * @return Data from file as object of type {@link T}
	 */
	public @NotNull T load() {
		return onLoad(readData());
	}

	/**
	 * Writes the given data to the specified destination data source.
	 *
	 * @param data The data to be saved in file
	 */
	public void write(@NotNull T data) {
		writeData(onWrite(data));
	}

	/**
	 * Parse the given content of type {@link S} of the specified database to an object of type {@link T}.
	 * Given {@link S} might be <code>null</code> if the data source does not exist.
	 *
	 * @return Parsed data as object of type {@link T}.
	 */
	protected abstract @NotNull T onLoad(@Nullable S data);

	/**
	 * Parse the given data object to a {@link S} to be saved at the specified data source.
	 *
	 * @param data The data of type {@link T} to be parsed
	 * @return Data as {@link S}
	 */
	protected abstract @NotNull S onWrite(@NotNull T data);

	/**
	 * Read the data from the specified data source as database-specific, parsable content type {@link S}.
	 * Returns <code>null</code> if the data source does not exist.
	 *
	 * @return The data from the data source {@link S} or <code>null</code> if the data source does not exist
	 */
	protected abstract @Nullable S readData();


	/**
	 * Write the already parsed data of type {@link S} to the specified data soruce.
	 *
	 * @param data The data of already parsed of {@link S}
	 */
	protected abstract void writeData(@NotNull S data);

}
