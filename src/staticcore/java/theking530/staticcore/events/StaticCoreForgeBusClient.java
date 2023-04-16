package theking530.staticcore.events;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import theking530.staticcore.StaticCore;
import theking530.staticcore.client.rendering.ICustomRenderer;
import theking530.staticcore.data.gamedata.StaticCoreGameDataManager.StaticCoreDataAccessor;
import theking530.staticcore.gui.screens.StaticCoreHUDElement;

@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class StaticCoreForgeBusClient {
	private static final Set<StaticCoreHUDElement> HUD_ELEMENTS = new HashSet<>();
	private static final Set<ICustomRenderer> CUSTOM_RENDERERS = new HashSet<>();

	public static void addHUDElement(StaticCoreHUDElement element) {
		HUD_ELEMENTS.add(element);
	}

	public static void addCustomRenderer(ICustomRenderer renderer) {
		CUSTOM_RENDERERS.add(renderer);
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void clientTickEvent(TickEvent.ClientTickEvent event) {
		Level level = Minecraft.getInstance().level;

		if (level != null) {
			StaticCoreDataAccessor.getClient().tickGameData(Minecraft.getInstance().level);
		}
	}

	@SubscribeEvent
	public static void onClientPlayerJoinServer(ClientPlayerNetworkEvent.LoggingIn event) {
		StaticCoreDataAccessor.createForClient();
	}

	@SubscribeEvent
	public static void onClientPlayerLeavingServer(ClientPlayerNetworkEvent.LoggingOut event) {
		StaticCoreDataAccessor.unloadForClient();
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void render(RenderLevelStageEvent event) {
		// Start our own stack entry and project us into world space.
		event.getPoseStack().pushPose();
		Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		event.getPoseStack().translate(-projectedView.x, -projectedView.y, -projectedView.z);

		// Renderer all the renderers.
		for (ICustomRenderer renderer : CUSTOM_RENDERERS) {
			renderer.render(Minecraft.getInstance().level, event);
		}

		// Pop our stack entry.
		event.getPoseStack().popPose();
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void onDrawHUD(RenderGuiOverlayEvent.Post event) {
		if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
			for (StaticCoreHUDElement gui : HUD_ELEMENTS) {
				gui.setCurrentWindow(event.getWindow());
				gui.tick();
				gui.renderBackground(event.getPoseStack());
				gui.render(event.getPoseStack(), 0, 0, event.getPartialTick());
			}
		}
	}
}
