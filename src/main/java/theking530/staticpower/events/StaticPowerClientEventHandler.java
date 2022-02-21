package theking530.staticpower.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.teams.research.ActiveResearchHUD;
import theking530.staticpower.teams.research.GuiResearchMenu;

/**
 * Any client only event handling is performed here.
 * 
 * @author amine
 *
 */
@SuppressWarnings("resource")
@OnlyIn(Dist.CLIENT)
public class StaticPowerClientEventHandler {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerClientEventHandler.class);

	/**
	 * This event is raised by the client setup event (duh).
	 * 
	 * @param event The client setup event.
	 */
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		// If the block does not request the standard solid render type, set the new
		// render type for the block.
		LOGGER.info("Initializing Block Render Layers!");
		for (Block block : StaticPowerRegistry.BLOCKS) {
			// Check and update the render type as needed.
			if (block instanceof IRenderLayerProvider) {
				IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) block;
				if (renderLayerProvider.getRenderType() != RenderType.solid()) {
					ItemBlockRenderTypes.setRenderLayer(block, renderLayerProvider.getRenderType());
				}
			}
		}

		LOGGER.info("Initializing Fluid Render Layers!");
		for (Fluid fluid : StaticPowerRegistry.FLUIDS) {
			// Check and update the render type as needed.
			if (fluid instanceof IRenderLayerProvider) {
				IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) fluid;
				if (renderLayerProvider.getRenderType() != RenderType.solid()) {
					ItemBlockRenderTypes.setRenderLayer(fluid, renderLayerProvider.getRenderType());
				}
			}
		}

		// Register the guis.
		LOGGER.info("Registering Screen Factories!");
		StaticCoreRegistry.registerScreenFactories(event);

		LOGGER.info("Performing Key Bindings!");
		ModKeyBindings.registerBindings(event);

		// TODO: Build a system to handle this non-manually.
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
			ModKeyBindings.addCallback(ModKeyBindings.OPEN_RESEARCH, (binding) -> {
				if (binding.wasJustPressed()) {
					if (Minecraft.getInstance().screen == null) {
						Minecraft.getInstance().setScreen(new GuiResearchMenu());
					} else if (Minecraft.getInstance().screen instanceof GuiResearchMenu) {
						Minecraft.getInstance().screen.onClose();
					}
				}
			});
			StaticPowerRenderEventHandler.addHUDElement(new ActiveResearchHUD());
		});

		// Log the completion.
		LOGGER.info("Static Power Client Setup Completed!");
	}

	public static void onModelBakeEvent(ModelBakeEvent event) {
		// Loop through all the blocks, and check to see if they are a model supplier.
		for (Block block : StaticPowerRegistry.BLOCKS) {
			if (block instanceof ICustomModelSupplier) {

				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) block);

				// Loop through all the blockstates and override their models if they have an
				// override.
				for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
					if (supplier.hasModelOverride(blockState)) {
						// Get the existing model.
						ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
						BakedModel existingModel = event.getModelRegistry().get(variantMRL);
						BakedModel override = supplier.getModelOverride(blockState, existingModel, event);
						if (override != null) {
							event.getModelRegistry().put(variantMRL, override);
						} else {
							LOGGER.error(String.format("Encountered null model override for block: %1$s.", block.getName().getString()));
						}
					}
				}
			}
		}
		for (Item item : StaticPowerRegistry.ITEMS) {
			if (item instanceof ICustomModelSupplier) {

				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) item);

				if (supplier.hasModelOverride(null)) {
					// Get the existing model.
					ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
					BakedModel existingModel = supplier.getBaseModelOverride(event);
					if (existingModel == null) {
						existingModel = event.getModelManager().getModel(modelLocation);
					}

					if (existingModel != null) {
						BakedModel override = supplier.getModelOverride(null, existingModel, event);
						event.getModelRegistry().put(modelLocation, override);
					}
				}
			}
		}
	}

	public static void onItemColorBakeEvent(ColorHandlerEvent.Item event) {
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.IronFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.BasicFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.AdvancedFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.StaticFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.EnergizedFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.LumumFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.CreativeFluidCapsule);
	}

	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {
			int spriteCount = 0;
			for (ResourceLocation sprite : StaticPowerSprites.SPRITES) {
				try {
					event.addSprite(sprite);
					spriteCount++;
				} catch (Exception e) {
					LOGGER.error(String.format("Failed to register texture: %1$s to the texture atlas.", sprite));
				}
			}
			LOGGER.info(String.format("Registered %1$s Static Power sprites.", spriteCount));
		}
	}

}
