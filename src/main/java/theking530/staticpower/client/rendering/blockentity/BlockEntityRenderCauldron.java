package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.nonpowered.cauldron.BlockEntityCauldron;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderCauldron extends StaticPowerBlockEntitySpecialRenderer<BlockEntityCauldron> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	public BlockEntityRenderCauldron(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityCauldron tileEntity, BlockPos pos, float partialTicks,
			PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		// Render the contained fluid if it exists.
		if (tileEntity.internalTank.getFluidAmount() > 0) {
			// Get the fluid.
			FluidStack fluid = tileEntity.internalTank.getFluid();

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);

			// Calculate the height and position, then render.
			float height = tileEntity.internalTank.getVisualFillLevel();
			CUBE_MODEL.drawPreviewCube(new Vector3f(2 * TEXEL, 4 * TEXEL, 2 * TEXEL),
					new Vector3f(12 * TEXEL, height * 11 * TEXEL, 12 * TEXEL), fluidColor, matrixStack, sprite,
					new Vector3D(1.0f, height, 1.0f));
		}
	}
}
