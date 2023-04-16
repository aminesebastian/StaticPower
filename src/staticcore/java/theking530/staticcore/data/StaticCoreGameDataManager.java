package theking530.staticcore.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;
import theking530.staticcore.events.StaticCoreForgeEventsCommon;
import theking530.staticcore.utilities.JsonUtilities;

public class StaticCoreGameDataManager {
	private static final ResourceLocation MAIN_DATABASE = new ResourceLocation(StaticCore.MOD_ID, "main");
	private static final HashMap<ResourceLocation, IStaticCoreGameDataFactory> DATA_FACTORIES = new LinkedHashMap<>();

	private static StaticCoreGameDataManager instance;

	private final HashMap<ResourceLocation, Connection> databaseConnections;
	private final HashMap<ResourceLocation, StaticCoreGameData> cachedData;
	private final Connection db;
	private final boolean isClientSide;

	public StaticCoreGameDataManager(boolean isClientSide) {
		this.isClientSide = isClientSide;
		cachedData = new LinkedHashMap<>();
		if (!isClientSide) {
			databaseConnections = new LinkedHashMap<>();
			db = getDatabaseConnection(MAIN_DATABASE);
			initializeDatabase();
		} else {
			databaseConnections = null;
			db = null;
		}

		initializeCachedData();
	}

	public boolean isClientSide() {
		return isClientSide;
	}

	private void initializeCachedData() {
		DATA_FACTORIES.entrySet().parallelStream().forEach((entry) -> {
			createAndCacheDataFirstTime(entry.getKey());
		});
	}

	private void initializeDatabase() {
		//@formatter:off
		String createTableStatement = "CREATE TABLE IF NOT EXISTS serialized_data (\n" 
				+ "	id text NOT NULL,\n" 
				+ "	data text NOT NULL,\n"
				+ " UNIQUE(id))\n";
		//@formatter:on
		try {
			Statement stmt = getMainDatabase().createStatement();
			stmt.execute(createTableStatement);
		} catch (Exception e) {
			throw new RuntimeException("An error occured when creating the serialized data table!", e);
		}
	}

	public static StaticCoreGameDataManager get() {
		return instance;
	}

	public Connection getMainDatabase() {
		return db;
	}

	public static void registerDataFactory(ResourceLocation id, IStaticCoreGameDataFactory factory) {
		DATA_FACTORIES.put(id, factory);
	}

	public void load() {
		if (isClientSide()) {
			return;
		}
		
		StaticCore.LOGGER.info("Loading Static Core data!");

		// Iterate through all the factory entries and get the data file (if one exists)
		// for the data.
		DATA_FACTORIES.entrySet().parallelStream().forEach((entry) -> {
			try {
				//@formatter:off
				String query = String.format("SELECT data \n"
						+ "FROM serialized_data \n"
						+ "WHERE id = \"%1$s\"",
						formatDataIdToDatabaseId(entry.getKey()));		
				//@formatter:on		

				PreparedStatement stmt = getMainDatabase().prepareStatement(query);
				ResultSet sqlData = stmt.executeQuery();
				if (sqlData.next()) {
					loadGameData(entry.getKey(), sqlData.getString(0), entry.getValue());
				}
			} catch (Exception e) {
				StaticCore.LOGGER.error(String.format(
						"An error occured when attempting to load data: %1$s from the database.", entry.getKey()), e);
			}
		});
		loadDataForClients();
		StaticCore.LOGGER.info("Finished Loading Static Core data!");
	}

	private void loadGameData(ResourceLocation id, String data, IStaticCoreGameDataFactory factory)
			throws IOException, CommandSyntaxException {
		CompoundTag tag = TagParser.parseTag(data);

		// Try to load the data if there is cached data for this id. If there is not,
		// then the data in SQL might be fore a data manager that no longer exists --
		// ignore the data.
		if (cachedData.containsKey(id)) {
			cachedData.get(id).deserialize(tag);
		}
	}

	public void save() {
		if (isClientSide()) {
			return;
		}

		StaticCore.LOGGER.info("Saving Static Core data!");

		// Iterate through all the data objects and save the data for each object.
		cachedData.values().parallelStream().forEach((data) -> {
			CompoundTag tag = new CompoundTag();
			data.serialize(tag);
			String serializedData = JsonUtilities.nbtToPrettyJson(tag);
			//@formatter:off
				String insert = String.format("REPLACE INTO serialized_data(id, data) \n"
						+ "  VALUES('%1$s', '%2$s');",
						formatDataIdToDatabaseId(data.getId()), serializedData);
				//@formatter:on
			try {
				PreparedStatement stmt = getMainDatabase().prepareStatement(insert);
				stmt.execute();
			} catch (Exception e) {
				StaticCore.LOGGER
						.error(String.format("An error occured when saving the serialized game data for data id: $1$s!",
								data.getId().toString()), e);
			}
		});

		StaticCore.LOGGER.info("Finished Saving Static Core data!");
	}

	@SuppressWarnings("unchecked")
	public <T extends StaticCoreGameData> T getGameData(ResourceLocation id) {
		if (!cachedData.containsKey(id)) {
			throw new RuntimeException(
					"Attempted to fetch game data that does not exist! Make sure the game data factory was properly registered.");
		}
		return (T) cachedData.get(id);
	}

	public void deleteData(ResourceLocation id) {
		cachedData.remove(id);
	}

	public static void createForServer() {
		instance = new StaticCoreGameDataManager(false);
		StaticCore.LOGGER.info("Creating StaticCoreGameDataManager on the Server.");
	}

	public static void createForClient() {
		instance = new StaticCoreGameDataManager(true);
		StaticCore.LOGGER.info("Creating StaticCoreGameDataManager on the Client.");
	}

	public static void unload() {
		if (instance != null) {
			StaticCore.LOGGER.info(
					String.format("Unloading StaticCoreGameDataManager. Client Side: %1$s.", instance.isClientSide()));
			instance = null;
		}
	}

	/**
	 * Synchronizes the data to all clients and forces them to completely clear
	 * their local data. This is good for sending when players first join in case
	 * they were already in another server that also has your mod.
	 */
	public void loadDataForClients() {
		for (StaticCoreGameData data : cachedData.values()) {
			data.syncToClients();
		}
	}

	public void tickGameData(Level level) {
		for (StaticCoreGameData data : cachedData.values()) {
			data.tick(level);
		}
	}

	private StaticCoreGameData createAndCacheDataFirstTime(ResourceLocation id) {
		StaticCoreGameData newInstance = DATA_FACTORIES.get(id).createGameDataInstance(isClientSide());
		newInstance.onFirstTimeCreated();
		cachedData.put(newInstance.getId(), newInstance);
		return newInstance;
	}

	public Connection getDatabaseConnection(ResourceLocation database) {
		if (!databaseConnections.containsKey(database)) {
			databaseConnections.put(database, ensureDatabaseExists(database));
		}
		return databaseConnections.get(database);
	}

	private Connection ensureDatabaseExists(ResourceLocation database) {
		String url = String.format("jdbc:sqlite:%1$s/%2$s_%3$s.db",
				StaticCoreForgeEventsCommon.DATA_PATH.toAbsolutePath().toString(), database.getNamespace(),
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
			throw new RuntimeException(
					String.format("An error occured when attempting to create the sqlite database: %1$s",
							database.toString()),
					exception);
		}

		// Otherwise log the fact that we connected and return the connection.
		StaticCore.LOGGER.debug(String.format("Connected to database: %1$s.", database.toString()));
		return connection;
	}

	private String formatDataIdToDatabaseId(ResourceLocation id) {
		return id.toString().replace(":", "_");
	}
}
