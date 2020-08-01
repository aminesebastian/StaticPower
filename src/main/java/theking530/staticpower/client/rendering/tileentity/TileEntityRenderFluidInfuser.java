package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.SDMath;
import theking530.common.utilities.Vector3D;
import theking530.common.utilities.Vector4D;
import theking530.staticpower.tileentities.powered.fluidinfuser.TileEntityFluidInfuser;

@SuppressWarnings("deprecation")
public class TileEntityRenderFluidInfuser extends StaticPowerTileEntitySpecialRenderer<TileEntityFluidInfuser> {

	public TileEntityRenderFluidInfuser(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityFluidInfuser tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();

			float firstSectionFilledPercentage = SDMath.clamp(filledPercentage, 0.0f, 0.2f) * 5.0f;
			float secondSectionFilledPercentage = SDMath.clamp(filledPercentage - 0.2f, 0.0f, 0.6f) * 1.665f;
			float lastSectionFilledPercentage = SDMath.clamp(filledPercentage - 0.8f, 0.0f, 0.2f) * 5.0f;

			float firstSectionHeight = firstSectionFilledPercentage * 0.095f;
			float secondSectionHeight = secondSectionFilledPercentage * 0.284f;
			float lastSectionHeight = lastSectionFilledPercentage * 0.095f;

			drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.3125f, 0.186f, 0.001f), new Vector3D(0.376f, firstSectionHeight, 1.0f),
					new Vector4D(0.0f, 0.0f, 1.0f, firstSectionFilledPercentage * 0.45f));

			drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.218f, 0.28f, 0.001f), new Vector3D(0.564f, secondSectionHeight, 1.0f),
					new Vector4D(0.0f, 0.0f, 1.0f, secondSectionFilledPercentage * 0.75f));

			drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.3125f, 0.562f, 0.001f), new Vector3D(0.376f, lastSectionHeight, 1.0f),
					new Vector4D(0.0f, 0.0f, 1.0f, lastSectionFilledPercentage * 0.45f));

			// Render the item inside the infuser.
			if (!tileEntity.internalInventory.getStackInSlot(0).isEmpty()) {
				// Get the baked model and check if it wants to render the item in 3d or 2d.
				IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tileEntity.internalInventory.getStackInSlot(0), null, null);
				boolean render3D = itemModel.isGui3d();

				int forwardBlockLightLevel = WorldRenderer.getCombinedLight(tileEntity.getWorld(), tileEntity.getPos().offset(tileEntity.getFacingDirection()));

				if (render3D) {
					drawItemInWorld(tileEntity, tileEntity.internalInventory.getStackInSlot(0), TransformType.GUI, new Vector3D(0.5f, 0.42f, 1.005f), new Vector3D(0.3f, 0.3f, 0.02f), partialTicks,
							matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				} else {
					drawItemInWorld(tileEntity, tileEntity.internalInventory.getStackInSlot(0), TransformType.GUI, new Vector3D(0.5f, 0.42f, 1.005f), new Vector3D(0.3f, 0.3f, 0.16f), partialTicks,
							matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				}
			}
		}
	}
}
