package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.nonpowered.experiencehopper.TileEntityExperienceHopper;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderHopper extends StaticPowerTileEntitySpecialRenderer<TileEntityExperienceHopper> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	public TileEntityRenderHopper(BlockEntityRenderDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityExperienceHopper tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		// Render the contained fluid if it exists.
		if (tileEntity.internalTank.getVisualFillLevel() > 0) {
			// Get the fluid.
			FluidStack fluid = tileEntity.internalTank.getFluid();

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);

			// Calculate the hight and position, then render.
			float height = tileEntity.internalTank.getVisualFillLevel();
			float yPosition = TEXEL * 11f;
			CUBE_MODEL.drawPreviewCube(new Vector3f(2.01f * TEXEL, yPosition, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, height * TEXEL * 4.8f, 11.95f * TEXEL), fluidColor, matrixStack,
					sprite, new Vector3D(1.0f, height, 1.0f));
		}
	}
}
