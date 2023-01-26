package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.blockentities.digistorenetwork.digistore.BlockEntityDigistore;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.utilities.MetricConverter;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderDigistore extends StaticPowerBlockEntitySpecialRenderer<BlockEntityDigistore> {
	private static final float ICON_SIZE = 0.09f;

	public BlockEntityRenderDigistore(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityDigistore tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Digistore");
		if (tileEntity.inventory.getUniqueItemCapacity() > 0) {
			WorldRenderingUtilities.drawFlatItemInWorld(tileEntity, tileEntity.inventory.getDigistoreStack(0).getStoredItem(), new Vector3D(0.5f, 0.57f, 1.01f),
					new Vector2D(0.4f, 0.4f), partialTicks, matrixStack, buffer, 15728880, combinedOverlay);
			drawFillBar(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
			MetricConverter metric = new MetricConverter(tileEntity.inventory.getTotalContainedCount());
			WorldRenderingUtilities.drawTextInWorld(this.blockRenderer, metric.getValueAsString(true), SDColor.EIGHT_BIT_WHITE, new Vector3D(0.5f, 0.276f, 1.001f), 0.007f, matrixStack,
					buffer, combinedLight, combinedOverlay);
		}
		drawIndicators(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		Minecraft.getInstance().getProfiler().pop();
	}

	public void drawIndicators(BlockEntityDigistore tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
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
					new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f), Vector4D.DEFAULT_UV, SDColor.WHITE);
			offset += ICON_SIZE;
		}
		if (tileEntity.isVoidUpgradeInstalled()) {
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_VOID_INDICATOR, matrixStack, buffer, new Vector3D(0.5f + offset, 0.815f, 0.0001f),
					new Vector3D(ICON_SIZE, ICON_SIZE, 1.0f), Vector4D.DEFAULT_UV, SDColor.WHITE);
			offset += ICON_SIZE;
		}
	}

	public void drawFillBar(BlockEntityDigistore tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		float filledRatio = tileEntity.inventory.getFilledRatio();
		if (filledRatio < 1.0f) {
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_FILL_BAR, matrixStack, buffer, new Vector3D(0.281f, 0.217f, 0.0315f),
					new Vector3D(0.438f * filledRatio, 0.04f, 1.0f), Vector4D.DEFAULT_UV, SDColor.WHITE);
		} else {
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.DIGISTORE_FILL_BAR_FULL, matrixStack, buffer, new Vector3D(0.281f, 0.217f, 0.0315f),
					new Vector3D(0.438f * filledRatio, 0.04f, 1.0f), Vector4D.DEFAULT_UV, SDColor.WHITE);
		}
	}
}
