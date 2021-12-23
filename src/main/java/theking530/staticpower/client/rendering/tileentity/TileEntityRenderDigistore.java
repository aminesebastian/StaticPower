package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.utilities.MetricConverter;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderDigistore extends StaticPowerTileEntitySpecialRenderer<TileEntityDigistore> {
	private static final float ICON_SIZE = 0.09f;

	public TileEntityRenderDigistore(BlockEntityRenderDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		if (tileEntity.inventory.getUniqueItemCapacity() > 0) {
			WorldRenderingUtilities.drawFlatItemInWorld(tileEntity, tileEntity.inventory.getDigistoreStack(0).getStoredItem(), new Vector3D(0.5f, 0.57f, 1.01f), new Vector2D(0.4f, 0.4f),
					partialTicks, matrixStack, buffer, 15728880, combinedOverlay);
			drawFillBar(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
			MetricConverter metric = new MetricConverter(tileEntity.inventory.getTotalContainedCount());
			WorldRenderingUtilities.drawTextInWorld(this.renderer, metric.getValueAsString(true), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f),
					new Vector3D(0.5f, 0.323f, 1.0001f), 0.007f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
		drawIndicators(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
	}

	public void drawIndicators(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
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
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_LOCKED_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.815f, 0.0001f),
					new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f), Vector4D.DEFAULT_UV, Color.WHITE);
			offset += ICON_SIZE;
		}
		if (tileEntity.isVoidUpgradeInstalled()) {
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_VOID_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.815f, 0.0001f),
					new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f), Vector4D.DEFAULT_UV, Color.WHITE);
			offset += ICON_SIZE;
		}
	}

	public void drawFillBar(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		float filledRatio = tileEntity.inventory.getFilledRatio();
		if (filledRatio < 1.0f) {
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_FILL_BAR, matrixStack, buffer, new Vector3D(0.293f, 0.192f, 0.047f),
					new Vector3D(0.413f * filledRatio, 0.029f, 1.0f), Vector4D.DEFAULT_UV, Color.WHITE);
		} else {
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_FILL_BAR_FULL, matrixStack, buffer, new Vector3D(0.293f, 0.192f, 0.047f),
					new Vector3D(0.413f * filledRatio, 0.029f, 1.0f), Vector4D.DEFAULT_UV, Color.WHITE);
		}
	}
}
