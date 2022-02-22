package theking530.staticcore.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.events.StaticPowerForgeEventsCommon;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class StaticPowerGameDataManager {
	private static final HashMap<ResourceLocation, StaticPowerGameData> DATA = new LinkedHashMap<>();
	private static final HashMap<ResourceLocation, Supplier<StaticPowerGameData>> DATA_FACTORIES = new LinkedHashMap<>();

	public static void registerDataFactory(ResourceLocation id, Supplier<StaticPowerGameData> factory) {
		DATA_FACTORIES.put(id, factory);
	}

	public static void loadDataFromDisk(Load load) {
		if (!load.getWorld().isClientSide() && load.getWorld().dimensionType().effectsLocation().equals(new ResourceLocation("minecraft:overworld"))) {
			// TODO: Determine how to prevent it from loading multiple times (if there are
			// multiple worlds loaded).
			StaticPower.LOGGER.info("Loading Static Power data!");

			// Iterate through all the factory entries and get the data file (if one exists)
			// for the data.
			DATA_FACTORIES.entrySet().parallelStream().forEach((entry) -> {
				try {
					String formattedName = formatDataSaveFileName(load, entry.getKey());
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
					StaticPower.LOGGER.error(String.format("An error occured when attempting to save data: %1$s to the disk.", entry.getKey()), e);
				}
			});
			loadDataForClients();
			StaticPower.LOGGER.info("Finished Loading Static Power data!");
		}
	}

	public static void saveDataToDisk(Save save) {
		if (!save.getWorld().isClientSide() && save.getWorld().dimensionType().effectsLocation().equals(new ResourceLocation("minecraft:overworld"))) {
			// TODO: Determine how to prevent it from saving multiple times (if there are
			// multiple worlds loaded).
			StaticPower.LOGGER.info("Saving Static Power data!");

			// Iterate through all the data objects and save the data for each object.
			DATA.values().parallelStream().forEach((data) -> {
				BufferedWriter writer = null;
				File lockFile = null;

				// Create a writer for the file and pass it to the data to save.
				String formattedName = formatDataSaveFileName(save, data.getId());
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

	public static void tickGameData() {
		for (StaticPowerGameData data : DATA.values()) {
			data.tick();
		}
	}

	private static StaticPowerGameData createAndCacheDataFirstTime(ResourceLocation id) {
		StaticPowerGameData newInstance = DATA_FACTORIES.get(id).get();
		newInstance.onFirstTimeCreated();
		DATA.put(newInstance.getId(), newInstance);
		return newInstance;
	}

	private static String formatDataSaveFileName(WorldEvent event, ResourceLocation id) {
		return String.format("%1$s/%2$s_%3$s.json", StaticPowerForgeEventsCommon.DATA_PATH.toAbsolutePath().toString(), id.getNamespace(), id.getPath());
	}

}
