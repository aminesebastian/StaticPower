package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blockentities.nonpowered.chest.BlockEntityStaticChest;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockEntityRenderStaticChest extends StaticPowerBlockEntitySpecialRenderer<BlockEntityStaticChest> {
	private static final Material BASIC_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, StaticPowerSprites.BASIC_CHEST);
	private static final Material ADVANCED_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, StaticPowerSprites.ADVANCED_CHEST);
	private static final Material STATIC_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, StaticPowerSprites.STATIC_CHEST);
	private static final Material ENERGIZED_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, StaticPowerSprites.ENERGIZED_CHEST);
	private static final Material LUMUM_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, StaticPowerSprites.LUMUM_CHEST);
	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;

	public BlockEntityRenderStaticChest(BlockEntityRendererProvider.Context context) {
		super(context);
		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = modelpart.getChild("bottom");
		this.lid = modelpart.getChild("lid");
		this.lock = modelpart.getChild("lock");
	}

	@Override
	protected void renderTileEntityBase(BlockEntityStaticChest te, BlockPos position, float partialTicks, PoseStack pos, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Chest");
		float interpretedOpenAlpha = 0.0f;

		if (te.isOpen()) {
			interpretedOpenAlpha = SDMath.clamp(te.openAlpha + (partialTicks / 10), 0, 1);
		} else if (te.openAlpha > 0) {
			interpretedOpenAlpha = SDMath.clamp(te.openAlpha - (partialTicks / 10), 0, 1);
		}

		interpretedOpenAlpha = 1.0F - interpretedOpenAlpha;
		interpretedOpenAlpha = 1.0F - interpretedOpenAlpha * interpretedOpenAlpha * interpretedOpenAlpha;

		Material material = BASIC_MATERIAL;
		if (te.getTier() == StaticPowerTiers.ADVANCED) {
			material = ADVANCED_MATERIAL;
		} else if (te.getTier() == StaticPowerTiers.STATIC) {
			material = STATIC_MATERIAL;
		} else if (te.getTier() == StaticPowerTiers.ENERGIZED) {
			material = ENERGIZED_MATERIAL;
		} else if (te.getTier() == StaticPowerTiers.LUMUM) {
			material = LUMUM_MATERIAL;
		}

		VertexConsumer vertexconsumer = material.buffer(buffer, RenderType::entityCutout);
		render(pos, vertexconsumer, this.lid, this.lock, this.bottom, interpretedOpenAlpha, combinedLight, combinedOverlay);
		Minecraft.getInstance().getProfiler().pop();
	}

	private void render(PoseStack stack, VertexConsumer consumer, ModelPart lid, ModelPart lock, ModelPart bottom, float lidRotation, int light, int overlay) {
		lid.xRot = -(lidRotation * ((float) Math.PI / 2F));
		lock.xRot = lid.xRot;

		stack.pushPose();
		stack.translate(0, 2.79f * (1.0 / 16.0), 0);
		stack.scale(1, 0.8f, 1);
		lid.render(stack, consumer, light, overlay);
		stack.popPose();

		stack.pushPose();
		stack.translate(0, -0.115f * lidRotation, 0.0);
		lock.render(stack, consumer, light, overlay);
		stack.translate(-0.2, 0, 0);
		lock.render(stack, consumer, light, overlay);
		stack.translate(0.4, 0, 0);
		lock.render(stack, consumer, light, overlay);
		stack.popPose();

		bottom.render(stack, consumer, light, overlay);
	}
}