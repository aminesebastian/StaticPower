package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.powered.pump.TileEntityPump;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderPump extends StaticPowerTileEntitySpecialRenderer<TileEntityPump> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	public TileEntityRenderPump(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityPump tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (tileEntity.fluidTankComponent.getFluidAmount() > 0) {
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(tileEntity.fluidTankComponent.getFluid());
			Color fluidColor = GuiDrawUtilities.getFluidColor(tileEntity.fluidTankComponent.getFluid());
			float height = tileEntity.fluidTankComponent.getVisualFillLevel();
			CUBE_MODEL.drawPreviewCube(new Vector3f(2.01f * TEXEL, 1.99f * TEXEL, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, 11.98f * TEXEL * height, 11.95f * TEXEL), fluidColor, matrixStack,
					sprite, new Vector2D(1.0f, height));
		}

		// Draw the glass. We have to do it like this because of how mineraft orders
		// transparency.
		if (ModelLoader.instance().getSpriteMap() != null) {
			@SuppressWarnings("deprecation")
			AtlasTexture blocksTexture = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			TextureAtlasSprite sprite = blocksTexture.getSprite(StaticPowerSprites.BLANK_TEXTURE);
			CUBE_MODEL.drawPreviewCube(new Vector3f(1.95f * TEXEL, 2f * TEXEL, 1.95f * TEXEL), new Vector3f(12.1f * TEXEL, 12.1f * TEXEL, 12.1f * TEXEL), new Color(0.4f, 0.45f, 0.55f, 0.35f),
					matrixStack, sprite);
		}
	}
}
