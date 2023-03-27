package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.BlockModel;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.nonpowered.experiencehopper.BlockEntityExperienceHopper;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderHopper extends StaticPowerBlockEntitySpecialRenderer<BlockEntityExperienceHopper> {

	public BlockEntityRenderHopper(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityExperienceHopper tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLight, int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Hopper");
		// Render the contained fluid if it exists.
		if (tileEntity.internalTank.getVisualFillLevel() > 0) {
			// Get the fluid.
			FluidStack fluid = tileEntity.internalTank.getFluid();

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);

			// Calculate the hight and position, then render.
			float height = tileEntity.internalTank.getVisualFillLevel();
			float yPosition = TEXEL * 11f;
			BlockModel.drawCubeInWorld(matrixStack, new Vector3f(2.01f * TEXEL, yPosition, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, height * TEXEL * 4.8f, 11.95f * TEXEL),
					fluidColor, sprite, new Vector3D(1.0f, height, 1.0f));
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
