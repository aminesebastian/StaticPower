package theking530.staticpower;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.blocks.interfaces.IItemBlockProvider;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.data.StaticPowerGameData;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.events.StaticPowerForgeEventRegistry;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

/**
 * Main registry class responsible for preparing entities for registration and
 * then registering them.
 * 
 * @author Amine Sebastian
 *
 */
@SuppressWarnings({ "rawtypes" })
public class StaticPowerRegistry {
	public static final HashSet<Item> ITEMS = new LinkedHashSet<>();
	public static final HashSet<Block> BLOCKS = new LinkedHashSet<>();
	public static final HashSet<FlowingFluid> FLUIDS = new LinkedHashSet<>();
	public static final HashSet<RecipeSerializer> RECIPE_SERIALIZERS = new LinkedHashSet<>();
	public static final HashSet<AbstractEntityType<?>> ENTITIES = new LinkedHashSet<>();
	public static final HashSet<AbstractStaticPowerTree> TREES = new LinkedHashSet<>();
	public static final HashMap<String, StaticPowerGameData> DATA = new LinkedHashMap<>();

	private static final HashMap<String, Supplier<StaticPowerGameData>> DATA_FACTORIES = new LinkedHashMap<>();

	/**
	 * Pre-registers an item for registration through the registry event.
	 * 
	 * @param item The item to pre-register.
	 * @return The item that was passed.
	 */
	public static Item preRegisterItem(Item item) {
		ITEMS.add(item);
		return item;
	}

	/**
	 * Pre-registers an entity for registration through the registry event.
	 * 
	 * @param entity The entity to pre-register.
	 * @return The entity that was passed.
	 */
	public static <T extends Entity> AbstractEntityType<T> preRegisterEntity(AbstractEntityType<T> entity) {
		ENTITIES.add(entity);
		return entity;
	}

	/**
	 * Pre-registers a tree for registration through the init method.
	 * 
	 * @param tree The tree to pre-register.
	 * @return The tree that was passed.
	 */
	public static AbstractStaticPowerTree preRegisterTree(AbstractStaticPowerTree tree) {
		TREES.add(tree);
		return tree;
	}

	/**
	 * Pre-registers a {@link Block} for initialization through the registry event.
	 * If the block implements {@link IItemBlockProvider}, a {@link BlockItem} is
	 * also pre-registered.
	 * 
	 * @param block The {@link Block} to pre-register.
	 * @return The {@link Block} that was passed.
	 */
	public static Block preRegisterBlock(Block block) {
		BLOCKS.add(block);
		if (block instanceof IItemBlockProvider) {
			IItemBlockProvider provider = (IItemBlockProvider) block;
			BlockItem itemBlock = provider.getItemBlock();

			// Skip items with null item blocks.
			if (itemBlock != null) {
				preRegisterItem(provider.getItemBlock());
			}
		}
		return block;
	}

	public static FlowingFluid preRegisterFluid(FlowingFluid fluid) {
		FLUIDS.add(fluid);
		return fluid;
	}

	public static RecipeSerializer preRegisterRecipeSerializer(RecipeSerializer recipeSerializer) {
		RECIPE_SERIALIZERS.add(recipeSerializer);
		return recipeSerializer;
	}

	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		for (Item item : ITEMS) {
			event.getRegistry().register(item);
		}
	}

	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		for (Block block : BLOCKS) {
			event.getRegistry().register(block);
		}
	}

	public static void onRegisterFluids(RegistryEvent.Register<Fluid> event) {
		for (Fluid fluid : FLUIDS) {
			event.getRegistry().register(fluid);
		}
	}

	public static void onRegisterTileEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
		StaticCoreRegistry.registerTileEntityTypes(event);
	}

	public static void onRegisterContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
		StaticCoreRegistry.registerContainerTypes(event);
	}

	public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
		// Register mobs.
		for (AbstractEntityType<?> type : ENTITIES) {
			event.getRegistry().register(type.getType());
		}
	}

	public static void onRegisterEntityAttributes(EntityAttributeCreationEvent event) {
		// Register mobs.
		for (AbstractEntityType<?> type : ENTITIES) {
			if (type instanceof AbstractSpawnableMobType) {
				((AbstractSpawnableMobType<?>) type).registerAttributes(event);
			}
		}
	}

	public static void onRegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		for (RecipeSerializer serializer : RECIPE_SERIALIZERS) {
			event.getRegistry().register(serializer);
		}
	}

	public static void registerDataFactory(String dataName, Supplier<StaticPowerGameData> factory) {
		DATA_FACTORIES.put(dataName, factory);
	}

	public static void onServerStarting(ServerAboutToStartEvent serverStarted) {
		DATA.clear();
	}

	public static void onGameLoaded(Load load) {
		if (!load.getWorld().isClientSide()) {
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
						DATA.get(entry.getKey()).load(tag);
					} else {
						// If the file was not found, create the data for the first time.
						createAndCacheDataFirstTime(entry.getKey());
					}

					// Then we sync the data to the clients.
					DATA.get(entry.getKey()).syncToClients();
				} catch (Exception e) {
					StaticPower.LOGGER.error(String.format("An error occured when attempting to save data: %1$s to the disk.", entry.getKey()), e);
				}
			});

			StaticPower.LOGGER.info("Finished Loading Static Power data!");
		}
	}

	public static void onGameSave(Save save) {
		if (!save.getWorld().isClientSide()) {
			// TODO: Determine how to prevent it from saving multiple times (if there are
			// multiple worlds loaded).
			StaticPower.LOGGER.info("Saving Static Power data!");

			// Iterate through all the data objects and save the data for each object.
			DATA.values().parallelStream().forEach((data) -> {
				BufferedWriter writer = null;
				File lockFile = null;

				// Create a writer for the file and pass it to the data to save.
				String formattedName = formatDataSaveFileName(save, data.getName());
				String lockfileName = formattedName + ".lock";
				try {
					// Sync the data to the clients.
					data.syncToClients();

					// If there is a lock file, just skip this save.
					lockFile = new File(lockfileName);
					if (lockFile.exists() && !lockFile.isDirectory()) {
						StaticPower.LOGGER.warn(String.format("Skipping saving data for: %1$s to the disk. Lock file still exists.", data.getName()));
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
					StaticPower.LOGGER.error(String.format("An error occured when attempting to save data: %1$s to the disk.", data.getName()), e);
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
						StaticPower.LOGGER.error(String.format("An error occured when attempting to close the save data writer for data: %1$s.", data.getName()), e);
					}
				}
			});

			StaticPower.LOGGER.info("Finished Saving Static Power data!");
		}

	}

	public static StaticPowerGameData getGameDataByName(String name) {
		if (!DATA.containsKey(name)) {
			createAndCacheDataFirstTime(name);
		}
		return DATA.get(name);
	}

	private static StaticPowerGameData createAndCacheDataFirstTime(String name) {
		StaticPowerGameData newInstance = DATA_FACTORIES.get(name).get();
		newInstance.onFirstTimeCreated();
		DATA.put(newInstance.getName(), newInstance);
		return newInstance;
	}

	private static String formatDataSaveFileName(WorldEvent event, String name) {
		return String.format("%1$s/%2$s_%3$s.json", StaticPowerForgeEventRegistry.DATA_PATH.toAbsolutePath().toString(), StaticPower.MOD_ID, name);
	}
}
