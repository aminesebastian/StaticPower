package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.api.utilities.Color;
import theking530.api.utilities.Vector3D;
import theking530.staticpower.client.rendering.StaticPowerRendererTextures;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;

@SuppressWarnings("deprecation")
public class TileEntityRenderDigistore extends StaticPowerTileEntitySpecialRenderer<TileEntityDigistore> {
	private static final float ICON_SIZE = 0.1f;

	public TileEntityRenderDigistore(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.getStoredItem().isEmpty()) {
			drawItemInWorld(tileEntity, tileEntity.getStoredItem(), TransformType.GUI, new Vector3f(0.5f, 0.57f, 1.01f), new Vector3f(0.4f, 0.4f, 0.01f), partialTicks,
					matrixStack, buffer, combinedLight, combinedOverlay);
			drawFillBar(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
			drawTextInWorld(Integer.toString(tileEntity.getStoredAmount()), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3f(0.5f, 0.315f, 1.0f), 0.01f, partialTicks, matrixStack,
					buffer, combinedLight, combinedOverlay);
		}
		drawIndicators(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
	}

	public void drawIndicators(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// Calculate the number of icons that need to be drawn.
		int icons = 0;
		if (tileEntity.isLocked()) {
			icons++;
		}
		if (tileEntity.isVoidUpgradeInstalled()) {
			icons++;
		}

		// Calculate the starting offset.
		float offset = -(ICON_SIZE / 2.0f) * icons;

		// Draw each icon and offset by the icon size each time.
		if (tileEntity.isLocked()) {
			drawTexturedQuadLit(StaticPowerRendererTextures.DIGISTORE_LOCKED_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.82f, 0.0001f), new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f),
					Color.WHITE, combinedLight);
			offset += ICON_SIZE;
		}
		if (tileEntity.isVoidUpgradeInstalled()) {
			drawTexturedQuadLit(StaticPowerRendererTextures.DIGISTORE_VOID_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.82f, 0.0001f), new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f),
					Color.WHITE, combinedLight);
			offset += ICON_SIZE;
		}
	}

	public void drawFillBar(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		float filledRatio = tileEntity.getFilledRatio();
		drawTexturedQuad(StaticPowerRendererTextures.DIGISTORE_FILL_BAR, matrixStack, buffer, new Vector3D(0.25f, 0.12f, 0.001f), new Vector3D(0.5f * filledRatio, 0.08f, 1.0f));
	}
}
