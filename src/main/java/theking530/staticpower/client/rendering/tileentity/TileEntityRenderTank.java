package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.nonpowered.tank.TileEntityTank;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderTank extends StaticPowerTileEntitySpecialRenderer<TileEntityTank> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	public TileEntityRenderTank(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(TileEntityTank tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		// Render the contained fluid if it exists.
		if (tileEntity.fluidTankComponent.getVisualFillLevel() > 0) {
			// Get the fluid.
			FluidStack fluid = tileEntity.fluidTankComponent.getFluid();

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			boolean isGas = fluid.getFluid().getAttributes().isGaseous();

			// Calculate the hight and position, then render.
			float height = tileEntity.fluidTankComponent.getVisualFillLevel(); 

			float yPosition = isGas ? 14.0f * TEXEL - (12.01f * TEXEL * height) : 1.99f * TEXEL;
			CUBE_MODEL.drawPreviewCube(new Vector3f(2.01f * TEXEL, yPosition, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, 11.98f * TEXEL * height, 11.95f * TEXEL), fluidColor, matrixStack,
					sprite, new Vector3D(1.0f, height, 1.0f));
			
		}

		// Draw the glass. We have to do it like this because of how mineraft orders
		// transparency.
		if (ForgeModelBakery.instance() != null && ForgeModelBakery.instance().getSpriteMap() != null) {
			@SuppressWarnings("deprecation")
			TextureAtlas blocksTexture = ForgeModelBakery.instance().getSpriteMap().getAtlas(TextureAtlas.LOCATION_BLOCKS);
			TextureAtlasSprite sprite = blocksTexture.getSprite(StaticPowerSprites.BLANK_TEXTURE);
			CUBE_MODEL.drawPreviewCube(new Vector3f(1.95f * TEXEL, 2f * TEXEL, 1.95f * TEXEL), new Vector3f(12.1f * TEXEL, 12.1f * TEXEL, 12.1f * TEXEL),
					new Color(0.4f, 0.45f, 0.55f, 0.35f), matrixStack, sprite);
		}
	}
}
