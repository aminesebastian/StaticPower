package theking530.staticpower;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
import theking530.staticpower.client.rendering.blocks.MachineBakedModel;
import theking530.staticpower.crafting.wrappers.AbstractRecipe;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.crafting.wrappers.grinder.GrinderRecipeSerializer;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.utilities.Reference;

/**
 * Main registry class responsible for preparing entites for registration.
 * 
 * @author Amine Sebastian
 *
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("rawtypes")
public class StaticPowerRegistry {
	private static final HashSet<Item> ITEMS = new HashSet<>();
	private static final HashSet<Block> BLOCKS = new HashSet<>();
	private static final HashSet<TileEntityType<?>> TILE_ENTITY_TYPES = new HashSet<>();
	private static final HashSet<ContainerType<? extends Container>> CONTAINER_TYPES = new HashSet<>();
	private static final HashMap<IRecipeType, LinkedList<AbstractRecipe>> RECIPES = new HashMap<IRecipeType, LinkedList<AbstractRecipe>>();
	private static final HashSet<FlowingFluid> FLUIDS = new HashSet<FlowingFluid>();

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

	public static FlowingFluid preRegisterFluid(FlowingFluid fluid) {
		FLUIDS.add(fluid);
		return fluid;
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
	@SuppressWarnings({ "unchecked" })
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
	@SuppressWarnings({ "unchecked" })
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
		StaticPower.LOGGER.info("Static Power Common Setup Completed!");
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

		// Initialize the guis.
		ModContainerTypes.initializeGui();

		// Log the completion.
		StaticPower.LOGGER.info("Static Power Client Setup Completed!");
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	@SubscribeEvent
	public static void onResourcesReloaded(RecipesUpdatedEvent event) {
		// Capture if this is the first time we are caching.
		boolean firstTime = RECIPES.size() == 0;

		// Log that caching has started.
		StaticPower.LOGGER.info(String.format("%1$s Static Power recipes.", (firstTime ? "caching" : "re-caching")));

		// Clear the recipes list.
		RECIPES.clear();

		// Keep track of how many recipes are cached.
		int recipeCount = 0;

		// Iterate through all the recipes and cache the Static Power ones.
		Collection<IRecipe<?>> recipes = event.getRecipeManager().getRecipes();
		for (IRecipe<?> recipe : recipes) {
			if (recipe instanceof AbstractRecipe) {
				addRecipe((AbstractRecipe) recipe);
				recipeCount++;
			}
		}

		// Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"), recipeCount));
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		for (Item item : ITEMS) {
			event.getRegistry().register(item);
		}
	}

	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		for (Block block : BLOCKS) {
			event.getRegistry().register(block);
		}
	}

	@SubscribeEvent
	public static void onRegisterFluids(RegistryEvent.Register<Fluid> event) {
		for (Fluid fluid : FLUIDS) {
			event.getRegistry().register(fluid);
		}
	}

	@SubscribeEvent
	public static void onRegisterTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
		for (TileEntityType<?> teType : TILE_ENTITY_TYPES) {
			event.getRegistry().register(teType);
		}
	}

	@SubscribeEvent
	public static void onRegisterContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		for (ContainerType<?> container : CONTAINER_TYPES) {
			event.getRegistry().register(container);
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry().register(GrinderRecipeSerializer.INSTANCE);
	}

	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event) {
		for (BlockState blockState : ModBlocks.PoweredGrinder.getStateContainer().getValidStates()) {
			ModelResourceLocation variantMRL = BlockModelShapes.getModelLocation(blockState);
			IBakedModel existingModel = event.getModelRegistry().get(variantMRL);
			if (existingModel == null) {
				StaticPower.LOGGER.warn("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
			} else if (existingModel instanceof MachineBakedModel) {
				StaticPower.LOGGER.warn("Tried to replace MachineBakedModel twice");
			} else {
				MachineBakedModel customModel = new MachineBakedModel(existingModel);
				event.getModelRegistry().put(variantMRL, customModel);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE) {
			event.addSprite(MachineBakedModel.machineSideNormal);
			event.addSprite(MachineBakedModel.machineSideInput);
			event.addSprite(MachineBakedModel.machineSideOutput);
			event.addSprite(MachineBakedModel.machineSideDisabled);
		}
	}

	/**
	 * Attempts to find a recipe of the given type that matches the provided
	 * parameters.
	 * 
	 * @param <T>             The type of the recipe.
	 * @param recipeType      The {@link IRecipeType} of the recipe.
	 * @param matchParameters The match parameters to used.
	 * @return Optional of the recipe if it exists, otherwise empty.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractRecipe> Optional<T> getRecipe(IRecipeType<T> recipeType, RecipeMatchParameters matchParameters) {
		// If no recipes of this type exist, return empty.
		if (!RECIPES.containsKey(recipeType)) {
			return Optional.empty();
		}

		// Iterate through the recipe linked list and return the first instance that
		// matches.
		for (AbstractRecipe recipe : RECIPES.get(recipeType)) {
			if (recipe.isValid(matchParameters)) {
				return Optional.of((T) recipe);
			}
		}

		// If we find no match, return empty.
		return Optional.empty();
	}

	/**
	 * Adds a recipe to the recipes list.
	 * 
	 * @param recipe
	 */
	private static void addRecipe(AbstractRecipe recipe) {
		if (!RECIPES.containsKey(recipe.getType())) {
			RECIPES.put(recipe.getType(), new LinkedList<AbstractRecipe>());
		}
		RECIPES.get(recipe.getType()).add(recipe);
	}
}
