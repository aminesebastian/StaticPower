package theking530.staticpower.client.rendering.renderers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class ElectricalOverlayRenderer implements ICustomRenderer {
	@SuppressWarnings("rawtypes")
	private static final Map<Class<BlockEntity>, IBlockEntityMultimeterRenderer> RENDERERS = new HashMap<>();

	public ElectricalOverlayRenderer() {
		RENDERERS.put(BlockEntity.class, new DefaultRenderer());
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public void render(Level level, RenderLevelStageEvent event) {
		if (event.getStage() != Stage.AFTER_PARTICLES) {
			return;
		}

		Minecraft.getInstance().getProfiler().push("StaticPower.ElectricalOverlayRendering");
		LocalPlayer player = Minecraft.getInstance().player;
		if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof Multimeter)) {
			// return;
		}

		Map<BlockEntity, IBlockEntityMultimeterRenderer> entities = new HashMap<>();

		int radius = 15;
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius / 2; y < radius / 2; y++) {
				for (int z = -radius; z < radius; z++) {
					BlockEntity testBe = level.getBlockEntity(player.getOnPos().offset(x, y, z));
					if (testBe != null) {
						IBlockEntityMultimeterRenderer renderer = getRendererForBlockEntity(testBe);
						if (renderer != null) {
							entities.put(testBe, renderer);
						}
					}
				}
			}
		}

		MultiBufferSource.BufferSource buffer = event.getLevelRenderer().renderBuffers.bufferSource();
		float playerRot = player.getViewYRot(1.0f) + 180;
		for (Entry<BlockEntity, IBlockEntityMultimeterRenderer> entry : entities.entrySet()) {
			Vector3D position = new Vector3D(entry.getKey().getBlockPos());
			event.getPoseStack().pushPose();
			event.getPoseStack().translate(position.getX(), position.getY(), position.getZ());
			entry.getValue().render(entry.getKey(), level, player, event.getPoseStack(), buffer);
			event.getPoseStack().popPose();
		}

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
		public void render(BlockEntity blockEntity, Level level, LocalPlayer player, PoseStack stack, BufferSource buffer) {
			IStaticPowerStorage storage = blockEntity.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).orElse(null);
			if (storage == null || storage.getEnergyTracker() == null) {
				return;
			}


			stack.pushPose();

			stack.translate(0.5f, 1.0f, 0.5f);
			stack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -player.getViewYRot(1.0f) + 180, true));

			Vector3D offset = new Vector3D(0.0f, 0.1f, 0.0f);
			WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
					PowerTextFormatting.formatVoltageToString(storage.getOutputVoltage()).getString(), SDColor.EIGHT_BIT_WHITE, offset, 0.01f, stack, buffer, 0, 0);

			offset.add(0, 0.1f, 0);
			WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
					PowerTextFormatting.formatPowerRateToString(storage.getEnergyTracker().getAveragePowerUsedPerTick()).getString(), SDColor.EIGHT_BIT_RED, offset, 0.01f, stack,
					buffer, 0, 0);

			offset.add(0, 0.1f, 0);
			WorldRenderingUtilities.drawTextInWorld(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
					PowerTextFormatting.formatPowerRateToString(storage.getEnergyTracker().getAveragePowerAddedPerTick()).getString(), SDColor.EIGHT_BIT_WHITE, offset, 0.01f,
					stack, buffer, 0, 0);

			stack.popPose();
			BlockModel.drawCubeInWorld(stack, new Vector3f(-0.001f, -0.001f, -0.001f), new Vector3f(1.002f, 1.002f, 1.002f), new SDColor(0, 1, 0.1f, 0.15f));
		}

		@Override
		public void renderCameraFacingText(BlockEntity blockEntity, Level level, LocalPlayer player, PoseStack stack, BufferSource buffer) {

		}
	}
}
