package theking530.staticpower.client.rendering.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.client.rendering.DrawCubeRequest;

public class RadiusPreviewRenderer implements ICustomRenderer {
	private static final HashMap<BlockEntity, HashMap<String, DrawCubeRequest>> CUBE_RENDER_REQUESTS = new HashMap<BlockEntity, HashMap<String, DrawCubeRequest>>();

	public void render(Level level, RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_TRANSLUCENT_BLOCKS) {
			return;
		}

		Minecraft.getInstance().getProfiler().push("StaticPower.RadiusPreviewRenderer");
		// Prepare a list of entries to remove if the te has been removed from the
		// world.
		List<BlockEntity> teEntriesToRemove = new LinkedList<BlockEntity>();

		// Draw the requested cubes.
		for (BlockEntity te : CUBE_RENDER_REQUESTS.keySet()) {
			// If the tile entity is not valid, add it to the list of entries to remove.
			if (te == null || te.isRemoved()) {
				teEntriesToRemove.add(te);
				continue;
			}

			// Draw the request.
			for (DrawCubeRequest request : CUBE_RENDER_REQUESTS.get(te).values()) {
				BlockModel.drawCubeInWorld(event.getPoseStack(), request.Position, request.Scale, request.Color);
			}
		}

		// Purge the old requests.
		for (BlockEntity te : teEntriesToRemove) {
			CUBE_RENDER_REQUESTS.remove(te);
		}
		Minecraft.getInstance().getProfiler().pop();
	}

	public static void addRadiusRenderRequest(BlockEntity tileEntity, String key, Vector3f position, Vector3f scale, SDColor color) {
		if (!CUBE_RENDER_REQUESTS.containsKey(tileEntity)) {
			CUBE_RENDER_REQUESTS.put(tileEntity, new HashMap<String, DrawCubeRequest>());
		}
		CUBE_RENDER_REQUESTS.get(tileEntity).put(key, new DrawCubeRequest(position, scale, color));
	}

	public static void addRadiusRenderRequest(BlockEntity tileEntity, String key, BlockPos position, Vector3f scale, SDColor color) {
		addRadiusRenderRequest(tileEntity, key, new Vector3f(position.getX(), position.getY(), position.getZ()), scale, color);
	}

	public static void removeAllRequestForBlockEntity(BlockEntity tileEntity) {
		CUBE_RENDER_REQUESTS.remove(tileEntity);
	}

	public static void removeRadiusRenderer(BlockEntity tileEntity, String key) {
		while (CUBE_RENDER_REQUESTS.containsKey(tileEntity)) {
			CUBE_RENDER_REQUESTS.get(tileEntity).remove(key);
			if (CUBE_RENDER_REQUESTS.get(tileEntity).isEmpty()) {
				CUBE_RENDER_REQUESTS.remove(tileEntity);
			}
		}
	}
}
