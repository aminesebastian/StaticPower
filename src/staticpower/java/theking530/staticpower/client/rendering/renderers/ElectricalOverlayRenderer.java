package theking530.staticpower.client.rendering.renderers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IBlockEntityMultimeterRenderer;
import theking530.api.energy.IStaticPowerStorage;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.items.tools.Multimeter;
import theking530.staticpower.utilities.RaytracingUtilities;

public class ElectricalOverlayRenderer implements ICustomRenderer {
	@SuppressWarnings("rawtypes")
	private static final Map<Class<BlockEntity>, IBlockEntityMultimeterRenderer> RENDERERS = new HashMap<>();

	public ElectricalOverlayRenderer() {
		RENDERERS.put(BlockEntity.class, new DefaultRenderer());
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public void render(Level level, RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_PARTICLES && event.getStage() != Stage.AFTER_CUTOUT_BLOCKS) {
			return;
		}

		Minecraft.getInstance().getProfiler().push("StaticPower.ElectricalOverlayRenderer");
		LocalPlayer player = Minecraft.getInstance().player;
		if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof Multimeter)) {
			return;
		}

		BlockHitResult playerRayTrace = RaytracingUtilities.findPlayerRayTrace(level, player, Fluid.NONE);

		Minecraft.getInstance().getProfiler().push("StaticPower.ElectricalOverlayRenderer.EntityCaching");
		Map<BlockEntity, IBlockEntityMultimeterRenderer> entities = new HashMap<>();

		int radius = 1;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos scanPosition = playerRayTrace.getBlockPos().offset(x, y, z);
					BlockEntity blockEntity = level.getBlockEntity(scanPosition);
					if (blockEntity != null) {
						IBlockEntityMultimeterRenderer renderer = getRendererForBlockEntity(blockEntity);
						if (renderer != null) {
							entities.put(blockEntity, renderer);
						}
					}
				}
			}
		}
		Minecraft.getInstance().getProfiler().pop();

		Minecraft.getInstance().getProfiler().push("StaticPower.ElectricalOverlayRenderer.Rendering");
		MultiBufferSource.BufferSource buffer = event.getLevelRenderer().renderBuffers.bufferSource();
		for (Entry<BlockEntity, IBlockEntityMultimeterRenderer> entry : entities.entrySet()) {
			BlockPos position = entry.getKey().getBlockPos();
			event.getPoseStack().pushPose();
			event.getPoseStack().translate(position.getX(), position.getY(), position.getZ());
			entry.getValue().render(entry.getKey(), playerRayTrace.getBlockPos().equals(position), level, player, event.getPoseStack(), buffer, event.getStage());
			event.getPoseStack().popPose();
		}
		Minecraft.getInstance().getProfiler().pop();

		Minecraft.getInstance().getProfiler().pop();
	}

	@SuppressWarnings({ "rawtypes" })
	protected IBlockEntityMultimeterRenderer getRendererForBlockEntity(BlockEntity be) {
		if (RENDERERS.containsKey(be.getClass())) {
			RENDERERS.get(be.getClass());
		}

		for (Entry<Class<BlockEntity>, IBlockEntityMultimeterRenderer> entry : RENDERERS.entrySet()) {
			if (entry.getKey().isAssignableFrom(be.getClass())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public class DefaultRenderer implements IBlockEntityMultimeterRenderer<BlockEntity> {

		@Override
		public void render(BlockEntity blockEntity, boolean isFocused, Level level, LocalPlayer player, PoseStack stack, BufferSource buffer, Stage renderStage) {
			IStaticPowerStorage storage = blockEntity.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).orElse(null);
			if (storage == null || storage.getEnergyTracker() == null) {
				return;
			}

			if (renderStage == Stage.AFTER_CUTOUT_BLOCKS) {
				stack.pushPose();

				stack.translate(0.5f, 1.1f, 0.5f);
				stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
				stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

				Vector3D offset = new Vector3D(0.0f, 0.0f, 0.0f);
				if (isFocused) {
					offset.add(0, 0.1f, 0);
					drawString(PowerTextFormatting.formatPowerRateToString(storage.getEnergyTracker().getAveragePowerDrainedPerTick()).getString(), SDColor.EIGHT_BIT_RED, offset,
							0.01f, stack, buffer, true);

					offset.add(0, 0.1f, 0);
					drawString(PowerTextFormatting.formatPowerRateToString(storage.getEnergyTracker().getAveragePowerAddedPerTick()).getString(), SDColor.EIGHT_BIT_WHITE, offset,
							0.01f, stack, buffer, true);
				} else {
					double delta = storage.getEnergyTracker().getAveragePowerAddedPerTick() + storage.getEnergyTracker().getAveragePowerDrainedPerTick();
					drawString(PowerTextFormatting.formatPowerRateToString(delta).getString(), SDColor.EIGHT_BIT_WHITE, offset, 0.01f, stack, buffer, true);
				}

				stack.popPose();
			}

			if (isFocused && renderStage == Stage.AFTER_PARTICLES) {
				BlockModel.drawCubeInWorld(stack, new Vector3f(-0.001f, -0.001f, -0.001f), new Vector3f(1.002f, 1.002f, 1.002f), new SDColor(0, 1, 0.1f, 0.25f));
			}
		}

		private void drawString(String string, SDColor color, Vector3D offset, float scale, PoseStack stack, BufferSource buffer, boolean withShadow) {
			if (withShadow) {
				Vector3D shadowOffset = offset.copy();
				shadowOffset.add(scale / 1.5f, -scale / 1.5f, -scale / 1.5f);
				WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(), string, SDColor.EIGHT_BIT_BLACK, shadowOffset, scale, stack,
						buffer, 0, 0);
			}

			WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(), string, color, offset, scale, stack, buffer, 0, 0);
		}
	}
}
