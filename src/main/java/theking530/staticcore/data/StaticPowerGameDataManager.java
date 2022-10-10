package theking530.staticcore.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent.Load;
import net.minecraftforge.event.level.LevelEvent.Save;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.events.StaticPowerForgeEventsCommon;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class StaticPowerGameDataManager {
	private static final HashMap<ResourceLocation, Connection> DATABASE_CONNECTIONS = new LinkedHashMap<>();
	private static final HashMap<ResourceLocation, StaticPowerGameData> DATA = new LinkedHashMap<>();
	private static final HashMap<ResourceLocation, Supplier<StaticPowerGameData>> DATA_FACTORIES = new LinkedHashMap<>();

	public static void registerDataFactory(ResourceLocation id, Supplier<StaticPowerGameData> factory) {
		DATA_FACTORIES.put(id, factory);
	}

	public static void loadDataFromDisk(Load load) {
		if (!load.getLevel().isClientSide() && load.getLevel().dimensionType().effectsLocation().equals(new ResourceLocation("minecraft:overworld"))) {
			// TODO: Determine how to prevent it from loading multiple times (if there are
			// multiple worlds loaded).
			StaticPower.LOGGER.info("Loading Static Power data!");

			// Iterate through all the factory entries and get the data file (if one exists)
			// for the data.
			DATA_FACTORIES.entrySet().parallelStream().forEach((entry) -> {
				try {
					String formattedName = formatDataSaveFileName(entry.getKey());
					Path path = Path.of(formattedName);
					if (Files.exists(path)) {
						// Read the file and parse it into a compound tag.
						List<String> lines = Files.readAllLines(path);
						String json = String.join("\n", lines);
						CompoundTag tag = TagParser.parseTag(json);

						// If we already have a loaded data object with this name, load on top of it,
						// otherwise create a new one.
						if (!DATA.containsKey(entry.getKey())) {
							DATA.put(entry.getKey(), entry.getValue().get());
						}
						DATA.get(entry.getKey()).loadFromDisk(tag);
					} else {
						// If the file was not found, create the data for the first time.
						createAndCacheDataFirstTime(entry.getKey());
					}
				} catch (Exception e) {
					StaticPower.LOGGER.error(String.format("An error occured when attempting to load data: %1$s from the disk.", entry.getKey()), e);
				}
			});
			loadDataForClients();
			StaticPower.LOGGER.info("Finished Loading Static Power data!");
		}
	}

	public static void saveDataToDisk(Save save) {
		if (!save.getLevel().isClientSide() && save.getLevel().dimensionType().effectsLocation().equals(new ResourceLocation("minecraft:overworld"))) {
			// TODO: Determine how to prevent it from saving multiple times (if there are
			// multiple worlds loaded).
			StaticPower.LOGGER.info("Saving Static Power data!");

			// Iterate through all the data objects and save the data for each object.
			DATA.values().parallelStream().forEach((data) -> {
				BufferedWriter writer = null;
				File lockFile = null;

				// Create a writer for the file and pass it to the data to save.
				String formattedName = formatDataSaveFileName(data.getId());
				String lockfileName = formattedName + ".lock";
				try {
					// Sync the data to the clients.
					data.syncToClients();

					// If there is a lock file, just skip this save.
					lockFile = new File(lockfileName);
					if (lockFile.exists() && !lockFile.isDirectory()) {
						StaticPower.LOGGER.warn(String.format("Skipping saving data for: %1$s to the disk. Lock file still exists.", data.getId()));
						return;
					}

					// Create the lock file.
					lockFile.createNewFile();

					// Write and save the actual data we want to save to the disk.
					CompoundTag tag = new CompoundTag();
					data.serialize(tag);
					writer = new BufferedWriter(new FileWriter(formattedName));
					writer.write(JsonUtilities.nbtToPrettyJson(tag));
					writer.close();
				} catch (Exception e) {
					StaticPower.LOGGER.error(String.format("An error occured when attempting to save data: %1$s to the disk.", data.getId()), e);
				} finally {
					try {
						// Delete the lock file if it exists.
						if (lockFile != null && lockFile.exists() && !lockFile.isDirectory()) {
							lockFile.delete();
						}

						// Always try to close the writer if not-null.
						if (writer != null) {
							writer.close();
						}
					} catch (IOException e) {
						StaticPower.LOGGER.error(String.format("An error occured when attempting to close the save data writer for data: %1$s.", data.getId()), e);
					}
				}
			});

			StaticPower.LOGGER.info("Finished Saving Static Power data!");
		}

	}

	@SuppressWarnings("unchecked")
	public static <T extends StaticPowerGameData> T getOrCreateaGameData(ResourceLocation id) {
		if (!DATA.containsKey(id)) {
			createAndCacheDataFirstTime(id);
		}
		return (T) DATA.get(id);
	}

	public static void deleteData(ResourceLocation id) {
		DATA.remove(id);
	}

	public static void clearAllGameData() {
		DATA.clear();

		for (Connection conn : DATABASE_CONNECTIONS.values()) {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				StaticPower.LOGGER.error("An error occured when attempting to close the sql connection on server stop.");
			}
		}
		DATABASE_CONNECTIONS.clear();
	}

	/**
	 * Synchronizes the data to all clients and they perform a create OR update.
	 */
	public static void syncAllGameDataToClients() {
		for (StaticPowerGameData data : DATA.values()) {
			data.syncToClients();
		}
	}

	/**
	 * Synchronizes the data to all clients and forces them to completely clear
	 * their local data. This is good for sending when players first join in case
	 * they were already in another server that also has your mod.
	 */
	public static void loadDataForClients() {
		for (StaticPowerGameData data : DATA.values()) {
			StaticPowerMessageHandler.sendToAllPlayers(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new StaticPowerGameDataLoadPacket(data));
		}
	}

	public static void tickGameData(Level level) {
		if (level.dimensionType().effectsLocation().equals(new ResourceLocation("minecraft:overworld"))) {
			for (StaticPowerGameData data : DATA.values()) {
				data.tick(level);
			}
		}
	}

	private static StaticPowerGameData createAndCacheDataFirstTime(ResourceLocation id) {
		StaticPowerGameData newInstance = DATA_FACTORIES.get(id).get();
		newInstance.onFirstTimeCreated();
		DATA.put(newInstance.getId(), newInstance);
		return newInstance;
	}

	public static Connection getDatabaseConnection(ResourceLocation database) {
		if (!DATABASE_CONNECTIONS.containsKey(database)) {
			DATABASE_CONNECTIONS.put(database, ensureDatabaseExists(database));
		}
		return DATABASE_CONNECTIONS.get(database);
	}

	private static Connection ensureDatabaseExists(ResourceLocation database) {
		String url = String.format("jdbc:sqlite:%1$s/%2$s_%3$s.db", StaticPowerForgeEventsCommon.DATA_PATH.toAbsolutePath().toString(), database.getNamespace(),
				database.getPath());
		Exception exception = null;
		Connection connection = null;

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(url);
		} catch (Exception e) {
			exception = e;
		}

		// If the connection is null or there was an exception thrown, throw our own
		// runtime exception.
		if (connection == null || exception != null) {
			throw new RuntimeException(String.format("An error occured when attempting to create the sqlite database: %1$s", database.toString()), exception);
		}

		// Otherwise log the fact that we connected and return the connection.
		StaticPower.LOGGER.debug(String.format("Connected to database: %1$s.", database.toString()));
		return connection;
	}

	private static String formatDataSaveFileName(ResourceLocation id) {
		return String.format("%1$s/%2$s_%3$s.json", StaticPowerForgeEventsCommon.DATA_PATH.toAbsolutePath().toString(), id.getNamespace(), id.getPath());
	}

}
