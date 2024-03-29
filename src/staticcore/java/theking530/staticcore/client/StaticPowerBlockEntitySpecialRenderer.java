package theking530.staticcore.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;

@OnlyIn(Dist.CLIENT)
public abstract class StaticPowerBlockEntitySpecialRenderer<T extends BlockEntityBase> implements BlockEntityRenderer<T> {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerBlockEntitySpecialRenderer.class);
	protected static final float TEXEL = (1.0f / 16.0f);
	
	protected ItemRenderer itemRenderer;
	protected BlockEntityRenderDispatcher blockRenderer;
	protected boolean shouldPreRotateTowardsFacingDirection;

	public StaticPowerBlockEntitySpecialRenderer(BlockEntityRendererProvider.Context context) {
		super();
		blockRenderer = context.getBlockEntityRenderDispatcher();
		shouldPreRotateTowardsFacingDirection = true;
	}

	/**
	 * Performs the proper rotation to ensure the rendering is rotated to follow the
	 * underlying block.
	 */
	@Override
	public void render(T tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLight, int combinedOverlay) {
		
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer");
		
		BlockPos tileEntityPos = tileEntity.getBlockPos();
		itemRenderer = Minecraft.getInstance().getItemRenderer();
		matrixStack.pushPose();

		// Rotate to face the side this tile is facing.
		if (shouldPreRotateTowardsFacingDirection) {
			Direction facing = getFacingDirection(tileEntity);
			WorldRenderingUtilities.rotateMatrixToFaceSide(facing, matrixStack);
		}

		// Draw the tile entity.
		try {
			// Update the rendering values for the components.
			for (AbstractBlockEntityComponent comp : tileEntity.getComponents()) {
				comp.updateBeforeRendering(partialTicks);
			}

			// Render the tile entity.
			renderTileEntityBase(tileEntity, tileEntityPos, partialTicks, matrixStack, buffer, combinedLight,
					combinedOverlay);
		} catch (Exception e) {
			LOGGER.error(String.format("An error occured when attempting to draw tile entity base: %1$s.", tileEntity),
					e);
		}

		matrixStack.popPose();
		matrixStack.popPose();
		Lighting.setupLevel(matrixStack.last().pose());
		matrixStack.pushPose();
		
		Minecraft.getInstance().getProfiler().pop();
	}

	protected Direction getFacingDirection(T tileEntity) {
		return tileEntity.getFacingDirection();
	}

	/**
	 * This method is where the main drawing should occur.
	 * 
	 * @param tileEntity      The {@link TileEntity} being drawn.
	 * @param pos             The position of the {@link TileEntity} .
	 * @param partialTicks    The partial ticks.
	 * @param matrixStack     The matrix stack to draw with.
	 * @param buffer          The buffer type.
	 * @param combinedLight   The combined light at the block this
	 *                        {@link TileEntity} is rendering at.
	 * @param combinedOverlay The combined overlay.
	 */
	protected abstract void renderTileEntityBase(T tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLight, int combinedOverlay);
}
