package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.utilities.Color;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.nonpowered.tank.TileEntityTank;

public class TileEntityRenderTank extends StaticPowerTileEntitySpecialRenderer<TileEntityTank> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	public TileEntityRenderTank(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityTank tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (tileEntity.fluidTankComponent.getFluidAmount() > 0) {
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(tileEntity.fluidTankComponent.getFluid());
			Color fluidColor = GuiDrawUtilities.getFluidColor(tileEntity.fluidTankComponent.getFluid());
			float height = (float)tileEntity.fluidTankComponent.getFluidAmount() / tileEntity.fluidTankComponent.getCapacity();
			CUBE_MODEL.drawPreviewCube(new Vector3f(2.01f * TEXEL, 1.99f * TEXEL, 2.01f * TEXEL), new Vector3f(11.95f * TEXEL, 11.98f * TEXEL * height, 11.95f * TEXEL), fluidColor, matrixStack, sprite);
		}
	}
}
