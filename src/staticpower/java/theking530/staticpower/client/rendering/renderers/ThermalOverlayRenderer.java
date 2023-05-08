package theking530.staticpower.client.rendering.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import theking530.staticcore.client.rendering.BlockModel;
import theking530.staticcore.client.rendering.ICustomRenderer;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;
import theking530.staticcore.climate.ClimateManager;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.RaytracingUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.items.tools.Thermometer;

public class ThermalOverlayRenderer implements ICustomRenderer {
	private final DefaultRenderer renderer;

	public ThermalOverlayRenderer() {
		renderer = new DefaultRenderer();
	}

	@SuppressWarnings({ "resource" })
	public void render(Level level, RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_PARTICLES && event.getStage() != Stage.AFTER_CUTOUT_BLOCKS) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof Thermometer)) {
			return;
		}

		Minecraft.getInstance().getProfiler().push("StaticPower.ThermalOverlayRenderer");

		BlockHitResult playerRayTrace = RaytracingUtilities.findPlayerRayTrace(level, player, Fluid.NONE);

		Minecraft.getInstance().getProfiler().push("StaticPower.ThermalOverlayRenderer.Rendering");
		{
			MultiBufferSource.BufferSource buffer = event.getLevelRenderer().renderBuffers.bufferSource();
			int radius = 1;
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						BlockPos position = playerRayTrace.getBlockPos().offset(x, y, z);
						event.getPoseStack().pushPose();
						event.getPoseStack().translate(position.getX(), position.getY(), position.getZ());
						renderer.render(position, playerRayTrace.getBlockPos().equals(position), level, player,
								event.getPoseStack(), buffer, event.getStage());
						event.getPoseStack().popPose();

					}
				}
			}
		}
		Minecraft.getInstance().getProfiler().pop();

		Minecraft.getInstance().getProfiler().pop();
	}

	public class DefaultRenderer {

		public void render(BlockPos pos, boolean isFocused, Level level, LocalPlayer player, PoseStack stack,
				BufferSource buffer, Stage renderStage) {
			float temperature = ClimateManager.get(level).getClimateData(level).getChunk(pos).getState(pos)
					.getTemperature();

			if (renderStage == Stage.AFTER_CUTOUT_BLOCKS) {
				stack.pushPose();

				stack.translate(0.5f, 1.1f, 0.5f);
				stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
				stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

				Vector3D offset = new Vector3D(0.0f, 0.0f, 0.0f);
				if (isFocused) {
					offset.add(0, 0.1f, 0);
					drawString(GuiTextUtilities.formatHeatToString(temperature).getString(), SDColor.EIGHT_BIT_RED,
							offset, 0.01f, stack, buffer, true);
				}

				stack.popPose();
			}

			if (isFocused && renderStage == Stage.AFTER_PARTICLES) {
				BlockModel.drawCubeInWorld(stack, new Vector3f(-0.001f, -0.001f, -0.001f),
						new Vector3f(1.002f, 1.002f, 1.002f), new SDColor(0, 1, 0.1f, 0.25f));
			}
		}

		private void drawString(String string, SDColor color, Vector3D offset, float scale, PoseStack stack,
				BufferSource buffer, boolean withShadow) {
			if (withShadow) {
				Vector3D shadowOffset = offset.copy();
				shadowOffset.add(scale / 1.5f, -scale / 1.5f, -scale / 1.5f);
				WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
						string, SDColor.EIGHT_BIT_BLACK, shadowOffset, scale, stack, buffer, 0, 0);
			}

			WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(), string,
					color, offset, scale, stack, buffer, 0, 0);
		}
	}
}
