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
import theking530.staticpower.blockentities.machines.pump.BlockEntityPump;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderFluidPump extends StaticPowerBlockEntitySpecialRenderer<BlockEntityPump> {

	public BlockEntityRenderFluidPump(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityPump tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.FluidPump");
		// Render the contained fluid if it exists.
		if (tileEntity.fluidTankComponent.getVisualFillLevel() > 0) {
			// Get the fluid.
			FluidStack fluid = tileEntity.fluidTankComponent.getFluid();

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			fluidColor.setAlpha(0.75f); // Render color as opaque so only the texture controls opacity.
			boolean isGas = fluid.getFluid().getFluidType().isLighterThanAir();

			// Calculate the height and position, then render.
			float height = tileEntity.fluidTankComponent.getVisualFillLevel();
			float yPosition = isGas ? 12.0f * TEXEL - (8.01f * TEXEL * height) : 4.55f * TEXEL;
			BlockModel.drawPreviewCube(matrixStack, new Vector3f(4.05f * TEXEL, yPosition, 4.55f * TEXEL), new Vector3f(7.9f * TEXEL, 7.05f * TEXEL * height, 6.95f * TEXEL),
					fluidColor, sprite, new Vector3D(1.0f, height, 1.0f), pos);
		}

		// Draw the glass. We have to do it like this because of how minecraft orders
		// transparency.
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		BlockModel.drawCubeInWorld(matrixStack,
				new Vector3f(4f * TEXEL, 4f * TEXEL, 4f * TEXEL), new Vector3f(8f * TEXEL, 8f * TEXEL, 8f * TEXEL),
				new SDColor(0.4f, 0.45f, 0.55f, 0.5f), sprite);
		Minecraft.getInstance().getProfiler().pop();
	}
}
