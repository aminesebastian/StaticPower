package theking530.staticpower.client.rendering.blockentity;

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
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.machines.pump.BlockEntityPump;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderPump extends StaticPowerBlockEntitySpecialRenderer<BlockEntityPump> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	public BlockEntityRenderPump(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityPump tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		if (tileEntity.fluidTankComponent.getFluidAmount() > 0) {
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(tileEntity.fluidTankComponent.getFluid());
			Color fluidColor = GuiDrawUtilities.getFluidColor(tileEntity.fluidTankComponent.getFluid());
			float height = tileEntity.fluidTankComponent.getVisualFillLevel();
			CUBE_MODEL.drawPreviewCube(new Vector3f(2.01f * TEXEL, 1.99f * TEXEL, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, 11.98f * TEXEL * height, 11.95f * TEXEL), fluidColor,
					matrixStack, sprite, new Vector3D(1.0f, height, 1.0f));
		}

		// Draw the glass. We have to do it like this because of how mineraft orders
		// transparency.
		if (ForgeModelBakery.instance().getSpriteMap() != null) {
			@SuppressWarnings("deprecation")
			TextureAtlas blocksTexture = ForgeModelBakery.instance().getSpriteMap().getAtlas(TextureAtlas.LOCATION_BLOCKS);
			TextureAtlasSprite sprite = blocksTexture.getSprite(StaticPowerSprites.BLANK_TEXTURE);
			CUBE_MODEL.drawPreviewCube(new Vector3f(1.95f * TEXEL, 2f * TEXEL, 1.95f * TEXEL), new Vector3f(12.1f * TEXEL, 12.1f * TEXEL, 12.1f * TEXEL),
					new Color(0.4f, 0.45f, 0.55f, 0.35f), matrixStack, sprite);
		}
	}
}
