package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.blockentities.machines.fluidinfuser.BlockEntityFluidInfuser;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderFluidInfuser extends StaticPowerBlockEntitySpecialRenderer<BlockEntityFluidInfuser> {

	public BlockEntityRenderFluidInfuser(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityFluidInfuser tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.FluidInfuser");
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();

			float firstSectionFilledPercentage = SDMath.clamp(filledPercentage, 0.0f, 0.2f) * 5.0f;
			float secondSectionFilledPercentage = SDMath.clamp(filledPercentage - 0.2f, 0.0f, 0.6f) * 1.665f;
			float lastSectionFilledPercentage = SDMath.clamp(filledPercentage - 0.8f, 0.0f, 0.2f) * 5.0f;

			float firstSectionHeight = firstSectionFilledPercentage * 0.095f;
			float secondSectionHeight = secondSectionFilledPercentage * 0.284f;
			float lastSectionHeight = lastSectionFilledPercentage * 0.095f;

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.3125f, 0.186f, 0.001f),
					new Vector3D(0.376f, firstSectionHeight, 1.0f), new Vector4D(0.0f, 0.0f, 1.0f, firstSectionFilledPercentage * 0.45f),
					WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.218f, 0.28f, 0.001f),
					new Vector3D(0.564f, secondSectionHeight, 1.0f), new Vector4D(0.0f, 0.0f, 1.0f, secondSectionFilledPercentage * 0.75f),
					WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.3125f, 0.562f, 0.001f),
					new Vector3D(0.376f, lastSectionHeight, 1.0f), new Vector4D(0.0f, 0.0f, 1.0f, lastSectionFilledPercentage * 0.45f),
					WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));

			// Render the item inside the infuser.
			OldProcessingContainer processingContainer = tileEntity.processingComponent.getProcessingMaterials();
			if (processingContainer.hasInputItems()) {
				ItemStack inputItem = processingContainer.getInputItem(0).item();
				// Get the baked model and check if it wants to render the item in 3d or 2d.
				BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(inputItem, null, null, combinedOverlay);
				boolean render3D = itemModel.isGui3d();

				int forwardBlockLightLevel = LevelRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().relative(tileEntity.getFacingDirection()));

				if (render3D) {
					WorldRenderingUtilities.drawItemInWorld(tileEntity, inputItem, TransformType.GUI, new Vector3D(0.5f, 0.42f, 1.005f), new Vector3D(0.3f, 0.3f, 0.02f),
							partialTicks, matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				} else {
					WorldRenderingUtilities.drawItemInWorld(tileEntity, inputItem, TransformType.GUI, new Vector3D(0.5f, 0.42f, 1.005f), new Vector3D(0.3f, 0.3f, 0.16f),
							partialTicks, matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				}
			}
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
