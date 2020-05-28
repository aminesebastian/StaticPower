package theking530.staticpower;

import java.util.HashSet;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import theking530.staticpower.blocks.IBlockRenderLayerProvider;
import theking530.staticpower.blocks.IItemBlockProvider;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.utilities.Reference;

/**
 * Main registry class responsible for preparing entites for registration.
 * 
 * @author Amine Sebastian
 *
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Registry {
	private static final HashSet<Item> ITEMS = new HashSet<>();
	private static final HashSet<Block> BLOCKS = new HashSet<>();
	private static final HashSet<TileEntityType<?>> TILE_ENTITY_TYPES = new HashSet<>();
	private static final HashSet<ContainerType<? extends Container>> CONTAINER_TYPES = new HashSet<>();

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

	/**
	 * Pre-registers a {@link TileEntity} for initialization through the registry
	 * event. Returns an instance of the {@link TileEntityType} for this
	 * {@link TileEntity} and {@link Block} combination.
	 * 
	 * @param <T>             The type of the {@link TileEntity}.
	 * @param factory         The tile entity create factory.
	 * @param tileEntityBlock The {@link Block} that is responsible for this tile
	 *                        entity.
	 * @return An instance of the {@link TileEntityType} for the provided
	 *         {@link TileEntity} & {@link Block}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends TileEntity> TileEntityType<T> preRegisterTileEntity(Supplier<? extends T> factory, Block tileEntityBlock) {
		TileEntityType teType = TileEntityType.Builder.create(factory, tileEntityBlock).build(null);
		teType.setRegistryName(tileEntityBlock.getRegistryName());
		TILE_ENTITY_TYPES.add(teType);
		return teType;
	}

	/**
	 * Pre-registers a {@link Container} for registration through the registration
	 * event. Returns an instance of the {@link ContainerType} for this
	 * {@link Container}.
	 * 
	 * @param <T>     The type of the {@link Container}.
	 * @param name    The registry name of the container sans namespace.
	 * @param factory The factory method (usually just the class name :: new).
	 * @return An instance of {@link ContainerType} for the provided
	 *         {@link Container}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Container, K extends ContainerScreen<T>> ContainerType<T> preRegisterContainer(String name, IContainerFactory factory) {
		ContainerType containerType = IForgeContainerType.create(factory);
		containerType.setRegistryName(name);
		CONTAINER_TYPES.add(containerType);
		return containerType;
	}

	/**
	 * This event is raised by the common setup event.
	 * 
	 * @param event The common setup event.
	 */
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {

	}

	/**
	 * This event is raised by the client setup event.
	 * 
	 * @param event The client setup event.
	 */
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		// If the block does not request the standard solid render type, set the new
		// render type for the block.
		for (Block block : BLOCKS) {
			// Skip non static power blocks.
			if (!(block instanceof IBlockRenderLayerProvider)) {
				continue;
			}

			// Check and update the render type as needed.
			IBlockRenderLayerProvider renderLayerProvider = (IBlockRenderLayerProvider) block;
			if (renderLayerProvider.getRenderType() != RenderType.getSolid()) {
				RenderTypeLookup.setRenderLayer(block, renderLayerProvider.getRenderType());
			}
		}
		ModContainerTypes.initializeGui();
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		for (Item item : ITEMS) {
			event.getRegistry().register(item);
		}
	}

	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		for (Block item : BLOCKS) {
			event.getRegistry().register(item);
		}
	}

	@SubscribeEvent
	public static void onRegisterTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
		for (TileEntityType<?> teType : TILE_ENTITY_TYPES) {
			System.out.println("STATIC POWER TE" + teType.getRegistryName());
			event.getRegistry().register(teType);
		}
	}

	@SubscribeEvent
	public static void onRegisterContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		for (ContainerType<?> container : CONTAINER_TYPES) {
			event.getRegistry().register(container);
		}
	}
}
