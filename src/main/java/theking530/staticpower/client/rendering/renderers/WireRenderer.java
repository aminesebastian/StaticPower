package theking530.staticpower.client.rendering.renderers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.WireRenderCache;

public class WireRenderer implements ICustomRenderer {
	private static final Map<BlockPos, Map<Long, WireRenderCache>> WIRE_RENDER_CACHE = new HashMap<>();

	public static void addWireRenderCache(BlockPos pos, Long id, WireRenderCache cache) {
		if (!WIRE_RENDER_CACHE.containsKey(pos)) {
			WIRE_RENDER_CACHE.put(pos, new HashMap<Long, WireRenderCache>());
		}

		if (WIRE_RENDER_CACHE.get(pos).containsKey(id)) {
			StaticPower.LOGGER.error(String.format("Attempted to add a wire render request at position: %1$s with duplicate id: %2$d.", pos.toString(), id));
		} else {
			WIRE_RENDER_CACHE.get(pos).put(id, cache);
		}
	}

	public static boolean removeWireRenderCache(BlockPos pos) {
		return WIRE_RENDER_CACHE.remove(pos) != null;
	}

	public void render(Level level, RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_TRANSLUCENT_BLOCKS) {
			return;
		}

		// Set up for rendering only once per render request.
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableCull();
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		// Duplicate the list to avoid situation where we remove from the game thread.
		// Don't want to hit a concurrent modification exception.
		Collection<WireRenderCache> values = WIRE_RENDER_CACHE.values().stream().flatMap(x -> x.values().stream()).toList();
		for (WireRenderCache cache : values) {
			cache.invalidateLightingCache();
			cache.render(level, event.getPoseStack(), event.getCamera(), event.getFrustum(), bufferBuilder);
		}

		// Reset the render flags and close the tesselator when done.
		tesselator.end();
		RenderSystem.enableCull();
	}
}
