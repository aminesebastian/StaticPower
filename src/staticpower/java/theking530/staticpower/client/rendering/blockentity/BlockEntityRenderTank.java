package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.nonpowered.tank.BlockEntityTank;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderTank extends StaticPowerBlockEntitySpecialRenderer<BlockEntityTank> {

	public BlockEntityRenderTank(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityTank tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Tank");
		// Render the contained fluid if it exists.
		if (tileEntity.fluidTankComponent.getVisualFillLevel() > 0) {
			// Get the fluid.
			FluidStack fluid = tileEntity.fluidTankComponent.getFluid();

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			fluidColor.setAlpha(1.0f); // Render color as opaque so only the texture controls opacity.
			boolean isGas = fluid.getFluid().getFluidType().isLighterThanAir();

			// Calculate the height and position, then render.
			float height = tileEntity.fluidTankComponent.getVisualFillLevel();

			float yPosition = isGas ? 14.0f * TEXEL - (12.01f * TEXEL * height) : 1.99f * TEXEL;
			BlockModel.drawCubeInWorld(matrixStack, new Vector3f(2.01f * TEXEL, yPosition, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, 11.98f * TEXEL * height, 11.95f * TEXEL),
					fluidColor, sprite, new Vector3D(1.0f, height, 1.0f), pos);
		}

		// Draw the glass. We have to do it like this because of how minecraft orders
		// transparency.
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		BlockModel.drawCubeInWorld(matrixStack, new Vector3f(1.95f * TEXEL, 2f * TEXEL, 1.95f * TEXEL), new Vector3f(12.1f * TEXEL, 12.1f * TEXEL, 12.1f * TEXEL),
				new SDColor(0.4f, 0.45f, 0.55f, 0.35f), sprite);
		Minecraft.getInstance().getProfiler().pop();
	}
}
