package theking530.staticcore.events;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticcore.StaticCore;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.init.StaticCoreKeyBindings;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.GuiProductionMenu;
import theking530.staticcore.productivity.ProductMetricTileRendererRegistry;
import theking530.staticcore.productivity.ProductMetricTileRendererRegistry.RegisterEvent;
import theking530.staticcore.productivity.product.fluid.FluidStackProductMetricRenderer;
import theking530.staticcore.productivity.product.item.ItemStackProductMetricRenderer;
import theking530.staticcore.productivity.product.power.PowerProductMetricRenderer;
import theking530.staticcore.research.gui.ActiveResearchHUD;
import theking530.staticcore.research.gui.GuiResearchMenu;

@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StaticCoreModClientEvents {

	@SubscribeEvent
	public static void textureStitchEvent(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {
			int spriteCount = 0;
			for (ResourceLocation sprite : StaticCoreSprites.SPRITES) {
				try {
					event.addSprite(sprite);
					spriteCount++;
				} catch (Exception e) {
					StaticCore.LOGGER.error(String.format("Failed to register texture: %1$s to the texture atlas.", sprite));
				}
			}
			StaticCore.LOGGER.info(String.format("Registered %1$s Static Core sprites.", spriteCount));
		}
		StaticCore.LOGGER.info("Static Core Texture Stitch Event Completed!");
	}

	@SubscribeEvent
	public static void registerProductMetricRenderers(ProductMetricTileRendererRegistry.RegisterEvent event) {
		event.registerProductMetricRenderer(StaticCoreProductTypes.Item, () -> new ItemStackProductMetricRenderer());
		event.registerProductMetricRenderer(StaticCoreProductTypes.Fluid, () -> new FluidStackProductMetricRenderer());
		event.registerProductMetricRenderer(StaticCoreProductTypes.Power, () -> new PowerProductMetricRenderer());
	}



	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		StaticCoreForgeBusClient.addHUDElement(new ActiveResearchHUD());

		// TODO: Build a system to handle this non-manually.
		StaticCoreKeyBindings.addCallback(StaticCoreKeyBindings.OPEN_RESEARCH, (binding) -> {
			if (binding.wasJustPressed()) {
				if (Minecraft.getInstance().screen == null) {
					Minecraft.getInstance().setScreen(new GuiResearchMenu());
				} else if (Minecraft.getInstance().screen instanceof GuiResearchMenu) {
					Minecraft.getInstance().screen.onClose();
				}
			}
		});
		StaticCoreKeyBindings.addCallback(StaticCoreKeyBindings.OPEN_PRODUCTION, (binding) -> {
			if (binding.wasJustPressed()) {
				if (Minecraft.getInstance().screen == null) {
					Minecraft.getInstance().setScreen(new GuiProductionMenu());
				} else if (Minecraft.getInstance().screen instanceof GuiProductionMenu) {
					Minecraft.getInstance().screen.onClose();
				}
			}
		});

		// Fire event to register metric renderers.
		ModLoader.get().postEvent(new ProductMetricTileRendererRegistry.RegisterEvent());
		RegisterEvent.finalizeRegistration();
	}
}
