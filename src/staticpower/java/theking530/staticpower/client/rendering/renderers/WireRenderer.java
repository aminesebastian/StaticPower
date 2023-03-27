package theking530.staticpower.client.rendering.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import theking530.staticcore.client.rendering.ICustomRenderer;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.WireRenderCache;

public class WireRenderer implements ICustomRenderer {
	private static final Map<Long, WireRenderCache> WIRE_RENDER_CACHE = new HashMap<>();

	public static void addWireRenderCache(WireRenderCache cache) {
		if (WIRE_RENDER_CACHE.containsKey(cache.getLinkId())) {
			StaticPower.LOGGER.warn(String.format(
					"Attempted to add a wire render request at position: %1$s with duplicate id: %2$d. This can safely be ignored -- just a heads up in case you don't want this.",
					cache.getBlockStart().toString(), cache.getLinkId()));
			return;
		}

		WIRE_RENDER_CACHE.put(cache.getLinkId(), cache);
		StaticPower.LOGGER.debug(String.format("Adding wire render cache at position: %1$s with id: %2$d.", cache.getBlockStart().toString(), cache.getLinkId()));
	}

	public static boolean removeWireRenderCache(BlockPos pos) {
		StaticPower.LOGGER.debug(String.format("Removing wire render cache at position: %1$s.", pos.toString()));

		List<Long> toRemove = new LinkedList<>();
		for (Entry<Long, WireRenderCache> entry : WIRE_RENDER_CACHE.entrySet()) {
			if (entry.getValue().getBlockStart().equals(pos) || entry.getValue().getBlockEnd().equals(pos)) {
				toRemove.add(entry.getKey());
			}
		}

		for (Long id : toRemove) {
			WIRE_RENDER_CACHE.remove(id);
		}

		return toRemove.size() > 0;
	}

	public void render(Level level, RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_TRANSLUCENT_BLOCKS) {
			return;
		}

		Minecraft.getInstance().getProfiler().push("StaticPower.WireRendering");
		// Set up for rendering only once per render request.
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableCull();
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		// Duplicate the list to avoid situation where we remove from the game thread.
		// Don't want to hit a concurrent modification exception.
		for (Entry<Long, WireRenderCache> cache : WIRE_RENDER_CACHE.entrySet()) {
			cache.getValue().invalidateLightingCache();
			cache.getValue().render(level, event.getPoseStack(), event.getCamera(), event.getFrustum(), bufferBuilder);
		}

		// Reset the render flags and close the tesselator when done.
		tesselator.end();
		RenderSystem.enableCull();
		Minecraft.getInstance().getProfiler().pop();
	}
}
