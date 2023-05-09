package theking530.staticcore.data.gamedata;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import theking530.staticcore.StaticCore;
import theking530.staticcore.events.StaticCoreForgeEventsCommon;
import theking530.staticcore.utilities.JsonUtilities;

public class StaticCoreGameDataManager {
	private static final ResourceLocation MAIN_DATABASE = new ResourceLocation(StaticCore.MOD_ID, "main");
	private static final HashMap<ResourceLocation, IStaticCoreGameDataFactory> DATA_FACTORIES = new LinkedHashMap<>();

	private final HashMap<ResourceLocation, Connection> databaseConnections;
	private final HashMap<ResourceLocation, IStaticCoreGameData> cachedData;
	private final Connection mainDbConnection;
	private final boolean isClientSide;

	public StaticCoreGameDataManager(boolean isClientSide) {
		this.isClientSide = isClientSide;
		cachedData = new LinkedHashMap<>();
		databaseConnections = new LinkedHashMap<>();

		if (!isClientSide) {
			mainDbConnection = getDatabaseConnection(MAIN_DATABASE);
			initializeDatabase();
		} else {
			mainDbConnection = null;
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

	public Connection getMainDatabase() {
		return mainDbConnection;
	}

	public void load() {
		StaticCore.LOGGER.info("Loading Static Core data!");

		// Iterate through all the factory entries and get the data file (if one exists)
		// for the data.
		DATA_FACTORIES.entrySet().parallelStream().forEach((entry) -> {
			try {
				String query = "SELECT data FROM serialized_data WHERE id = ?";
				PreparedStatement stmt = getMainDatabase().prepareStatement(query);
				stmt.setString(1, entry.getKey().toString());
				ResultSet sqlData = stmt.executeQuery();
				if (sqlData.next()) {
					loadGameData(entry.getKey(), sqlData.getString(1), entry.getValue());
				}
			} catch (Exception e) {
				StaticCore.LOGGER.error(String.format(
						"An error occured when attempting to load data: %1$s from the database.", entry.getKey()), e);
			}
		});
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
		StaticCore.LOGGER.info("Saving Static Core data!");

		// Iterate through all the data objects and save the data for each object.
		cachedData.values().parallelStream().forEach((data) -> {
			CompoundTag tag = new CompoundTag();
			data.serialize(tag);
			try {
				String replaceQuery = "REPLACE INTO serialized_data(id, data) VALUES(?, ?)";
				PreparedStatement stmt = getMainDatabase().prepareStatement(replaceQuery);
				stmt.setString(1, data.getId().toString());
				stmt.setString(2, JsonUtilities.nbtToPrettyJson(tag));
				stmt.execute();
			} catch (Exception e) {
				StaticCore.LOGGER
						.error(String.format("An error occured when saving the serialized game data for data id: %1$s!",
								data.getId().toString()), e);
			}
		});

		StaticCore.LOGGER.info("Finished Saving Static Core data!");
	}

	@SuppressWarnings("unchecked")
	public <T extends BasicStaticCoreGameData> T getGameData(ResourceLocation id) {
		if (!cachedData.containsKey(id)) {
			throw new RuntimeException(
					"Attempted to fetch game data that does not exist! Make sure the game data factory was properly registered.");
		}
		return (T) cachedData.get(id);
	}

	/**
	 * Synchronizes the data to the player forces them to completely clear their
	 * local data. This is good for sending when players first join in case they
	 * were already in another server that also has your mod.
	 */
	public void loadDataForClient(ServerPlayer player) {
		for (IStaticCoreGameData data : cachedData.values()) {
			data.syncToClient(player);
		}
	}

	public void tickGameData(Level level) {
		for (IStaticCoreGameData data : cachedData.values()) {
			data.tick(level);
		}
	}

	private void unload() {
		closeDatabaseConnection(mainDbConnection);
		for (Connection conn : databaseConnections.values()) {
			closeDatabaseConnection(conn);
		}
	}

	private void closeDatabaseConnection(Connection databaseConnection) {
		try {
			databaseConnection.close();
		} catch (SQLException e) {
			StaticCore.LOGGER.error(String.format("An error occured when attempted to close database connection: %1$s.",
					databaseConnection.toString()), e);
		}
	}

	private IStaticCoreGameData createAndCacheDataFirstTime(ResourceLocation id) {
		IStaticCoreGameData newInstance = DATA_FACTORIES.get(id).createGameDataInstance(isClientSide());
		newInstance.onFirstTimeCreated();
		cachedData.put(id, newInstance);
		return newInstance;
	}

	public Connection getDatabaseConnection(ResourceLocation database) {
		if (this.isClientSide()) {
			throw new RuntimeException("Database connections can only be aquired on the server!");
		}

		if (databaseConnections.containsKey(database)) {
			return databaseConnections.get(database);
		}

		String url = String.format("jdbc:sqlite:%1$s/%2$s_%3$s.db",
				StaticCoreForgeEventsCommon.dataPath.toAbsolutePath().toString(), database.getNamespace(),
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
		databaseConnections.put(database, connection);
		return connection;
	}

	public static class StaticCoreDataRegisterEvent extends Event implements IModBusEvent {
		public <T> void register(ResourceLocation id, IStaticCoreGameDataFactory factory) {
			DATA_FACTORIES.put(id, factory);
		}
	}

	public static class StaticCoreDataAccessor {
		private static StaticCoreGameDataManager serverInstance;
		private static StaticCoreGameDataManager clientInstance;

		public static void createForServer() {
			serverInstance = new StaticCoreGameDataManager(false);
			StaticCore.LOGGER.info("Creating StaticCoreGameDataManager on the Server.");
		}

		public static void createForClient() {
			clientInstance = new StaticCoreGameDataManager(true);
			StaticCore.LOGGER.info("Creating StaticCoreGameDataManager on the Client.");
		}

		public static void unloadForServer() {
			if (serverInstance != null) {
				StaticCore.LOGGER.info("Unloading StaticCoreGameDataManager on the Server.");
				serverInstance.unload();
				serverInstance = null;
			}
		}

		public static void unloadForClient() {
			if (clientInstance != null) {
				StaticCore.LOGGER.info("Unloading StaticCoreGameDataManager on the Client.");
				clientInstance = null;
			}
		}

		public static StaticCoreGameDataManager get(LevelAccessor level) {
			return get(level.isClientSide());
		}

		public static StaticCoreGameDataManager get(boolean isClientSide) {
			return isClientSide ? clientInstance : serverInstance;
		}

		public static StaticCoreGameDataManager getClient() {
			return clientInstance;
		}

		public static StaticCoreGameDataManager getServer() {
			return serverInstance;
		}

	}
}
