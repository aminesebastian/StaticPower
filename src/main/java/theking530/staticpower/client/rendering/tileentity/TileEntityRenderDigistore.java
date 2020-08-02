package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Color;
import theking530.common.utilities.Vector2D;
import theking530.common.utilities.Vector3D;
import theking530.common.utilities.Vector4D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;

public class TileEntityRenderDigistore extends StaticPowerTileEntitySpecialRenderer<TileEntityDigistore> {
	private static final float ICON_SIZE = 0.09f;

	public TileEntityRenderDigistore(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (tileEntity.inventory.getUniqueItemCapacity() > 0) {
			drawFlatItemInWorld(tileEntity, tileEntity.inventory.getDigistoreStack(0).getStoredItem(), new Vector3D(0.5f, 0.546f, 1.01f), new Vector2D(0.4f, 0.4f), partialTicks, matrixStack, buffer,
					15728880, combinedOverlay);
			drawFillBar(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
			drawTextInWorld(Integer.toString(tileEntity.inventory.getTotalContainedCount()), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.5f, 0.237f, 1.0f), 0.007f,
					partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
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
			drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_LOCKED_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.815f, 0.0001f), new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f),
					Vector4D.DEFAULT_UV, Color.WHITE);
			offset += ICON_SIZE;
		}
		if (tileEntity.isVoidUpgradeInstalled()) {
			drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_VOID_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.815f, 0.0001f), new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f),
					Vector4D.DEFAULT_UV, Color.WHITE);
			offset += ICON_SIZE;
		}
	}

	public void drawFillBar(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		float filledRatio = tileEntity.inventory.getFilledRatio();
		drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_FILL_BAR, matrixStack, buffer, new Vector3D(0.246f, 0.17f, 0.0004f), new Vector3D(0.506f * filledRatio, 0.08f, 1.0f), Vector4D.DEFAULT_UV,
				Color.WHITE);
	}
}
