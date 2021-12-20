package theking530.staticpower.client.rendering.tileentity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

@OnlyIn(Dist.CLIENT)
public abstract class StaticPowerTileEntitySpecialRenderer<T extends TileEntityBase> extends TileEntityRenderer<T> {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerTileEntitySpecialRenderer.class);
	protected static final float TEXEL = (1.0f / 16.0f);
	protected ItemRenderer ItemRenderer;
	protected boolean shouldPreRotateTowardsFacingDirection;

	public StaticPowerTileEntitySpecialRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		shouldPreRotateTowardsFacingDirection = true;
	}

	/**
	 * Performs the proper rotation to ensure the rendering is rotated to follow the
	 * underlying block.
	 */
	@Override
	public void render(T tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		BlockPos tileEntityPos = tileEntity.getPos();
		ItemRenderer = Minecraft.getInstance().getItemRenderer();

		matrixStack.push();

		// Rotate to face the side this tile is facing.
		if (shouldPreRotateTowardsFacingDirection) {
			Direction facing = getFacingDirection(tileEntity);
			WorldRenderingUtilities.rotateMatrixToFaceSide(facing, matrixStack);
		}

		// Draw the tile entity.
		try {
			// Update the rendering values for the components.
			for (AbstractTileEntityComponent comp : tileEntity.getComponents()) {
				comp.updateBeforeRendering(partialTicks);
			}

			// Render the tile entity.
			renderTileEntityBase(tileEntity, tileEntityPos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		} catch (Exception e) {
			LOGGER.error(String.format("An error occured when attempting to draw tile entity base: %1$s.", tileEntity), e);
		}

		matrixStack.pop();
		matrixStack.pop();
		RenderHelper.setupLevelDiffuseLighting(matrixStack.getLast().getMatrix());
		matrixStack.push();
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
	protected abstract void renderTileEntityBase(T tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay);
}
